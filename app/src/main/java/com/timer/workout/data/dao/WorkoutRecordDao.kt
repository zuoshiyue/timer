package com.timer.workout.data.dao

import androidx.room.*
import com.timer.workout.data.model.WorkoutRecord
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * 训练记录数据访问对象
 */
@Dao
interface WorkoutRecordDao {

    @Query("SELECT * FROM workout_record ORDER BY startTime DESC")
    fun getAllRecords(): Flow<List<WorkoutRecord>>

    @Query("SELECT * FROM workout_record WHERE planId = :planId ORDER BY startTime DESC")
    fun getRecordsByPlanId(planId: Long): Flow<List<WorkoutRecord>>

    @Query("SELECT * FROM workout_record WHERE id = :id")
    suspend fun getRecordById(id: Long): WorkoutRecord?

    @Query("SELECT * FROM workout_record WHERE startTime BETWEEN :startDate AND :endDate ORDER BY startTime DESC")
    fun getRecordsByDateRange(startDate: Date, endDate: Date): Flow<List<WorkoutRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: WorkoutRecord): Long

    @Update
    suspend fun updateRecord(record: WorkoutRecord)

    @Delete
    suspend fun deleteRecord(record: WorkoutRecord)

    @Query("DELETE FROM workout_record WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("SELECT COUNT(*) FROM workout_record WHERE planId = :planId")
    suspend fun getRecordCountByPlanId(planId: Long): Int

    @Query("SELECT SUM(totalDuration) FROM workout_record WHERE startTime BETWEEN :startDate AND :endDate AND status = 'COMPLETED'")
    suspend fun getTotalDurationByDateRange(startDate: Date, endDate: Date): Long?
}