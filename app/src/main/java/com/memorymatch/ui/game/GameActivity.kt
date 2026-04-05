package com.memorymatch.ui.game

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.memorymatch.R
import com.memorymatch.databinding.ActivityGameBinding
import com.memorymatch.ui.aftergame.ResultDialogFragment
import com.memorymatch.viewmodel.GameViewModel

/**
 * Game screen — 4×4 card grid, HUD (time + flips), pause button.
 *
 * ── 🎨 CUSTOMISE ──────────────────────────────────────────────────────────
 *  • Background  → res/drawable/bg_game.xml
 *  • HUD font    → res/values/themes.xml  (gameHudFont attr)
 *  • Card back   → res/drawable/card_back.xml
 *  • Card fronts → res/drawable/card_front_01..08.xml
 * ──────────────────────────────────────────────────────────────────────────
 */
class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val viewModel: GameViewModel by viewModels()
    private lateinit var cardAdapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupGrid()
        observeViewModel()
    }

    // ── Toolbar / Pause ───────────────────────────────────────────────────────

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pause, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_pause -> {
                showPauseDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showPauseDialog() {
        viewModel.pauseTimer()
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.pause_title))
            .setItems(
                arrayOf(
                    getString(R.string.pause_resume),
                    getString(R.string.pause_lobby)
                )
            ) { _, which ->
                when (which) {
                    0 -> viewModel.startTimer()           // Resume
                    1 -> finish()                          // Back to Lobby
                }
            }
            .setOnCancelListener { viewModel.startTimer() } // Dismissed → resume
            .show()
    }

    // ── Grid ──────────────────────────────────────────────────────────────────

    private fun setupGrid() {
        cardAdapter = CardAdapter { position ->
            viewModel.onCardFlipped(position)
        }
        binding.rvCardGrid.apply {
            layoutManager = GridLayoutManager(this@GameActivity, GRID_COLUMNS)
            adapter = cardAdapter
            // Prevent the RecyclerView from intercepting card animations.
            itemAnimator = null
        }
    }

    // ── Observers ─────────────────────────────────────────────────────────────

    private fun observeViewModel() {
        viewModel.cards.observe(this) { cards ->
            cardAdapter.submitList(cards.toList())
        }

        viewModel.flipCount.observe(this) { count ->
            binding.tvFlipCount.text = getString(R.string.hud_flips, count)
        }

        viewModel.elapsedSeconds.observe(this) { seconds ->
            binding.tvTimer.text = formatTime(seconds)
        }

        viewModel.gameWon.observe(this) { won ->
            if (won) showResultDialog()
        }
    }

    // ── Win dialog ────────────────────────────────────────────────────────────

    private fun showResultDialog() {
        val time = viewModel.elapsedSeconds.value ?: 0L
        val flips = viewModel.flipCount.value ?: 0

        ResultDialogFragment.newInstance(time, flips)
            .show(supportFragmentManager, ResultDialogFragment.TAG)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun formatTime(totalSeconds: Long): String {
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        return getString(R.string.hud_time, m, s)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseTimer()
    }

    override fun onResume() {
        super.onResume()
        // Only restart timer if game is still in progress.
        if (viewModel.gameWon.value == false) {
            viewModel.startTimer()
        }
    }

    companion object {
        private const val GRID_COLUMNS = 4
    }
}
