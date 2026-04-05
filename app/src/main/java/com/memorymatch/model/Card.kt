package com.memorymatch.model

/**
 * Represents a single card in the memory-match grid.
 *
 * @param id          Unique card instance id (0–15 for a 4×4 grid).
 * @param pairId      Shared id between the two cards that form a pair (0–7).
 * @param frontResId  Drawable resource for the card's FACE.
 *                    ── 🎨 CUSTOMISE: swap in your own drawable res ids ──
 * @param isFlipped   True when the card is face-up (visible to the player).
 * @param isMatched   True when this card's pair has been successfully found.
 */
data class Card(
    val id: Int,
    val pairId: Int,
    val frontResId: Int,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)
