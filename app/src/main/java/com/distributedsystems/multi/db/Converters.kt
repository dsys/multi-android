package com.distributedsystems.multi.db

import android.arch.persistence.room.TypeConverter
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(ts: Long) : Date {
        return Date(ts)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date) : Long {
        return date.time
    }
}