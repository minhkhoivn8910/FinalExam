package com.example.finalexam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalexam.Vocabulary

class VocabularyAdapter(private val vocabularyList: List<Vocabulary>, private val clickListener: ((Vocabulary) -> Unit)?, private val longPressListener: ((Vocabulary) -> Unit)?) :
    RecyclerView.Adapter<VocabularyAdapter.ViewHolder>() {
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vocabulary, parent, false)
        val viewHolder = ViewHolder(view)

        viewHolder.itemView.setOnClickListener {
            val clickedPosition = viewHolder.adapterPosition
            selectedPosition = clickedPosition
            notifyDataSetChanged()
            clickListener?.invoke(vocabularyList[clickedPosition])
        }

        viewHolder.itemView.setOnLongClickListener {
            val longPressedPosition = viewHolder.adapterPosition
            longPressListener?.invoke(vocabularyList[longPressedPosition])
            true
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vocabulary = vocabularyList[position]
        holder.englishWordTextView.text = vocabulary.englishWord
        holder.vietnameseMeaningTextView.text = vocabulary.vietnameseMeaning

        holder.itemView.setOnClickListener {
            val clickedPosition = holder.adapterPosition
            selectedPosition = clickedPosition
            notifyDataSetChanged()
            clickListener?.invoke(vocabularyList[clickedPosition])
        }

        holder.itemView.setOnLongClickListener {
            val longPressedPosition = holder.adapterPosition
            longPressListener?.invoke(vocabularyList[longPressedPosition])
            true // true để chỉ định rằng sự kiện đã được xử lý
        }

        holder.itemView.setBackgroundResource(
            if (position == selectedPosition) R.drawable.selected_item_background else android.R.color.transparent
        )
    }

    override fun getItemCount(): Int {
        return vocabularyList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val englishWordTextView: TextView = itemView.findViewById(R.id.englishWordTextView)
        val vietnameseMeaningTextView: TextView =
            itemView.findViewById(R.id.vietnameseMeaningTextView)
    }
}
