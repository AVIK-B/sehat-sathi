const dotenv = require("dotenv");

dotenv.config();

module.exports = {
  port: Number(process.env.PORT || 8080),
  // TODO: Replace dev secret with centrally managed secret in production deployment.
  jwtSecret: process.env.JWT_SECRET || "dev-secret",
  databaseUrl: process.env.DATABASE_URL || ""
};
