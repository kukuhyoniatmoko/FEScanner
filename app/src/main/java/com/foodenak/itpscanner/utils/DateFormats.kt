package com.foodenak.itpscanner.utils

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ITP on 8/15/2015.
 */
object DateFormats {

    private val sFormatPools = ArrayList<DateFormat>()

    private val sShortFormatPools = ArrayList<DateFormat>()

    @SuppressLint("SimpleDateFormat")
    fun format(date: Date): String {
        val result: String
        var dateFormat: DateFormat =
                synchronized (sFormatPools) {
                    if (sFormatPools.isEmpty()) {
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    } else {
                        sFormatPools.removeAt(0)
                    }
                }

        result = dateFormat.format(date)

        synchronized (sFormatPools) {
            if (sFormatPools.size < 5) {
                sFormatPools.add(dateFormat)
            }
        }

        return result
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun parse(date: String): Date {
        val result: Date
        var dateFormat: DateFormat =
                synchronized (sFormatPools) {
                    if (sFormatPools.isEmpty()) {
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    } else {
                        sFormatPools.removeAt(0)
                    }
                }

        try {
            result = dateFormat.parse(date)
            return result
        } finally {
            synchronized (sFormatPools) {
                if (sFormatPools.size < 5) {
                    sFormatPools.add(dateFormat)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun formatShort(date: Date): String {
        val result: String
        var dateFormat: DateFormat =
                synchronized (sShortFormatPools) {
                    if (sShortFormatPools.isEmpty()) {
                        SimpleDateFormat("yyyy-MM-dd")
                    } else {
                        sShortFormatPools.removeAt(0)
                    }
                }

        result = dateFormat.format(date)

        synchronized (sShortFormatPools) {
            if (sShortFormatPools.size < 5) {
                sShortFormatPools.add(dateFormat)
            }
        }

        return result
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun parseShort(date: String): Date {
        val result: Date
        var dateFormat: DateFormat =
                synchronized (sShortFormatPools) {
                    if (sShortFormatPools.isEmpty()) {
                        SimpleDateFormat("yyyy-MM-dd")
                    } else {
                        sShortFormatPools.removeAt(0)
                    }
                }

        try {
            result = dateFormat.parse(date)
            return result
        } finally {
            synchronized (sShortFormatPools) {
                if (sShortFormatPools.size < 5) {
                    sShortFormatPools.add(dateFormat)
                }
            }
        }
    }
}
