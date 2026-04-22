package com.leo.zamtrivia.ui.result

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.leo.zamtrivia.R
import com.leo.zamtrivia.databinding.FragmentResultBinding

class ResultFragment : Fragment(R.layout.fragment_result) {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentResultBinding.bind(view)

        val playerName = requireArguments().getString("player_name").orEmpty()
        val score = requireArguments().getDouble("score")
        val percentage = requireArguments().getDouble("percentage")
        val rank = requireArguments().getInt("rank")
        val timeUsed = requireArguments().getInt("time_used")

        binding.textResultName.text = getString(R.string.result_name_format, playerName)
        binding.textResultScore.text = getString(R.string.result_score_format, if (score % 1.0 == 0.0) score.toInt().toString() else score.toString())
        binding.textResultPercentage.text = getString(R.string.result_percentage_format, String.format("%.1f", percentage))
        binding.textResultRank.text = getString(R.string.result_rank_format, rank)
        binding.textResultTime.text = getString(R.string.result_time_format, timeUsed)

        binding.btnViewLeaderboard.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_leaderboardFragment)
        }

        binding.btnPlayAgain.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_mainMenuFragment)
        }

        binding.btnExitApp.setOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}