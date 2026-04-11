const bcrypt = require("bcryptjs");
const { PrismaClient } = require("@prisma/client");

const prisma = new PrismaClient();

async function main() {
  const users = [
    { id: "samuel", name: "Samuel Snow", pin: "samuel234" },
    { id: "john", name: "John Doe", pin: "john123" }
  ];

  for (const user of users) {
    const pinHash = await bcrypt.hash(user.pin, 10);

    await prisma.worker.upsert({
      where: { id: user.id },
      create: {
        id: user.id,
        name: user.name,
        block: "HQ",
        district: "Lucknow",
        role: "SUPERVISOR",
        pinHash
      },
      update: {
        name: user.name,
        block: "HQ",
        district: "Lucknow",
        role: "SUPERVISOR",
        pinHash
      }
    });
  }

  console.log("Login users ready: samuel / samuel234, john / john123");
}

main()
  .catch((err) => {
    console.error(err);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
