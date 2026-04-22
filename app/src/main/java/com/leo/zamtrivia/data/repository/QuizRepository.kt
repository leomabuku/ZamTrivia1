package com.leo.zamtrivia.data.repository

import android.content.Context
import com.leo.zamtrivia.R
import com.leo.zamtrivia.data.local.QuestionAssetReader
import com.leo.zamtrivia.data.model.Category
import com.leo.zamtrivia.data.model.Question
import com.leo.zamtrivia.util.Constants
import kotlin.random.Random

class QuizRepository(context: Context) {

    private val reader = QuestionAssetReader(context)
    private val allQuestions: List<Question> by lazy { reader.readQuestions() }

    fun getCategories(): List<Category> {
        return listOf(
            Category(1, "Tourism", "Landmarks, destinations, and natural attractions.", "ic_category_tourism"),
            Category(2, "Pop Culture", "Music, media, and notable public figures.", "ic_category_pop"),
            Category(3, "History", "Important events, timelines, and national milestones.", "ic_category_history"),
            Category(4, "Sports", "Teams, athletes, and major sporting moments.", "ic_category_sports"),
            Category(5, "Current Affairs", "Recent Zambia-focused public affairs in the packaged question bank.", "ic_category_current"),
            Category(6, "Geography", "Provinces, cities, rivers, borders, and physical features.", "ic_category_geography"),
            Category(7, "Politics & Government", "Institutions, leadership, governance, and civics.", "ic_category_politics"),
            Category(8, "Education", "Schools, universities, and education-related knowledge.", "ic_category_education"),
            Category(9, "National Symbols", "Flags, anthem, coat of arms, and national identity.", "ic_category_symbols"),
            Category(10, "Languages & Culture", "Languages, traditions, ceremonies, and cultural life.", "ic_category_culture")
        )
    }

    fun buildQuizQuestions(selectedCategory: String?, isRandomMode: Boolean): List<Question> {
        val source = if (isRandomMode || selectedCategory.isNullOrBlank()) {
            allQuestions
        } else {
            allQuestions.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }

        require(source.isNotEmpty()) { "No questions available for the selected mode." }

        return source.shuffled(Random(System.currentTimeMillis()))
            .take(Constants.QUIZ_QUESTION_COUNT)
    }

    fun countForCategory(name: String): Int {
        return allQuestions.count { it.category.equals(name, ignoreCase = true) }
    }
}