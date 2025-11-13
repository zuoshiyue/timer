package com.timer.workout.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.timer.workout.data.converter.DateConverter
import com.timer.workout.data.converter.WorkoutStatusConverter
import java.util.Date

/**
 * 训练记录状态枚举
 */
enum class WorkoutStatus {
    COMPLETED,  // 已完成
    CANCELLED   // 已取消
}

/**
 * 训练记录数据模型
 */
@Entity(
    tableName = "workout_record",
    foreignKeys = [ForeignKey(
        entity = WorkoutPlan::class,
        parentColumns = ["id"],
        childColumns = ["planId"],
        onDelete = ForeignKey.SET_NULL
    )]
)
@TypeConverters(DateConverter::class, WorkoutStatusConverter::class)
data class WorkoutRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val planId: Long,
    val planName: String,
    val type: WorkoutType,
    val startTime: Date,
    val endTime: Date? = null,
    val totalDuration: Long? = null,
    val actualDuration: Long? = null,
    val completedStages: Int? = null,
    val completionRate: Float? = null,
    val caloriesBurned: Float? = null,
    val intensity: Int? = null,
    val status: WorkoutStatus = WorkoutStatus.COMPLETED,
    val notes: String? = null
)