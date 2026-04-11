const bcrypt = require("bcryptjs");
const dayjs = require("dayjs");
const { v4: uuidv4 } = require("uuid");
const { PrismaClient } = require("@prisma/client");

const prisma = new PrismaClient();

const workers = [
  { id: "w001", name: "Sita Devi", block: "Chinhat", district: "Lucknow", role: "WORKER", pin: "1234" },
  { id: "w002", name: "Kiran Kumari", block: "Biraul", district: "Darbhanga", role: "WORKER", pin: "1234" },
  { id: "w003", name: "Meena Bai", block: "Pithora", district: "Mahasamund", role: "WORKER", pin: "1234" },
  { id: "sup001", name: "Supervisor Anil", block: "HQ", district: "Lucknow", role: "SUPERVISOR", pin: "1234" },
  { id: "samuel", name: "Samuel Snow", block: "HQ", district: "Lucknow", role: "SUPERVISOR", pin: "samuel234" },
  { id: "john", name: "John Doe", block: "HQ", district: "Lucknow", role: "SUPERVISOR", pin: "john123" }
];

async function main() {
  for (const w of workers) {
    const pinHash = await bcrypt.hash(w.pin, 10);
    const { pin, ...workerData } = w;

    await prisma.worker.upsert({
      where: { id: workerData.id },
      create: { ...workerData, pinHash },
      update: {
        name: workerData.name,
        block: workerData.block,
        district: workerData.district,
        role: workerData.role,
        pinHash
      }
    });
  }

  const rules = [
    { actionType: "GREEN_VISIT", amountInr: 10 },
    { actionType: "YELLOW_REFERRAL", amountInr: 25 },
    { actionType: "RED_EMERGENCY", amountInr: 50 }
  ];

  for (const r of rules) {
    await prisma.incentiveRule.upsert({
      where: { actionType: r.actionType },
      create: r,
      update: { amountInr: r.amountInr }
    });
  }

  const workerIds = workers.filter((w) => w.role === "WORKER").map((w) => w.id);
  const results = ["GREEN", "YELLOW", "RED"];

  for (let d = 0; d < 90; d += 1) {
    const day = dayjs().subtract(d, "day").hour(10).minute(0).second(0);
    for (const workerId of workerIds) {
      const visitsToday = 2 + Math.floor(Math.random() * 4);
      for (let i = 0; i < visitsToday; i += 1) {
        const triageResult = results[Math.floor(Math.random() * results.length)];
        const referralRequired = triageResult !== "GREEN";
        const urgency = triageResult === "RED" ? "IMMEDIATE" : triageResult === "YELLOW" ? "WITHIN_24H" : "NONE";
        const id = uuidv4();

        await prisma.visit.create({
          data: {
            id,
            workerId,
            patientType: "CHILD",
            patientAgeMonths: 24,
            chiefComplaint: "fever",
            triageResult,
            adviceGiven: ["Hydration", "Follow protocol"],
            referralRequired,
            referralUrgency: urgency,
            latitude: 26.85 + Math.random() / 100,
            longitude: 80.94 + Math.random() / 100,
            visitTimestamp: day.add(i, "hour").toDate(),
            synced: true,
            syncAttempts: 0
          }
        });

        await prisma.questionResponse.createMany({
          data: [
            {
              visitId: id,
              questionId: "q1",
              questionText: "Sample question",
              response: "YES"
            },
            {
              visitId: id,
              questionId: "q2",
              questionText: "Sample question 2",
              response: "NO"
            }
          ]
        });

        let amount = 10;
        let actionType = "GREEN_VISIT";
        if (triageResult === "YELLOW") {
          amount = 25;
          actionType = "YELLOW_REFERRAL";
        }
        if (triageResult === "RED") {
          amount = 50;
          actionType = "RED_EMERGENCY";
        }

        await prisma.workerEarning.create({
          data: {
            workerId,
            visitId: id,
            actionType,
            amountInr: amount
          }
        });
      }
    }
  }

  console.log("Seed complete");
}

main()
  .catch((err) => {
    console.error(err);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
