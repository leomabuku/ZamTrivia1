package com.leo.zamtrivia.data.model

data class LeaderboardEntry(
    val playerName: String,
    val score: Double,
    val totalTimeSeconds: Int,
    val timestamp: Long
)