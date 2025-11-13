package com.timer.workout.data.converter

import androidx.room.TypeConverter
import com.timer.workout.data.model.WorkoutType

/**
 * WorkoutType枚举转换器
 */
class WorkoutTypeConverter {
    @TypeConverter
    fun fromWorkoutType(type: WorkoutType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toWorkoutType(name: String?): WorkoutType? {
        return name?.let { WorkoutType.valueOf(it) }
    }
}