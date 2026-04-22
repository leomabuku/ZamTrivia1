package com.leo.zamtrivia.data.model

data class QuizSession(
    val playerName: String,
    val selectedCategory: String?,
    val isRandomMode: Boolean
)
