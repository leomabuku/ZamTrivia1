package com.leo.zamtrivia.data.model

data class Question(
    val id: Int,
    val category: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String
)