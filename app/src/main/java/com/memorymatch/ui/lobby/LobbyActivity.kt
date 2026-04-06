package com.memorymatch.ui.lobby

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.memorymatch.R
import com.memorymatch.databinding.ActivityLobbyBinding
import com.memorymatch.ui.game.GameActivity
import com.memorymatch.utils.SessionManager
import kotlin.system.exitProcess

/**
 * Lobby / home screen.
 */
class LobbyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun setupButtons() {
        binding.btnLogin.setOnClickListener {
            if (SessionManager.isLoggedIn(this)) {
                SessionManager.logout(this)
                updateUI()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        binding.btnStartGame.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        binding.btnLeaderboards.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }

        binding.btnExitGame.setOnClickListener {
            finish()
            exitProcess(0)
        }
    }

    private fun updateUI() {
        val username = SessionManager.getUsername(this)
        if (username != null) {
            binding.btnLogin.text = getString(R.string.lobby_logout)
            binding.tvLobbyTitle.text = getString(R.string.welcome_user, username)
        } else {
            binding.btnLogin.text = getString(R.string.lobby_login)
            binding.tvLobbyTitle.text = getString(R.string.lobby_title)
        }
    }
}
