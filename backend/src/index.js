const app = require("./app");
const env = require("./config/env");
const prisma = require("./config/prisma");

async function bootstrap() {
  await prisma.$connect();
  app.listen(env.port, () => {
    console.log(`Backend listening on port ${env.port}`);
  });
}

bootstrap().catch((err) => {
  console.error(err);
  process.exit(1);
});
