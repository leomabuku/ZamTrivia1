package com.leo.zamtrivia.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leo.zamtrivia.R
import com.leo.zamtrivia.data.model.Category
import com.leo.zamtrivia.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val items: List<Category>,
    private val countProvider: (String) -> Int,
    private val onClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.textCategoryName.text = category.name
            binding.textCategoryDescription.text = category.description
            binding.textCategoryCount.text = itemView.context.getString(
                R.string.category_question_count,
                countProvider(category.name)
            )
            val iconRes = itemView.context.resources.getIdentifier(
                category.iconResName,
                "drawable",
                itemView.context.packageName
            )
            if (iconRes != 0) {
                binding.imageCategoryIcon.setImageResource(iconRes)
            } else {
                binding.imageCategoryIcon.setImageResource(R.drawable.ic_category_default)
            }
            binding.root.setOnClickListener { onClick(category) }
        }
    }
}