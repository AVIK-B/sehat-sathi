const express = require("express");
const { computeProtocolVersions } = require("../services/protocolService");

const router = express.Router();

router.get("/version", (_req, res) => {
  return res.json({ versions: computeProtocolVersions() });
});

module.exports = router;
