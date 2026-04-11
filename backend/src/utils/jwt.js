const jwt = require("jsonwebtoken");
const env = require("../config/env");

function signWorkerToken(worker) {
  return jwt.sign(
    {
      sub: worker.id,
      workerId: worker.id,
      role: worker.role,
      name: worker.name
    },
    env.jwtSecret,
    { expiresIn: "7d" }
  );
}

module.exports = {
  signWorkerToken
};
