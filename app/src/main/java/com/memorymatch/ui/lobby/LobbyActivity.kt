package com.memorymatch.ui.lobby

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.memorymatch.R
import com.memorymatch.databinding.ActivityLobbyBinding
import com.memorymatch.ui.game.GameActivity
import kotlin.system.exitProcess

/**
 * Lobby / home screen.
 *
 * ── 🎨 CUSTOMISE ──────────────────────────────────────────────────────────
 *  • Background  → res/drawable/bg_lobby.xml
 *  • Title font  → res/values/themes.xml  (lobbyTitleFont / lobbyButtonFont)
 *  • Button text → res/values/strings.xml
 * ──────────────────────────────────────────────────────────────────────────
 */
class LobbyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartGame.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        binding.btnExitGame.setOnClickListener {
            finish()
            exitProcess(0)
        }
    }
}
