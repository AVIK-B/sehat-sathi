const express = require("express");
const prisma = require("../config/prisma");
const { authenticate, requireRole } = require("../middleware/auth");

const router = express.Router();

router.get("/:id/stats", authenticate, requireRole("SUPERVISOR"), async (req, res) => {
  const workerId = req.params.id;

  const visitCount = await prisma.visit.count({ where: { workerId } });
  const referralCount = await prisma.visit.count({
    where: { workerId, referralRequired: true }
  });
  const earnings = await prisma.workerEarning.aggregate({
    where: { workerId },
    _sum: { amountInr: true }
  });

  const referralRate = visitCount === 0 ? 0 : referralCount / visitCount;

  return res.json({
    workerId,
    visitCount,
    referralRate,
    incentiveTotal: earnings._sum.amountInr || 0
  });
});

module.exports = router;
