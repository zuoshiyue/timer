package com.timer.workout.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.timer.workout.data.converter.DateConverter
import com.timer.workout.data.converter.WorkoutTypeConverter
import java.util.Date

/**
 * 训练计划数据模型
 */
@Entity(tableName = "workout_plan")
@TypeConverters(DateConverter::class, WorkoutTypeConverter::class)
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    val description: String? = null,
    val totalDuration: Int,
    val type: WorkoutType = WorkoutType.SIMPLE,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val createdAt: Date = Date()
)