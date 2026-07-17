const express = require("express");
const cors = require("cors");
const helmet = require("helmet");
const morgan = require("morgan");

const authRoutes = require("./routes/authRoutes");
const visitRoutes = require("./routes/visitRoutes");
const workerRoutes = require("./routes/workerRoutes");
const protocolRoutes = require("./routes/protocolRoutes");
const dashboardRoutes = require("./routes/dashboardRoutes");

const app = express();

// 1. Configure Helmet to allow cross-origin resource sharing
app.use(helmet({
  crossOriginResourcePolicy: { policy: "cross-origin" }
}));

// 2. Configure CORS to explicitly allow your frontend domain and credentials
app.use(cors({
  origin: "https://sehat-sathi112.onrender.com", 
  credentials: true,
  methods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
  allowedHeaders: ["Content-Type", "Authorization"]
}));

app.use(express.json({ limit: "2mb" }));
app.use(morgan("dev"));

app.get("/health", (_req, res) => {
  res.json({ ok: true, service: "asha-backend" });
});

app.use("/api/v1/auth", authRoutes);
app.use("/api/v1/visits", visitRoutes);
app.use("/api/v1/workers", workerRoutes);
app.use("/api/v1/protocols", protocolRoutes);
app.use("/api/v1/dashboard", dashboardRoutes);

module.exports = app;
