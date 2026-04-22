package com.leo.zamtrivia.ui.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AppCompatDelegate
import com.leo.zamtrivia.R
import com.leo.zamtrivia.databinding.FragmentMainMenuBinding
import com.leo.zamtrivia.ui.dialog.NameInputDialogFragment
import com.leo.zamtrivia.ui.session.SessionViewModel
import com.leo.zamtrivia.util.ThemeManager

class MainMenuFragment : Fragment(R.layout.fragment_main_menu) {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!
    private val sessionViewModel: SessionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainMenuBinding.bind(view)

        childFragmentManager.setFragmentResultListener(
            NameInputDialogFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val name = bundle.getString(NameInputDialogFragment.BUNDLE_NAME).orEmpty()
            if (name.isNotBlank()) {
                sessionViewModel.setPlayerName(name)
                findNavController().navigate(R.id.action_mainMenuFragment_to_modeSelectionFragment)
            }
        }

        binding.btnStartQuiz.setOnClickListener {
            NameInputDialogFragment().show(childFragmentManager, NameInputDialogFragment.TAG)
        }

        binding.btnLeaderboard.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_leaderboardFragment)
        }

        binding.btnExit.setOnClickListener {
            requireActivity().finish()
        }

        updateUiForTheme()
        binding.switchDarkMode.isChecked = ThemeManager.isDarkMode(requireContext())
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            ThemeManager.setDarkMode(requireContext(), isChecked)
        }
    }

    private fun updateUiForTheme() {
        val isDark = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES

        if (isDark) {
            binding.imgLogo.setImageResource(R.drawable.ic_app_logo_dark)
        } else {
            binding.imgLogo.setImageResource(R.drawable.ic_app_logo)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}