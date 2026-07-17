const express = require("express");
const bcrypt = require("bcryptjs");
const prisma = require("../config/prisma");
const { signWorkerToken } = require("../utils/jwt");

const router = express.Router();

router.post("/login", async (req, res) => {
  try {
    const { workerId, pin } = req.body;
    if (!workerId || !pin) {
      return res.status(400).json({ message: "workerId and pin are required" });
    }

    // FIX 1: prisma.workers (plural), not prisma.worker
    const worker = await prisma.workers.findUnique({ where: { id: workerId } });
    if (!worker) {
      return res.status(401).json({ message: "Invalid credentials" });
    }

    // FIX 2: worker.pin_hash, not worker.pinHash
    const ok = await bcrypt.compare(pin, worker.pin_hash);
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
  } catch (error) {
    console.error("Login error:", error);
    return res.status(500).json({ message: "Server error", error: error.message });
  }
});

module.exports = router;
