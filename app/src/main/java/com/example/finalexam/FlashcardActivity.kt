package com.example.finalexam

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalexam.Vocabulary

class FlashcardActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var flashcardAdapter: FlashcardAdapter
    private lateinit var vocabularyList: List<Vocabulary>
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)
        recyclerView = findViewById(R.id.flashcardRecyclerView)

        // Lấy danh sách từ vựng từ Intent
        vocabularyList = intent.getParcelableArrayListExtra("vocabularyList") ?: emptyList()

        flashcardAdapter = FlashcardAdapter(vocabularyList)
        recyclerView.adapter = flashcardAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Thêm nút next và previous
        findViewById<Button>(R.id.nextButton).setOnClickListener {
            showNextFlashcard()
        }

        findViewById<Button>(R.id.previousButton).setOnClickListener {
            showPreviousFlashcard()
        }

        // Hiển thị flashcard hiện tại
        showFlashcard(currentPosition)
    }

    private fun showNextFlashcard() {
        if (currentPosition < vocabularyList.size - 1) {
            currentPosition++
            showFlashcard(currentPosition)
        }
    }

    private fun showPreviousFlashcard() {
        if (currentPosition > 0) {
            currentPosition--
            showFlashcard(currentPosition)
        }
    }

    private fun showFlashcard(position: Int) {
        // Hiển thị flashcard ở vị trí position
        recyclerView.scrollToPosition(position)
        toggleLanguage(position)
    }

    private fun toggleLanguage(position: Int) {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) as? FlashcardAdapter.ViewHolder
        viewHolder?.let {
            it.englishWordTextView.visibility = View.VISIBLE
            it.vietnameseMeaningTextView.visibility = View.GONE
            it.vietnameseMeaningTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
    }

}
