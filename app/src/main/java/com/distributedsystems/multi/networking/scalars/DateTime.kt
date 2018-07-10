package com.distributedsystems.multi.networking.scalars

import java.text.SimpleDateFormat
import java.util.*

class DateTime(val value: Date) {

    override fun toString(): String {
        return SimpleDateFormat.getInstance().format(value)
    }
}