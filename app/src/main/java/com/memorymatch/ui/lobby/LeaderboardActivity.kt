package com.memorymatch.ui.lobby

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.memorymatch.databinding.ActivityLeaderboardBinding
import com.memorymatch.model.AppDatabase
import kotlinx.coroutines.launch

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private val adapter = LeaderboardAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvLeaderboard.layoutManager = LinearLayoutManager(this)
        binding.rvLeaderboard.adapter = adapter

        binding.btnBackToLobby.setOnClickListener {
            finish()
        }

        loadLeaderboards()
    }

    private fun loadLeaderboards() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@LeaderboardActivity)
            val topScores = db.scoreDao().getTopScores()
            
            if (topScores.isEmpty()) {
                binding.tvEmptyLeaderboard.visibility = View.VISIBLE
                binding.rvLeaderboard.visibility = View.GONE
            } else {
                binding.tvEmptyLeaderboard.visibility = View.GONE
                binding.rvLeaderboard.visibility = View.VISIBLE
                adapter.submitList(topScores)
            }
        }
    }
}
