package com.ashaworker.triage.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisit(visit: VisitEntity)

    @Update
    suspend fun updateVisit(visit: VisitEntity)

    @Query("SELECT * FROM visits WHERE id = :id LIMIT 1")
    suspend fun getVisitById(id: String): VisitEntity?

    @Query("SELECT * FROM visits ORDER BY visitTimestamp DESC")
    fun getAllVisits(): Flow<List<VisitEntity>>

    @Query("SELECT * FROM visits WHERE synced = 0 ORDER BY visitTimestamp ASC")
    suspend fun getUnsyncedVisits(): List<VisitEntity>

    @Query("SELECT * FROM visits WHERE workerId = :workerId ORDER BY visitTimestamp DESC")
    fun getAllVisitsForWorker(workerId: String): Flow<List<VisitEntity>>

    @Query("SELECT COUNT(*) FROM visits WHERE workerId = :workerId AND visitTimestamp >= (strftime('%s','now','start of day','localtime') * 1000) AND visitTimestamp < (strftime('%s','now','start of day','+1 day','localtime') * 1000)")
    suspend fun getVisitCountToday(workerId: String): Int

    @Query("UPDATE visits SET synced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<String>)

    @Query("UPDATE visits SET syncAttempts = syncAttempts + 1 WHERE id IN (:ids)")
    suspend fun incrementSyncAttempts(ids: List<String>)
}
