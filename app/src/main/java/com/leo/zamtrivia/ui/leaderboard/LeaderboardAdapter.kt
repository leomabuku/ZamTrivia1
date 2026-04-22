package com.leo.zamtrivia.ui.leaderboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leo.zamtrivia.data.model.LeaderboardEntry
import com.leo.zamtrivia.databinding.ItemLeaderboardBinding

class LeaderboardAdapter(
    private val items: List<LeaderboardEntry>
) : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaderboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(position + 1, items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class LeaderboardViewHolder(
        private val binding: ItemLeaderboardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(rank: Int, item: LeaderboardEntry) {
            binding.textRank.text = rank.toString()
            binding.textPlayerName.text = item.playerName
            binding.textScore.text = item.score.toString()
            binding.textTime.text = "${item.totalTimeSeconds}s"
        }
    }
}