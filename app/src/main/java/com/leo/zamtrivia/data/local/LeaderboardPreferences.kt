package com.leo.zamtrivia.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leo.zamtrivia.data.model.LeaderboardEntry
import com.leo.zamtrivia.util.Constants

class LeaderboardPreferences(context: Context) {

    private val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getEntries(): List<LeaderboardEntry> {
        val json = prefs.getString(Constants.LEADERBOARD_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<LeaderboardEntry>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun saveEntries(entries: List<LeaderboardEntry>) {
        prefs.edit().putString(Constants.LEADERBOARD_KEY, gson.toJson(entries)).apply()
    }
}