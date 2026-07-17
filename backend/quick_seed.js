const bcrypt = require("bcryptjs");
const { PrismaClient } = require("@prisma/client");

const prisma = new PrismaClient();

async function main() {
  const pinHash = await bcrypt.hash("samuel234", 10);

  await prisma.worker.upsert({
    where: { id: "samuel" },
    create: {
      id: "samuel",
      name: "Samuel Snow",
      block: "HQ",
      district: "Lucknow",
      role: "SUPERVISOR",
      pinHash
    },
    update: {
      pinHash
    }
  });

  console.log("User ready");
}

main()
  .catch((err) => {
    console.error(err);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });