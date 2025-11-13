package com.timer.workout.data.dao

import androidx.room.*
import com.timer.workout.data.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

/**
 * 训练计划数据访问对象
 */
@Dao
interface WorkoutPlanDao {

    @Query("SELECT * FROM workout_plan ORDER BY createdAt DESC")
    fun getAllPlans(): Flow<List<WorkoutPlan>>

    @Query("SELECT * FROM workout_plan WHERE id = :id")
    suspend fun getPlanById(id: Long): WorkoutPlan?

    @Query("SELECT * FROM workout_plan WHERE type = :type ORDER BY createdAt DESC")
    fun getPlansByType(type: String): Flow<List<WorkoutPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: WorkoutPlan): Long

    @Update
    suspend fun updatePlan(plan: WorkoutPlan)

    @Delete
    suspend fun deletePlan(plan: WorkoutPlan)

    @Query("DELETE FROM workout_plan WHERE id = :id")
    suspend fun deletePlanById(id: Long)

    @Query("SELECT COUNT(*) FROM workout_plan")
    suspend fun getPlanCount(): Int
}