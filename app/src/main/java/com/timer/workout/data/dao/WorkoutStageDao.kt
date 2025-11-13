package com.timer.workout.data.dao

import androidx.room.*
import com.timer.workout.data.model.WorkoutStage

/**
 * 训练阶段数据访问对象
 */
@Dao
interface WorkoutStageDao {

    @Query("SELECT * FROM workout_stage WHERE planId = :planId ORDER BY stageOrder ASC")
    suspend fun getStagesByPlanId(planId: Long): List<WorkoutStage>

    @Query("SELECT * FROM workout_stage WHERE id = :id")
    suspend fun getStageById(id: Long): WorkoutStage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStage(stage: WorkoutStage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStages(stages: List<WorkoutStage>)

    @Update
    suspend fun updateStage(stage: WorkoutStage)

    @Delete
    suspend fun deleteStage(stage: WorkoutStage)

    @Query("DELETE FROM workout_stage WHERE planId = :planId")
    suspend fun deleteStagesByPlanId(planId: Long)

    @Query("DELETE FROM workout_stage WHERE id = :id")
    suspend fun deleteStageById(id: Long)

    @Query("SELECT COUNT(*) FROM workout_stage WHERE planId = :planId")
    suspend fun getStageCountByPlanId(planId: Long): Int
}