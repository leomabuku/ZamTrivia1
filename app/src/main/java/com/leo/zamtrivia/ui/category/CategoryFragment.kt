package com.leo.zamtrivia.ui.category

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.leo.zamtrivia.R
import com.leo.zamtrivia.data.repository.QuizRepository
import com.leo.zamtrivia.databinding.FragmentCategoryBinding
import com.leo.zamtrivia.ui.session.SessionViewModel

class CategoryFragment : Fragment(R.layout.fragment_category) {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private val sessionViewModel: SessionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCategoryBinding.bind(view)

        val repository = QuizRepository(requireContext())
        val categories = repository.getCategories()

        binding.recyclerCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCategories.adapter = CategoryAdapter(
            items = categories,
            countProvider = { repository.countForCategory(it) }
        ) { category ->
            val name = sessionViewModel.playerName.value
            if (name.isNullOrBlank()) {
                findNavController().popBackStack()
                return@CategoryAdapter
            }
            sessionViewModel.setCategory(category.name)
            val action = CategoryFragmentDirections.actionCategoryFragmentToQuizFragment(
                playerName = name,
                categoryName = category.name,
                isRandomMode = false
            )
            findNavController().navigate(action)
        }

        binding.btnBackCategory.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}