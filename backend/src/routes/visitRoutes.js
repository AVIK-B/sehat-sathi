const express = require("express");
const prisma = require("../config/prisma");
const { authenticate, requireRole } = require("../middleware/auth");
const { calculateIncentiveForVisit } = require("../services/incentiveService");

const router = express.Router();

router.post("/batch", authenticate, async (req, res) => {
  const visits = Array.isArray(req.body) ? req.body : [];
  if (visits.length === 0) {
    return res.status(400).json({ message: "Visits array required" });
  }

  const summary = {
    processed: 0,
    incentives: 0
  };

  for (const incoming of visits) {
    const visit = await prisma.visit.upsert({
      where: { id: incoming.id },
      create: {
        id: incoming.id,
        workerId: incoming.workerId,
        patientType: incoming.patientType,
        patientAgeMonths: incoming.patientAgeMonths,
        chiefComplaint: incoming.chiefComplaint,
        triageResult: incoming.triageResult,
        adviceGiven: incoming.adviceGiven || [],
        referralRequired: Boolean(incoming.referralRequired),
        referralUrgency: incoming.referralUrgency || "NONE",
        latitude: incoming.latitude,
        longitude: incoming.longitude,
        visitTimestamp: new Date(incoming.visitTimestamp),
        synced: true,
        syncAttempts: 0
      },
      update: {
        patientType: incoming.patientType,
        patientAgeMonths: incoming.patientAgeMonths,
        chiefComplaint: incoming.chiefComplaint,
        triageResult: incoming.triageResult,
        adviceGiven: incoming.adviceGiven || [],
        referralRequired: Boolean(incoming.referralRequired),
        referralUrgency: incoming.referralUrgency || "NONE",
        latitude: incoming.latitude,
        longitude: incoming.longitude,
        visitTimestamp: new Date(incoming.visitTimestamp),
        synced: true
      }
    });

    await prisma.questionResponse.deleteMany({ where: { visitId: visit.id } });
    const responses = Array.isArray(incoming.questionResponses) ? incoming.questionResponses : [];
    if (responses.length > 0) {
      await prisma.questionResponse.createMany({
        data: responses.map((r) => ({
          visitId: visit.id,
          questionId: r.questionId,
          questionText: r.questionText || r.questionId,
          response: r.response
        }))
      });
    }

    const incentive = await calculateIncentiveForVisit(visit);
    if (incentive) summary.incentives += incentive.amountInr;

    summary.processed += 1;
  }

  return res.json(summary);
});

router.get("/", authenticate, requireRole("SUPERVISOR"), async (req, res) => {
  const { workerId, from, to } = req.query;
  const where = {};
  if (workerId) where.workerId = String(workerId);
  if (from || to) {
    where.visitTimestamp = {};
    if (from) where.visitTimestamp.gte = new Date(String(from));
    if (to) where.visitTimestamp.lte = new Date(String(to));
  }

  const visits = await prisma.visit.findMany({
    where,
    include: {
      worker: true,
      questionResponses: true
    },
    orderBy: { visitTimestamp: "desc" }
  });

  return res.json(visits);
});

module.exports = router;
