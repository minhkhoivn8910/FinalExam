package com.example.finalexam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FlashcardAdapter(private val vocabularyList: List<Vocabulary>) :
    RecyclerView.Adapter<FlashcardAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val englishWordTextView: TextView = itemView.findViewById(R.id.englishWordTextView)
        val vietnameseMeaningTextView: TextView = itemView.findViewById(R.id.vietnameseMeaningTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.flashcard_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vocabulary = vocabularyList[position]
        holder.englishWordTextView.text = vocabulary.englishWord
        holder.vietnameseMeaningTextView.text = vocabulary.vietnameseMeaning

        // Initially, hide the Vietnamese meaning
        holder.vietnameseMeaningTextView.visibility = View.GONE

        // Set a click listener to toggle visibility
        holder.englishWordTextView.setOnClickListener {
            toggleLanguage(holder)
        }
    }

    override fun getItemCount(): Int {
        return vocabularyList.size
    }

    private fun toggleLanguage(holder: ViewHolder) {
        // Toggle visibility of English and Vietnamese TextViews
        if (holder.englishWordTextView.visibility == View.VISIBLE) {
            holder.englishWordTextView.visibility = View.GONE
            holder.vietnameseMeaningTextView.visibility = View.VISIBLE
        } else {
            holder.englishWordTextView.visibility = View.VISIBLE
            holder.vietnameseMeaningTextView.visibility = View.GONE
        }
    }
}
