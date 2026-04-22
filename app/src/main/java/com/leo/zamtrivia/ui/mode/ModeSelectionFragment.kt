package com.leo.zamtrivia.ui.mode

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.leo.zamtrivia.R
import com.leo.zamtrivia.databinding.FragmentModeSelectionBinding
import com.leo.zamtrivia.ui.session.SessionViewModel

class ModeSelectionFragment : Fragment(R.layout.fragment_mode_selection) {

    private var _binding: FragmentModeSelectionBinding? = null
    private val binding get() = _binding!!
    private val sessionViewModel: SessionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentModeSelectionBinding.bind(view)

        binding.btnChooseCategory.setOnClickListener {
            if (sessionViewModel.playerName.value.isNullOrBlank()) {
                Toast.makeText(requireContext(), R.string.name_required, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return@setOnClickListener
            }
            sessionViewModel.clearMode()
            findNavController().navigate(R.id.action_modeSelectionFragment_to_categoryFragment)
        }

        binding.btnRandomQuiz.setOnClickListener {
            val name = sessionViewModel.playerName.value
            if (name.isNullOrBlank()) {
                Toast.makeText(requireContext(), R.string.name_required, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return@setOnClickListener
            }
            sessionViewModel.setRandomMode()
            val action = ModeSelectionFragmentDirections.actionModeSelectionFragmentToQuizFragment(
                playerName = name,
                categoryName = null,
                isRandomMode = true
            )
            findNavController().navigate(action)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}