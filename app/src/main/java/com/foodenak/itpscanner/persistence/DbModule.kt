package com.foodenak.itpscanner.persistence

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.preference.PreferenceManager
import com.foodenak.itpscanner.BuildConfig
import com.foodenak.itpscanner.persistence.dao.*
import dagger.Module
import dagger.Provides
import de.greenrobot.dao.DbUtils
import de.greenrobot.dao.query.QueryBuilder
import javax.inject.Singleton

/**
 * Created by ITP on 10/8/2015.
 */
@Module
class DbModule {

    @Provides
    @Singleton
    internal fun provideDaoSession(context: Context): DaoSession {
        val applicationContext = context.applicationContext
        val devOpenHelper = OpenHelper(applicationContext, "FEScanner", null)
        val master = DaoMaster(devOpenHelper.writableDatabase)
        if (BuildConfig.DEBUG) {
            QueryBuilder.LOG_SQL = true
            QueryBuilder.LOG_VALUES = true
        }
        val session = master.newSession()

        vacuumDataBase(session, applicationContext)
        return session
    }

    private fun vacuumDataBase(session: DaoSession, context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val PREF = "vacuum-time"
        val nowTime = System.currentTimeMillis()
        val currentVacuumTime = preferences.getLong(PREF, 0)
        if (currentVacuumTime > nowTime) {
            return
        }
        val newVacuumTime = nowTime + (60 * 60 * 24 * 1000 * 7)
        DbUtils.vacuum(session.database)
        preferences.edit().putLong(PREF, newVacuumTime).apply()
    }

    @Provides
    @Singleton
    internal fun provideEventDao(session: DaoSession): EventEntityDao {
        return session.eventEntityDao
    }

    @Provides
    @Singleton
    internal fun provideEventImageDao(session: DaoSession): EventImageEntityDao {
        return session.eventImageEntityDao
    }

    @Provides
    @Singleton
    internal fun provideUserDao(session: DaoSession): UserEntityDao {
        return session.userEntityDao
    }

    @Provides
    @Singleton
    internal fun provideHistoryDao(session: DaoSession): HistoryEntityDao {
        return session.historyEntityDao
    }

    internal class OpenHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?) : DaoMaster.OpenHelper(context, name, factory) {

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            DaoMaster.dropAllTables(db, true)
            onCreate(db)
        }

        override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            DaoMaster.dropAllTables(db, true)
            onCreate(db)
        }
    }
}