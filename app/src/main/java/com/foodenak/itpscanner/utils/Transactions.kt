package com.foodenak.itpscanner.utils

import android.database.sqlite.SQLiteDatabase

/**
 * Created by kukuh on 16/03/14.
 */
inline fun <T> SQLiteDatabase.executeTransaction(function: () -> T): T {
  return if (isDbLockedByCurrentThread) {
    function()
  } else {
    beginTransaction()
    try {
      val t = function()
      setTransactionSuccessful()
      t
    } finally {
      endTransaction()
    }
  }
}