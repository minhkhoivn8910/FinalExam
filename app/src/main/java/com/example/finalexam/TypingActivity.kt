package com.example.finalexam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class TypingActivity : AppCompatActivity() {
    private lateinit var vocabularyList: List<Vocabulary>
    private lateinit var vietnameseWordTextView: TextView
    private lateinit var englishWordEditText: EditText
    private lateinit var checkButton: Button
    private var currentVocabularyIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_typing)

        vietnameseWordTextView = findViewById(R.id.vietnameseWordTextView)
        englishWordEditText = findViewById(R.id.englishWordEditText)
        checkButton = findViewById(R.id.checkButton)

        // Nhận danh sách từ Intent
        vocabularyList = intent.getParcelableArrayListExtra("vocabularyList") ?: emptyList()

        // Hiển thị từ tiếng Việt ban đầu
        displayCurrentVocabulary()

        // Xử lý khi người dùng nhấn nút "Check Answer"
        checkButton.setOnClickListener {
            checkAnswer()
        }
    }

    private fun displayCurrentVocabulary() {
        if (currentVocabularyIndex < vocabularyList.size) {
            val currentVocabulary = vocabularyList[currentVocabularyIndex]
            vietnameseWordTextView.text = currentVocabulary.vietnameseMeaning
        } else {
            // Hiển thị thông báo hoặc chuyển về màn hình chính nếu đã hoàn thành tất cả từ
            Toast.makeText(this, "All words completed", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun checkAnswer() {
        val currentVocabulary = vocabularyList[currentVocabularyIndex]
        val userAnswer = englishWordEditText.text.toString().trim()

        if (currentVocabulary.englishWord.equals(userAnswer, ignoreCase = true)) {
            // Câu trả lời đúng
            Toast.makeText(this, "Correct Answer!", Toast.LENGTH_SHORT).show()
        } else {
            // Câu trả lời sai
            Toast.makeText(this, "Incorrect Answer. Try again next time.", Toast.LENGTH_SHORT).show()
        }

        // Chuyển sang từ tiếp theo
        currentVocabularyIndex++
        displayCurrentVocabulary()

        // Xóa nội dung của EditText
        englishWordEditText.text.clear()
    }
}
