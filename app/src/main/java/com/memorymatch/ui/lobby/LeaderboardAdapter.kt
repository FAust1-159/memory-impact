package com.memorymatch.ui.lobby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.memorymatch.databinding.ItemLeaderboardBinding
import com.memorymatch.model.Score

class LeaderboardAdapter : ListAdapter<Score, LeaderboardAdapter.ViewHolder>(ScoreDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position + 1)
    }

    class ViewHolder(private val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(score: Score, rank: Int) {
            binding.tvRank.text = "#$rank"
            binding.tvUsername.text = score.username
            binding.tvScore.text = score.score.toString()
        }
    }

    class ScoreDiffCallback : DiffUtil.ItemCallback<Score>() {
        override fun areItemsTheSame(oldItem: Score, newItem: Score): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Score, newItem: Score): Boolean = oldItem == newItem
    }
}
