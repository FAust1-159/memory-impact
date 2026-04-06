package com.memorymatch.ui.aftergame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.memorymatch.R
import com.memorymatch.databinding.DialogResultBinding
import com.memorymatch.viewmodel.GameViewModel
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.memorymatch.model.AppDatabase
import com.memorymatch.model.Score
import com.memorymatch.utils.SessionManager
import kotlinx.coroutines.launch

/**
 * Modal dialog shown after all pairs are matched.
 * Displays time consumed, total flips, and score.
 * Offers Retry (new game) and Back to Lobby.
 */
class ResultDialogFragment : DialogFragment() {

    private var _binding: DialogResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dialog should not be dismissible by tapping outside.
        isCancelable = false

        val totalSeconds = arguments?.getLong(ARG_TIME) ?: 0L
        val flips = arguments?.getInt(ARG_FLIPS) ?: 0
        val score = arguments?.getInt(ARG_SCORE) ?: 0

        binding.tvResultTime.text = getString(R.string.result_time, formatTime(totalSeconds))
        binding.tvResultFlips.text = getString(R.string.result_flips, flips)
        binding.tvResultScore.text = getString(R.string.result_score, score)

        saveScoreIfLoggedIn(score)

        binding.btnRetry.setOnClickListener {
            viewModel.resetGame()
            dismiss()
        }

        binding.btnBackToLobby.setOnClickListener {
            dismiss()
            requireActivity().finish()   // Pops GameActivity → back to LobbyActivity
        }
    }

    private fun saveScoreIfLoggedIn(score: Int) {
        val username = SessionManager.getUsername(requireContext())
        if (username != null) {
            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(requireContext())
                db.scoreDao().insertScore(Score(username = username, score = score))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun formatTime(totalSeconds: Long): String {
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        return getString(R.string.hud_time, m, s)
    }

    companion object {
        const val TAG = "ResultDialog"
        private const val ARG_TIME = "arg_time"
        private const val ARG_FLIPS = "arg_flips"
        private const val ARG_SCORE = "arg_score"

        fun newInstance(timeSeconds: Long, flips: Int, score: Int) =
            ResultDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_TIME, timeSeconds)
                    putInt(ARG_FLIPS, flips)
                    putInt(ARG_SCORE, score)
                }
            }
    }
}
