package com.timer.workout.data.converter

import androidx.room.TypeConverter
import com.timer.workout.data.model.InclineType

/**
 * InclineType枚举转换器
 */
class InclineTypeConverter {
    @TypeConverter
    fun fromInclineType(type: InclineType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toInclineType(name: String?): InclineType? {
        return name?.let { InclineType.valueOf(it) }
    }
}