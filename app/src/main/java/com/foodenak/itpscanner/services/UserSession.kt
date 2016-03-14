package com.foodenak.itpscanner.services

import android.content.Context
import android.preference.PreferenceManager
import com.foodenak.itpscanner.entities.User

/**
 * Created by ITP on 10/7/2015.
 */
data class UserSession(val id: String, val token: String) {

    var user: User? = null

    fun isActive(): Boolean {
        return id.length > 0 && token.length > 0
    }

    companion object {
        var currentSession: UserSession = UserSession("", "")

        const val USER_HASH_ID = "user-hash-id"

        const val USER_ACCESS_TOKEN = "user-access-token"

        fun initialize(context: Context) {
            currentSession = createFromPreference(context)
        }

        fun initialize(session: UserSession, context: Context) {
            currentSession = session;
            saveToPreference(session, context)
        }

        private fun saveToPreference(session: UserSession, context: Context) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            preferences.edit()
                    .putString(USER_HASH_ID, session.id)
                    .putString(USER_ACCESS_TOKEN, session.token)
                    .apply()
        }

        private fun createFromPreference(context: Context): UserSession {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val id = preferences.getString(USER_HASH_ID, "")
            val token = preferences.getString(USER_ACCESS_TOKEN, "")
            return UserSession(id, token)
        }
    }
}