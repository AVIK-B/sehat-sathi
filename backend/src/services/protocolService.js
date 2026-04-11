const fs = require("fs");
const path = require("path");
const crypto = require("crypto");

function computeProtocolVersions() {
  const protocolDir = path.join(process.cwd(), "protocols");
  if (!fs.existsSync(protocolDir)) {
    return {};
  }

  const files = fs.readdirSync(protocolDir).filter((f) => f.endsWith(".json"));
  const out = {};
  files.forEach((file) => {
    const content = fs.readFileSync(path.join(protocolDir, file));
    out[file] = crypto.createHash("sha256").update(content).digest("hex");
  });
  return out;
}

module.exports = {
  computeProtocolVersions
};
