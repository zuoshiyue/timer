package com.timer.workout.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.timer.workout.data.converter.InclineTypeConverter
import com.timer.workout.data.converter.StageTypeConverter

/**
 * 训练阶段数据模型
 */
@Entity(
    tableName = "workout_stage",
    foreignKeys = [ForeignKey(
        entity = WorkoutPlan::class,
        parentColumns = ["id"],
        childColumns = ["planId"],
        onDelete = ForeignKey.CASCADE
    )]
)
@TypeConverters(StageTypeConverter::class, InclineTypeConverter::class)
data class WorkoutStage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val planId: Long,
    val stageOrder: Int,
    val name: String,
    val duration: Int,
    val stageType: StageType,

    // 训练参数
    val inclineType: InclineType? = null,
    val inclineStart: Float? = null,
    val inclineEnd: Float? = null,
    val speedStart: Float? = null,
    val speedEnd: Float? = null,
    val heartRateMin: Int? = null,
    val heartRateMax: Int? = null,

    // 循环设置
    val cycles: Int = 1,
    val workDuration: Int? = null,
    val restDuration: Int? = null,

    // 描述信息
    val description: String? = null,
    val tips: String? = null
)