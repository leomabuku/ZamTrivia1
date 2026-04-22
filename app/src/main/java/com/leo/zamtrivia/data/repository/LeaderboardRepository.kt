package com.leo.zamtrivia.data.repository

import android.content.Context
import com.leo.zamtrivia.data.local.LeaderboardPreferences
import com.leo.zamtrivia.data.model.LeaderboardEntry
import com.leo.zamtrivia.util.Constants

class LeaderboardRepository(context: Context) {

    private val prefs = LeaderboardPreferences(context)

    fun getLeaderboard(): List<LeaderboardEntry> {
        return prefs.getEntries().sortedWith(compareByDescending<LeaderboardEntry> { it.score }
            .thenBy { it.totalTimeSeconds }
            .thenBy { it.timestamp })
            .take(Constants.LEADERBOARD_LIMIT)
    }

    fun addEntry(entry: LeaderboardEntry): Int {
        val updated = (prefs.getEntries() + entry)
            .sortedWith(compareByDescending<LeaderboardEntry> { it.score }
                .thenBy { it.totalTimeSeconds }
                .thenBy { it.timestamp })
            .take(Constants.LEADERBOARD_LIMIT)

        prefs.saveEntries(updated)
        return updated.indexOfFirst {
            it.playerName == entry.playerName &&
                    it.score == entry.score &&
                    it.totalTimeSeconds == entry.totalTimeSeconds &&
                    it.timestamp == entry.timestamp
        } + 1
    }
}