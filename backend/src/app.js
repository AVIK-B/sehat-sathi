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

// 1. Helmet (keep it, but don't let it block CORS preflights)
app.use(helmet());

// 2. Explicit CORS whitelist — REQUIRED when credentials: true
const allowedOrigins = [
  "https://sehat-sathi112.onrender.com",   // your deployed frontend
  "http://localhost:5173",                  // Vite dev server (adjust if different)
  "http://localhost:3000",                  // React dev server (adjust if different)
];

app.use(cors({
  origin: function (origin, callback) {
    // allow requests with no origin (e.g. mobile apps, Postman, curl)
    if (!origin) return callback(null, true);
    if (allowedOrigins.includes(origin)) {
      return callback(null, true);
    }
    return callback(new Error("Not allowed by CORS"));
  },
  credentials: true,
  methods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
  allowedHeaders: ["Content-Type", "Authorization"],
}));

// Handle preflight explicitly for all routes (safety net for Render/Proxies)
app.options("*", cors());

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
