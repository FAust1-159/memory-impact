package com.memorymatch.utils

import com.memorymatch.model.Card
import com.memorymatch.R

/**
 * Builds a shuffled list of 16 [Card] objects (8 pairs) for the 4×4 grid.
 *
 * ── 🎨 CUSTOMISE CARD FRONTS ──────────────────────────────────────────────
 * Replace the drawable resource ids in [FRONT_DRAWABLES] with your own art.
 * Each entry represents ONE pair; you need exactly 8 drawables.
 * ──────────────────────────────────────────────────────────────────────────
 */
object CardDeck {

    /**
     * 8 drawable resource ids — one per pair.
     * Swap these for your own card-front images / vector drawables.
     */
    private val FRONT_DRAWABLES = listOf(
        R.drawable.card_front_01,
        R.drawable.card_front_02,
        R.drawable.card_front_03,
        R.drawable.card_front_04,
        R.drawable.card_front_05,
        R.drawable.card_front_06,
        R.drawable.card_front_07,
        R.drawable.card_front_08
    )

    /** Returns a new shuffled deck every time it is called. */
    fun buildShuffledDeck(): List<Card> {
        val deck = mutableListOf<Card>()
        FRONT_DRAWABLES.forEachIndexed { pairId, drawableRes ->
            // Each drawable appears TWICE (once per card in the pair).
            repeat(2) {
                deck.add(
                    Card(
                        id = deck.size,          // assigned after adding
                        pairId = pairId,
                        frontResId = drawableRes
                    )
                )
            }
        }
        deck.shuffle()
        // Re-assign sequential ids after shuffle so position == id.
        return deck.mapIndexed { index, card -> card.copy(id = index) }
    }
}
