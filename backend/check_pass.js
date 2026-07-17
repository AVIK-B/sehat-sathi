const { PrismaClient } = require("@prisma/client");
const bcrypt = require("bcryptjs");
const p = new PrismaClient();
(async () => {
  const w = await p.worker.findUnique({ where: { id: "samuel" } });
  if (!w) { console.log("no samuel"); await p.$disconnect(); return; }
  for (const pin of ["samuel234", "1234", "john123"]) {
    const ok = await bcrypt.compare(pin, w.pinHash);
    console.log("pin", pin, "->", ok);
  }
  await p.$disconnect();
})();