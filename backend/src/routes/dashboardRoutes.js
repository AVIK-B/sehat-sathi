const express = require("express");
const dayjs = require("dayjs");
const prisma = require("../config/prisma");
const { authenticate, requireRole } = require("../middleware/auth");

const router = express.Router();

router.get("/summary", authenticate, requireRole("SUPERVISOR"), async (_req, res) => {
  const start = dayjs().startOf("day").toDate();
  const end = dayjs().endOf("day").toDate();

  const [totalVisitsToday, totalReferralsToday, workers, byResult] = await Promise.all([
    prisma.visit.count({ where: { visitTimestamp: { gte: start, lte: end } } }),
    prisma.visit.count({ where: { visitTimestamp: { gte: start, lte: end }, referralRequired: true } }),
    prisma.worker.findMany({ where: { role: "WORKER" }, select: { id: true, name: true, block: true, district: true } }),
    prisma.visit.groupBy({
      by: ["triageResult"],
      where: { visitTimestamp: { gte: start, lte: end } },
      _count: { _all: true }
    })
  ]);

  const workerSummaries = await Promise.all(
    workers.map(async (w) => {
      const lastVisit = await prisma.visit.findFirst({
        where: { workerId: w.id },
        orderBy: { visitTimestamp: "desc" }
      });
      const visitCountToday = await prisma.visit.count({
        where: { workerId: w.id, visitTimestamp: { gte: start, lte: end } }
      });
      return {
        ...w,
        visitCountToday,
        lastSyncedAt: lastVisit?.serverReceivedAt || null
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
});

module.exports = router;
