package com.leo.zamtrivia.data.model

data class QuestionState(
    val questionId: Int,
    val remainingTimeSeconds: Int = 10,
    val attemptsUsed: Int = 0,
    val isSkipped: Boolean = false,
    val hasBeenRequeued: Boolean = false,
    val isCompleted: Boolean = false
)
