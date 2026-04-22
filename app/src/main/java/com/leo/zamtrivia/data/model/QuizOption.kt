package com.leo.zamtrivia.data.model

enum class QuizOption {
    A, B, C, D;

    companion object {
        fun fromRaw(value: String): QuizOption? {
            return entries.firstOrNull { it.name.equals(value.trim(), ignoreCase = true) }
        }
    }
}