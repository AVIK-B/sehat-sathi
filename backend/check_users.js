const { PrismaClient } = require("@prisma/client");
const p = new PrismaClient();
p.worker.findMany().then((ws) => {
  console.log(JSON.stringify(ws.map((w) => ({ id: w.id, role: w.role, pinHash: w.pinHash ? w.pinHash.substring(0, 12) : null })), null, 2));
  p.$disconnect();
}).catch((e) => {
  console.error(e.message);
  p.$disconnect();
});