package com.leo.zamtrivia.data.model

data class QuizResult(
    val playerName: String,
    val score: Double,
    val percentage: Double,
    val rank: Int,
    val totalTimeSeconds: Int
)