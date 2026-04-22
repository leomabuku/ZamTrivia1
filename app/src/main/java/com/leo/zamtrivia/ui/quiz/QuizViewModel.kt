package com.leo.zamtrivia.ui.quiz

import android.app.Application
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.leo.zamtrivia.R
import com.leo.zamtrivia.data.model.LeaderboardEntry
import com.leo.zamtrivia.data.model.Question
import com.leo.zamtrivia.data.model.QuizOption
import com.leo.zamtrivia.data.model.QuizResult
import com.leo.zamtrivia.data.repository.LeaderboardRepository
import com.leo.zamtrivia.data.repository.QuizRepository
import com.leo.zamtrivia.util.Constants
import java.util.Locale

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val quizRepository = QuizRepository(application)
    private val leaderboardRepository = LeaderboardRepository(application)

    private var questions: List<Question> = emptyList()
    private val skippedQuestions = mutableListOf<Question>()

    private var isProcessingSkipped = false
    private var answeredCount = 0
    private var skippedIndex = 0
    private var totalScore = 0.0
    private var totalTimeUsed = 0
    private var playerName = ""

    private val _uiState = MutableLiveData<QuizUiState>()
    val uiState: LiveData<QuizUiState> = _uiState

    private val _finishedResult = MutableLiveData<QuizResult?>()
    val finishedResult: LiveData<QuizResult?> = _finishedResult

    private val _retrySkippedPrompt = MutableLiveData<Int?>()
    val retrySkippedPrompt: LiveData<Int?> = _retrySkippedPrompt

    private var currentShuffledOptions = listOf<String>()
    private var currentCorrectOption: QuizOption? = null
    private var currentQuestionState: QuestionQueueState? = null
    private var currentSound: MediaPlayer? = null

    fun startQuiz(name: String, category: String?, isRandom: Boolean) {
        playerName = name
        questions = quizRepository.buildQuizQuestions(category, isRandom)

        skippedQuestions.clear()
        isProcessingSkipped = false
        answeredCount = 0
        skippedIndex = 0
        totalScore = 0.0
        totalTimeUsed = 0

        _retrySkippedPrompt.value = null
        _finishedResult.value = null

        loadNextQuestion()
    }

    private fun loadNextQuestion() {
        if (!isProcessingSkipped) {
            if (answeredCount >= questions.size) {
                if (skippedQuestions.isNotEmpty()) {
                    _retrySkippedPrompt.value = skippedQuestions.size
                } else {
                    finishQuiz()
                }
                return
            }

            val question = questions[answeredCount]
            val state = QuestionQueueState(
                question = question,
                remainingSeconds = Constants.QUESTION_TIME_SECONDS
            )
            currentQuestionState = state
            prepareQuestion(question)
            updateUi(state)
            return
        }

        if (skippedIndex >= skippedQuestions.size) {
            finishQuiz()
            return
        }

        val question = skippedQuestions[skippedIndex]
        val state = QuestionQueueState(
            question = question,
            remainingSeconds = Constants.QUESTION_TIME_SECONDS
        )
        currentQuestionState = state
        prepareQuestion(question)
        updateUi(state)
    }

    private fun prepareQuestion(question: Question) {
        val options = listOf(
            question.optionA,
            question.optionB,
            question.optionC,
            question.optionD
        )

        val correctAnswerText = when (question.correctAnswer.uppercase()) {
            "A" -> question.optionA
            "B" -> question.optionB
            "C" -> question.optionC
            "D" -> question.optionD
            else -> question.optionA
        }

        currentShuffledOptions = options.shuffled()
        val correctIndex = currentShuffledOptions.indexOf(correctAnswerText)

        currentCorrectOption = when (correctIndex) {
            0 -> QuizOption.A
            1 -> QuizOption.B
            2 -> QuizOption.C
            3 -> QuizOption.D
            else -> QuizOption.A
        }
    }

    private fun updateUi(
        state: QuestionQueueState,
        correctOption: QuizOption? = null,
        wrongOption: QuizOption? = null,
        canAnswer: Boolean = true
    ) {
        _uiState.value = buildUiState(
            currentState = state,
            status = if (isProcessingSkipped) {
                getApplication<Application>().getString(R.string.retry_round_status)
            } else {
                ""
            },
            correctOption = correctOption,
            wrongOption = wrongOption,
            canAnswer = canAnswer,
            canSkip = !isProcessingSkipped,
            isReviewing = isProcessingSkipped,
            score = totalScore
        )
    }

    fun submitAnswer(option: QuizOption) {
        val state = currentQuestionState ?: return
        val correctOption = currentCorrectOption ?: return

        if (option == correctOption) {
            totalScore += 1.0
            playSound(R.raw.sound_correct)
            updateUi(state, correctOption, null, false)

            Handler(Looper.getMainLooper()).postDelayed({
                moveForwardAfterAnswer()
            }, 1000)
        } else {
            playSound(R.raw.sound_wrong)
            updateUi(state, correctOption, option, false)

            Handler(Looper.getMainLooper()).postDelayed({
                moveForwardAfterAnswer()
            }, 1500)
        }
    }

    fun skipQuestion() {
        if (isProcessingSkipped) {
            skippedIndex++
        } else {
            currentQuestionState?.let { skippedQuestions.add(it.question) }
            answeredCount++
        }
        loadNextQuestion()
    }

    fun tickSecond() {
        val state = currentQuestionState ?: return
        if (_uiState.value?.canAnswer == false) return

        totalTimeUsed++
        val newRemaining = state.remainingSeconds - 1

        if (newRemaining <= 0) {
            playSound(R.raw.sound_timeout)
            skipQuestion()
        } else {
            val newState = state.copy(remainingSeconds = newRemaining)
            currentQuestionState = newState
            updateUi(newState)
        }
    }

    fun startSkippedReview() {
        if (skippedQuestions.isEmpty()) {
            finishQuiz()
            return
        }

        isProcessingSkipped = true
        skippedIndex = 0
        loadNextQuestion()
    }

    fun declineSkippedReview() {
        finishQuiz()
    }

    fun consumeRetrySkippedPrompt() {
        _retrySkippedPrompt.value = null
    }

    fun consumeFinishedResult() {
        _finishedResult.value = null
    }

    private fun moveForwardAfterAnswer() {
        if (isProcessingSkipped) {
            skippedIndex++
        } else {
            answeredCount++
        }
        loadNextQuestion()
    }

    private fun finishQuiz() {
        playSound(R.raw.sound_result)

        val entry = LeaderboardEntry(
            playerName = playerName,
            score = totalScore,
            totalTimeSeconds = totalTimeUsed,
            timestamp = System.currentTimeMillis()
        )

        val rank = leaderboardRepository.addEntry(entry)
        val percentage = (totalScore / questions.size.coerceAtLeast(1)) * 100.0

        _finishedResult.value = QuizResult(
            playerName = playerName,
            score = totalScore,
            percentage = percentage,
            rank = rank,
            totalTimeSeconds = totalTimeUsed
        )
    }

    private fun buildUiState(
        currentState: QuestionQueueState,
        status: String,
        correctOption: QuizOption?,
        wrongOption: QuizOption?,
        canAnswer: Boolean,
        canSkip: Boolean,
        isReviewing: Boolean = false,
        reviewCountdown: Int = 0,
        score: Double
    ): QuizUiState {
        val context = getApplication<Application>()

        val questionNumberText = if (isProcessingSkipped) {
            context.getString(
                R.string.retry_question_number_format,
                skippedIndex + 1,
                skippedQuestions.size
            )
        } else {
            context.getString(
                R.string.question_number_format,
                answeredCount + 1
            )
        }

        val progressValue = if (isProcessingSkipped) {
            skippedIndex + 1
        } else {
            answeredCount + 1
        }

        val totalQuestionsValue = if (isProcessingSkipped) {
            skippedQuestions.size.coerceAtLeast(1)
        } else {
            questions.size.coerceAtLeast(1)
        }

        return QuizUiState(
            playerName = playerName,
            score = score,
            scoreText = formatScore(score),
            progress = progressValue,
            questionNumber = if (isProcessingSkipped) skippedIndex + 1 else answeredCount + 1,
            questionNumberText = questionNumberText,
            totalQuestions = totalQuestionsValue,
            timerText = currentState.remainingSeconds.toString(),
            statusText = status,
            questionText = currentState.question.questionText,
            optionA = currentShuffledOptions.getOrNull(0) ?: "",
            optionB = currentShuffledOptions.getOrNull(1) ?: "",
            optionC = currentShuffledOptions.getOrNull(2) ?: "",
            optionD = currentShuffledOptions.getOrNull(3) ?: "",
            correctOption = correctOption,
            wrongSelectedOption = wrongOption,
            canAnswer = canAnswer,
            canSkip = canSkip,
            isReviewing = isReviewing,
            reviewCountdownText = if (isReviewing && reviewCountdown > 0) {
                reviewCountdown.toString()
            } else {
                ""
            }
        )
    }

    private fun formatScore(score: Double): String {
        return if (score % 1.0 == 0.0) {
            score.toInt().toString()
        } else {
            String.format(Locale.US, "%.1f", score)
        }
    }

    private fun playSound(soundRes: Int) {
        currentSound?.release()
        currentSound = MediaPlayer.create(getApplication(), soundRes)
        currentSound?.setOnCompletionListener {
            it.release()
            if (currentSound === it) currentSound = null
        }
        currentSound?.start()
    }

    override fun onCleared() {
        super.onCleared()
        currentSound?.release()
        currentSound = null
    }
}

data class QuestionQueueState(
    val question: Question,
    val remainingSeconds: Int
)

data class QuizUiState(
    val playerName: String,
    val score: Double,
    val scoreText: String,
    val progress: Int,
    val questionNumber: Int,
    val questionNumberText: String,
    val totalQuestions: Int,
    val timerText: String,
    val statusText: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: QuizOption?,
    val wrongSelectedOption: QuizOption?,
    val canAnswer: Boolean,
    val canSkip: Boolean,
    val isReviewing: Boolean,
    val reviewCountdownText: String
)