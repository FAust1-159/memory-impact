package com.memorymatch.ui.game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.memorymatch.R
import com.memorymatch.model.Card

/**
 * RecyclerView adapter for the 4×4 card grid.
 *
 * Uses [ListAdapter] + [DiffUtil] so only changed cards are re-bound,
 * which lets us play per-card flip animations correctly.
 *
 * ── 🎨 CUSTOMISE CARD BACK ────────────────────────────────────────────────
 * The card-back image is loaded from [R.drawable.card_back].
 * Replace that drawable with your own art to change the card back globally.
 * ──────────────────────────────────────────────────────────────────────────
 */
class CardAdapter(
    private val onCardClick: (Int) -> Unit
) : ListAdapter<Card, CardAdapter.CardViewHolder>(CardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(getItem(position), onCardClick)
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgFront: ImageView = itemView.findViewById(R.id.imgCardFront)
        private val imgBack: ImageView = itemView.findViewById(R.id.imgCardBack)

        fun bind(card: Card, onClick: (Int) -> Unit) {
            if (card.isFlipped || card.isMatched) {
                showFront(card)
            } else {
                showBack()
            }

            itemView.setOnClickListener {
                if (!card.isFlipped && !card.isMatched) {
                    playFlipOutIn(card.frontResId)
                    onClick(bindingAdapterPosition)
                }
            }
        }

        private fun showFront(card: Card) {
            imgFront.setImageResource(card.frontResId)
            imgFront.visibility = View.VISIBLE
            imgBack.visibility = View.GONE
        }

        private fun showBack() {
            // 🎨 card_back.xml is the placeholder — swap for your art.
            imgBack.setImageResource(R.drawable.card_back)
            imgBack.visibility = View.VISIBLE
            imgFront.visibility = View.GONE
        }

        /**
         * Plays the flip-out then flip-in animation pair on the card cell.
         * Animations are defined in res/anim/flip_out.xml and flip_in.xml.
         */
        private fun playFlipOutIn(frontRes: Int) {
            val ctx = itemView.context
            val flipOut = AnimationUtils.loadAnimation(ctx, R.anim.flip_out)
            val flipIn = AnimationUtils.loadAnimation(ctx, R.anim.flip_in)

            flipOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(a: android.view.animation.Animation?) {}
                override fun onAnimationRepeat(a: android.view.animation.Animation?) {}
                override fun onAnimationEnd(a: android.view.animation.Animation?) {
                    imgBack.visibility = View.GONE
                    imgFront.setImageResource(frontRes)
                    imgFront.visibility = View.VISIBLE
                    itemView.startAnimation(flipIn)
                }
            })
            itemView.startAnimation(flipOut)
        }
    }

    // ── DiffUtil ──────────────────────────────────────────────────────────────

    private class CardDiffCallback : DiffUtil.ItemCallback<Card>() {
        override fun areItemsTheSame(old: Card, new: Card) = old.id == new.id
        override fun areContentsTheSame(old: Card, new: Card) = old == new
    }
}
