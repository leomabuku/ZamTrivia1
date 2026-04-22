package com.leo.zamtrivia.ui.quiz

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.leo.zamtrivia.R
import com.leo.zamtrivia.data.model.QuizOption
import com.leo.zamtrivia.databinding.FragmentQuizBinding

class QuizFragment : Fragment(R.layout.fragment_quiz) {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizViewModel by viewModels()
    private val args: QuizFragmentArgs by navArgs()

    private var timer: CountDownTimer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentQuizBinding.bind(view)

        viewModel.startQuiz(args.playerName, args.categoryName, args.isRandomMode)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.textQuestion.text = state.questionText
            binding.textScoreValue.text = state.scoreText
            binding.textTimer.text = state.timerText
            binding.textQuestionNumber.text = state.questionNumberText
            binding.progressBar.progress = state.progress
            binding.progressBar.max = state.totalQuestions

            binding.textOptionAText.text = state.optionA
            binding.textOptionBText.text = state.optionB
            binding.textOptionCText.text = state.optionC
            binding.textOptionDText.text = state.optionD

            binding.textStatus.text = state.statusText

            if (state.reviewCountdownText.isNotBlank()) {
                binding.textNextCountdown.visibility = View.VISIBLE
                binding.textNextCountdown.text = state.reviewCountdownText
            } else {
                binding.textNextCountdown.visibility = View.GONE
                binding.textNextCountdown.text = ""
            }

            binding.textOptionALabel.text =
                getOptionLabel("A", state.wrongSelectedOption == QuizOption.A)
            binding.textOptionBLabel.text =
                getOptionLabel("B", state.wrongSelectedOption == QuizOption.B)
            binding.textOptionCLabel.text =
                getOptionLabel("C", state.wrongSelectedOption == QuizOption.C)
            binding.textOptionDLabel.text =
                getOptionLabel("D", state.wrongSelectedOption == QuizOption.D)

            styleOption(
                binding.cardOptionA,
                binding.textOptionAText,
                binding.textOptionALabel,
                state.correctOption == QuizOption.A,
                state.wrongSelectedOption == QuizOption.A,
                state.canAnswer
            )
            styleOption(
                binding.cardOptionB,
                binding.textOptionBText,
                binding.textOptionBLabel,
                state.correctOption == QuizOption.B,
                state.wrongSelectedOption == QuizOption.B,
                state.canAnswer
            )
            styleOption(
                binding.cardOptionC,
                binding.textOptionCText,
                binding.textOptionCLabel,
                state.correctOption == QuizOption.C,
                state.wrongSelectedOption == QuizOption.C,
                state.canAnswer
            )
            styleOption(
                binding.cardOptionD,
                binding.textOptionDText,
                binding.textOptionDLabel,
                state.correctOption == QuizOption.D,
                state.wrongSelectedOption == QuizOption.D,
                state.canAnswer
            )

            binding.btnSkip.isEnabled = state.canSkip
            binding.btnSkip.alpha = if (state.canSkip) 1.0f else 0.5f

            startTimerLoop()
        }
        viewModel.retrySkippedPrompt.observe(viewLifecycleOwner) { skippedCount ->
            if (skippedCount != null && skippedCount > 0) {
                timer?.cancel()

                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.retry_skipped_title))
                    .setMessage(getString(R.string.retry_skipped_message, skippedCount))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.retry_skipped_yes)) { _, _ ->
                        viewModel.startSkippedReview()
                        viewModel.consumeRetrySkippedPrompt()
                    }
                    .setNegativeButton(getString(R.string.retry_skipped_no)) { _, _ ->
                        viewModel.declineSkippedReview()
                        viewModel.consumeRetrySkippedPrompt()
                    }
                    .create()

                dialog.setOnShowListener {
                    val positiveButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                    val negativeButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)

                    positiveButton.setTextColor(
                        androidx.core.content.ContextCompat.getColor(requireContext(), R.color.lt_primary)
                    )

                    negativeButton.setTextColor(
                        androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_primary)
                    )
                }

                dialog.show()
            }
        }


        viewModel.finishedResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                val bundle = Bundle().apply {
                    putString("player_name", result.playerName)
                    putDouble("score", result.score)
                    putDouble("percentage", result.percentage)
                    putInt("rank", result.rank)
                    putInt("time_used", result.totalTimeSeconds)
                }
                viewModel.consumeFinishedResult()
                findNavController().navigate(R.id.action_quizFragment_to_resultFragment, bundle)
            }
        }
    }

    private fun setupClickListeners() {
        binding.cardOptionA.setOnClickListener { viewModel.submitAnswer(QuizOption.A) }
        binding.cardOptionB.setOnClickListener { viewModel.submitAnswer(QuizOption.B) }
        binding.cardOptionC.setOnClickListener { viewModel.submitAnswer(QuizOption.C) }
        binding.cardOptionD.setOnClickListener { viewModel.submitAnswer(QuizOption.D) }

        binding.btnSkip.setOnClickListener { viewModel.skipQuestion() }
    }

    private fun startTimerLoop() {
        timer?.cancel()

        timer = object : CountDownTimer(1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) = Unit

            override fun onFinish() {
                viewModel.tickSecond()
            }
        }.start()
    }

    private fun getOptionLabel(letter: String, isWrong: Boolean): String {
        return if (isWrong) "$letter ✕" else letter
    }

    private fun styleOption(
        card: MaterialCardView,
        text: android.widget.TextView,
        label: android.widget.TextView,
        isCorrect: Boolean,
        isWrong: Boolean,
        canAnswer: Boolean
    ) {
        card.isClickable = canAnswer

        when {
            isCorrect -> {
                card.setCardBackgroundColor(resources.getColor(R.color.green_primary, null))
                text.setTextColor(resources.getColor(R.color.white, null))
                label.setTextColor(resources.getColor(R.color.white, null))
            }

            isWrong -> {
                card.setCardBackgroundColor(resources.getColor(R.color.red_accent, null))
                text.setTextColor(resources.getColor(R.color.white, null))
                label.setTextColor(resources.getColor(R.color.white, null))
            }

            else -> {
                card.setCardBackgroundColor(resources.getColor(R.color.card_background, null))
                text.setTextColor(resources.getColor(R.color.text_primary, null))
                label.setTextColor(resources.getColor(R.color.gold_accent, null))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        timer = null
        _binding = null
    }
}