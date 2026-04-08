package com.memorymatch.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memorymatch.model.Card
import com.memorymatch.utils.CardDeck
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Holds and manages all game state so it survives configuration changes.
 *
 * Responsibilities:
 *  - Build and shuffle the deck
 *  - Track which cards are flipped / matched
 *  - Enforce the "only 2 cards face-up at once" rule
 *  - Run the elapsed-time counter
 *  - Expose LiveData for the UI to observe
 */
class GameViewModel : ViewModel() {

    // ── Deck ─────────────────────────────────────────────────────────────────

    private val _cards = MutableLiveData<List<Card>>()
    val cards: LiveData<List<Card>> = _cards

    // ── HUD counters ─────────────────────────────────────────────────────────

    private val _flipCount = MutableLiveData(0)
    val flipCount: LiveData<Int> = _flipCount

    private val _elapsedSeconds = MutableLiveData(0L)
    val elapsedSeconds: LiveData<Long> = _elapsedSeconds

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    // ── Win state ────────────────────────────────────────────────────────────

    private val _gameWon = MutableLiveData(false)
    val gameWon: LiveData<Boolean> = _gameWon

    // ── Internal state ───────────────────────────────────────────────────────

    /** Cards that are currently face-up but not yet matched (max 2). */
    private val pendingFlips = mutableListOf<Card>()

    /** While true the player cannot flip any card (brief lock while validating pairs). */
    private var isLocked = false

    /** True while the game clock should advance. */
    private var timerRunning = false
    private var timerJob: Job? = null

    // ── Lifecycle ────────────────────────────────────────────────────────────

    init {
        resetGame()
    }

    /** Start a fresh game: new shuffled deck, counters reset. */
    fun resetGame() {
        stopTimer()
        _cards.value = CardDeck.buildShuffledDeck()
        _flipCount.value = 0
        _elapsedSeconds.value = 0L
        _score.value = 0
        _gameWon.value = false
        pendingFlips.clear()
        isLocked = false
        startTimer()
    }

    // ── Timer ────────────────────────────────────────────────────────────────

    fun startTimer() {
        if (timerRunning) return
        timerRunning = true
        timerJob = viewModelScope.launch {
            while (timerRunning) {
                delay(1_000)
                _elapsedSeconds.value = (_elapsedSeconds.value ?: 0L) + 1
            }
        }
    }

    fun pauseTimer() {
        timerRunning = false
        timerJob?.cancel()
        timerJob = null
    }

    private fun stopTimer() {
        timerRunning = false
        timerJob?.cancel()
        timerJob = null
    }

    // ── Flip logic ───────────────────────────────────────────────────────────

    /**
     * Called when the player taps a card at [position].
     * Returns false if the tap should be ignored (locked, already matched/flipped).
     */
    fun onCardFlipped(position: Int): Boolean {
        if (isLocked) return false

        val currentCards = _cards.value?.toMutableList() ?: return false
        val card = currentCards[position]

        if (card.isFlipped || card.isMatched) return false
        if (pendingFlips.size >= 2) return false

        // Flip the card face-up.
        val flippedCard = card.copy(isFlipped = true)
        currentCards[position] = flippedCard
        _cards.value = currentCards
        _flipCount.value = (_flipCount.value ?: 0) + 1

        pendingFlips.add(flippedCard)

        if (pendingFlips.size == 2) {
            // Lock input until evaluatePair decides when to release it.
            isLocked = true
            evaluatePair()
        }

        return true
    }

    /**
     * Check whether the two pending cards are a pair.
     * If yes  → mark both matched.
     * If no   → lock input briefly, then flip both back down.
     */
    private fun evaluatePair() {
        val (first, second) = pendingFlips
        pendingFlips.clear()

        if (first.pairId == second.pairId) {
            // ✅ Match!
            val updated = _cards.value!!.toMutableList()
            updated[first.id] = first.copy(isMatched = true)
            updated[second.id] = second.copy(isMatched = true)
            _cards.value = updated
            
            // Score +10 for match
            _score.value = (_score.value ?: 0) + 10

            if (updated.all { it.isMatched }) {
                stopTimer()
                _gameWon.value = true
            }

            // Small buffer delay even for matches to prevent rapid-click bugs
            viewModelScope.launch {
                delay(MATCH_DELAY_MS)
                isLocked = false
            }
        } else {
            // ❌ No match — wait, flip back, then unlock.
            
            // Score -5 for mismatch
            _score.value = (_score.value ?: 0) - 5

            viewModelScope.launch {
                delay(MISMATCH_DELAY_MS)
                val updated = _cards.value!!.toMutableList()
                updated[first.id] = first.copy(isFlipped = false)
                updated[second.id] = second.copy(isFlipped = false)
                _cards.value = updated
                isLocked = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }

    companion object {
        /** How long (ms) the mismatched pair stays visible before flipping back. */
        const val MISMATCH_DELAY_MS = 800L
        /** Buffer time after a match before player can flip another card. */
        const val MATCH_DELAY_MS = 300L
    }
}
