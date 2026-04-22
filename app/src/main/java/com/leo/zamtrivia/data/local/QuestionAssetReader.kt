package com.leo.zamtrivia.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leo.zamtrivia.data.model.Question

class QuestionAssetReader(private val context: Context) {

    fun readQuestions(): List<Question> {
        val json = context.assets.open("questions.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Question>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }
}