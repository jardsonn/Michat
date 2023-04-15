package com.jalloft.michat.data.room

import java.util.*
import androidx.room.TypeConverter

object DateTimeTypeConverters {

    @TypeConverter
    @JvmStatic
    fun toDate(value: Long?): Date? {
        return value?.let { Date(value) }
    }

    @TypeConverter
    @JvmStatic
    fun fromDate(value: Date?): Long? {
        return value?.time
    }

}