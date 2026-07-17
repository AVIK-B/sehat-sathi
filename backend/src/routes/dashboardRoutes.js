const express = require("express");
const dayjs = require("dayjs");
const prisma = require("../config/prisma");
const { authenticate, requireRole } = require("../middleware/auth");

const router = express.Router();

router.get("/summary", authenticate, requireRole("SUPERVISOR"), async (_req, res) => {
  try {
    const start = dayjs().startOf("day").toDate();
    const end = dayjs().endOf("day").toDate();

    const [totalVisitsToday, totalReferralsToday, workers, byResult] = await Promise.all([
      // FIX: prisma.visits (not visit), visit_timestamp (not visitTimestamp)
      prisma.visits.count({ where: { visit_timestamp: { gte: start, lte: end } } }),
      
      // FIX: referral_required (not referralRequired)
      prisma.visits.count({ where: { visit_timestamp: { gte: start, lte: end }, referral_required: true } }),
      
      // FIX: prisma.workers (not worker)
      prisma.workers.findMany({ where: { role: "WORKER" }, select: { id: true, name: true, block: true, district: true } }),
      
      // FIX: triage_result (not triageResult)
      prisma.visits.groupBy({
        by: ["triage_result"],
        where: { visit_timestamp: { gte: start, lte: end } },
        _count: { _all: true }
      })
    ]);

    const workerSummaries = await Promise.all(
      workers.map(async (w) => {
        // FIX: worker_id (not workerId), visit_timestamp (not visitTimestamp)
        const lastVisit = await prisma.visits.findFirst({
          where: { worker_id: w.id },
          orderBy: { visit_timestamp: "desc" }
        });
        
        // FIX: worker_id, visit_timestamp
        const visitCountToday = await prisma.visits.count({
          where: { worker_id: w.id, visit_timestamp: { gte: start, lte: end } }
        });
        
        return {
          ...w,
          visitCountToday,
          // FIX: server_received_at (not serverReceivedAt)
          lastSyncedAt: lastVisit?.server_received_at || null
        };
      })
    );

    return res.json({
      totalVisitsToday,
      totalReferralsToday,
      referralRate: totalVisitsToday === 0 ? 0 : totalReferralsToday / totalVisitsToday,
      byResult,
      workers: workerSummaries
    });
  } catch (error) {
    console.error("Dashboard error:", error);
    return res.status(500).json({ message: "Server error", error: error.message });
  }
});

module.exports = router;
