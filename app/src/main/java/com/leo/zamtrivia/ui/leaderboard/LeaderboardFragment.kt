package com.leo.zamtrivia.ui.leaderboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.leo.zamtrivia.R
import com.leo.zamtrivia.data.repository.LeaderboardRepository
import com.leo.zamtrivia.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : Fragment(R.layout.fragment_leaderboard) {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLeaderboardBinding.bind(view)

        val items = LeaderboardRepository(requireContext()).getLeaderboard()

        binding.recyclerLeaderboard.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerLeaderboard.adapter = LeaderboardAdapter(items)
        binding.textEmptyLeaderboard.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE

        binding.btnBackLeaderboard.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}