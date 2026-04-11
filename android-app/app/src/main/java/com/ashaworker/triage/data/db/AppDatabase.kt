package com.ashaworker.triage.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [VisitEntity::class, QuestionResponseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun visitDao(): VisitDao
    abstract fun questionResponseDao(): QuestionResponseDao
}
