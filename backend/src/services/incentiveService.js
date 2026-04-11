const prisma = require("../config/prisma");

function actionForVisit(visit) {
  if (visit.triageResult === "GREEN") return "GREEN_VISIT";
  if (visit.triageResult === "YELLOW" && visit.referralRequired) return "YELLOW_REFERRAL";
  if (visit.triageResult === "RED" && visit.referralRequired) return "RED_EMERGENCY";
  return null;
}

async function calculateIncentiveForVisit(visit) {
  const actionType = actionForVisit(visit);
  if (!actionType) return null;

  const rule = await prisma.incentiveRule.findUnique({ where: { actionType } });
  if (!rule) return null;

  await prisma.workerEarning.upsert({
    where: { visitId: visit.id },
    create: {
      workerId: visit.workerId,
      visitId: visit.id,
      actionType,
      amountInr: rule.amountInr
    },
    update: {
      actionType,
      amountInr: rule.amountInr
    }
  });

  return { actionType, amountInr: rule.amountInr };
}

module.exports = {
  calculateIncentiveForVisit
};
