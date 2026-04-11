const express = require("express");
const bcrypt = require("bcryptjs");
const prisma = require("../config/prisma");
const { signWorkerToken } = require("../utils/jwt");

const router = express.Router();

router.post("/login", async (req, res) => {
  const { workerId, pin } = req.body;
  if (!workerId || !pin) {
    return res.status(400).json({ message: "workerId and pin are required" });
  }

  const worker = await prisma.worker.findUnique({ where: { id: workerId } });
  if (!worker) {
    return res.status(401).json({ message: "Invalid credentials" });
  }

  const ok = await bcrypt.compare(pin, worker.pinHash);
  if (!ok) {
    return res.status(401).json({ message: "Invalid credentials" });
  }

  const token = signWorkerToken(worker);
  return res.json({
    token,
    workerId: worker.id,
    name: worker.name,
    block: worker.block,
    district: worker.district,
    role: worker.role
  });
});

module.exports = router;
