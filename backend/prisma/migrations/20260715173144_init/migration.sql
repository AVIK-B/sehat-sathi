-- CreateEnum
CREATE TYPE "WorkerRole" AS ENUM ('WORKER', 'SUPERVISOR');

-- CreateTable
CREATE TABLE "workers" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "block" TEXT NOT NULL,
    "district" TEXT NOT NULL,
    "pin_hash" TEXT NOT NULL,
    "role" "WorkerRole" NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "workers_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "visits" (
    "id" TEXT NOT NULL,
    "worker_id" TEXT NOT NULL,
    "patient_type" TEXT NOT NULL,
    "patient_age_months" INTEGER,
    "chief_complaint" TEXT NOT NULL,
    "triage_result" TEXT NOT NULL,
    "advice_given" JSONB NOT NULL,
    "referral_required" BOOLEAN NOT NULL,
    "referral_urgency" TEXT NOT NULL,
    "latitude" DOUBLE PRECISION,
    "longitude" DOUBLE PRECISION,
    "visit_timestamp" TIMESTAMP(3) NOT NULL,
    "synced" BOOLEAN NOT NULL DEFAULT true,
    "sync_attempts" INTEGER NOT NULL DEFAULT 0,
    "server_received_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "visits_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "question_responses" (
    "id" TEXT NOT NULL,
    "visit_id" TEXT NOT NULL,
    "question_id" TEXT NOT NULL,
    "question_text" TEXT NOT NULL,
    "response" TEXT NOT NULL,

    CONSTRAINT "question_responses_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "incentive_rules" (
    "id" TEXT NOT NULL,
    "action_type" TEXT NOT NULL,
    "amount_inr" INTEGER NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "incentive_rules_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "worker_earnings" (
    "id" TEXT NOT NULL,
    "worker_id" TEXT NOT NULL,
    "visit_id" TEXT,
    "action_type" TEXT NOT NULL,
    "amount_inr" INTEGER NOT NULL,
    "calculated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "worker_earnings_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE INDEX "visits_worker_id_idx" ON "visits"("worker_id");

-- CreateIndex
CREATE INDEX "visits_visit_timestamp_idx" ON "visits"("visit_timestamp");

-- CreateIndex
CREATE INDEX "question_responses_visit_id_idx" ON "question_responses"("visit_id");

-- CreateIndex
CREATE UNIQUE INDEX "incentive_rules_action_type_key" ON "incentive_rules"("action_type");

-- CreateIndex
CREATE UNIQUE INDEX "worker_earnings_visit_id_key" ON "worker_earnings"("visit_id");

-- CreateIndex
CREATE INDEX "worker_earnings_worker_id_idx" ON "worker_earnings"("worker_id");

-- AddForeignKey
ALTER TABLE "visits" ADD CONSTRAINT "visits_worker_id_fkey" FOREIGN KEY ("worker_id") REFERENCES "workers"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "question_responses" ADD CONSTRAINT "question_responses_visit_id_fkey" FOREIGN KEY ("visit_id") REFERENCES "visits"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "worker_earnings" ADD CONSTRAINT "worker_earnings_worker_id_fkey" FOREIGN KEY ("worker_id") REFERENCES "workers"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "worker_earnings" ADD CONSTRAINT "worker_earnings_visit_id_fkey" FOREIGN KEY ("visit_id") REFERENCES "visits"("id") ON DELETE SET NULL ON UPDATE CASCADE;
