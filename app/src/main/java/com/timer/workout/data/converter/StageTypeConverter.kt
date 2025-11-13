package com.timer.workout.data.converter

import androidx.room.TypeConverter
import com.timer.workout.data.model.StageType

/**
 * StageType枚举转换器
 */
class StageTypeConverter {
    @TypeConverter
    fun fromStageType(type: StageType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toStageType(name: String?): StageType? {
        return name?.let { StageType.valueOf(it) }
    }
}