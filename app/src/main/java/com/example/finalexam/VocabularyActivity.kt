package com.example.finalexam

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalexam.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.util.Locale

class VocabularyActivity : AppCompatActivity() {
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var vocabularyList: MutableList<Vocabulary>
    private lateinit var vocabularyAdapter: VocabularyAdapter
    private lateinit var topicName: String
    private lateinit var fabAddVocabulary: FloatingActionButton
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var flashcardButton: Button
    private lateinit var quizButton: Button
    private lateinit var typingButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocabulary)

        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recyclerView)
        fabAddVocabulary = findViewById(R.id.fabAddVocabulary)
        flashcardButton = findViewById(R.id.flashcardButton)
        quizButton = findViewById(R.id.quizButton)
        typingButton = findViewById(R.id.typingButton)


        flashcardButton.setOnClickListener { startFlashcardMode() }
        quizButton.setOnClickListener { startQuizMode() }
        typingButton.setOnClickListener { startTypingMode() }



        val colorFrom = resources.getColor(R.color.selectedItemBackground)
        val colorTo = resources.getColor(android.R.color.transparent)
        val duration = 300L

        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = duration
        colorAnimation.addUpdateListener { animator ->
            recyclerView.setBackgroundColor(animator.animatedValue as Int)
        }


        // Lấy tên của Topic từ Intent
        topicName = intent.getStringExtra("topicName") ?: ""
        setupToolbar(topicName)

        vocabularyList = mutableListOf()
        vocabularyAdapter = VocabularyAdapter(vocabularyList,
            { vocabulary -> // Click listener
                colorAnimation.start()
                speak(vocabulary.englishWord.toString())
            },
            { vocabulary -> // Long press listener
                showUpdateDeleteDialog(vocabulary)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = vocabularyAdapter


        fabAddVocabulary.setOnClickListener {
            showAddVocabularyDialog()
        }

        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.US)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "TextToSpeech initialization failed", Toast.LENGTH_SHORT).show()
            }
        }

        displayVocabularyListByTopic()
    }

    private fun setupToolbar(topicName: String) {
        toolbar.title = topicName
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun displayVocabularyListByTopic() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("vocabulary")
        val query = databaseReference.orderByChild("topicName").equalTo(topicName)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                vocabularyList.clear()

                for (vocabularySnapshot in dataSnapshot.children) {
                    val vocabulary = vocabularySnapshot.getValue(Vocabulary::class.java)
                    vocabulary?.let {
                        it.id = vocabularySnapshot.key
                        vocabularyList.add(it)
                    }
                }

                vocabularyAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })
    }
    private fun showAddVocabularyDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_vocabulary, null)
        val englishWordEditText = dialogView.findViewById<EditText>(R.id.englishWordEditText)
        val vietnameseMeaningEditText = dialogView.findViewById<EditText>(R.id.vietnameseMeaningEditText)
        val submitButton = dialogView.findViewById<Button>(R.id.submitButton)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Add Vocabulary")

        val alertDialog = dialogBuilder.create()

        submitButton.setOnClickListener {
            val englishWord = englishWordEditText.text.toString().trim()
            val vietnameseMeaning = vietnameseMeaningEditText.text.toString().trim()

            if (englishWord.isNotEmpty() && vietnameseMeaning.isNotEmpty()) {
                val newVocabulary = Vocabulary(englishWord = englishWord, vietnameseMeaning = vietnameseMeaning, topicName = topicName)
                addVocabularyToFirebase(newVocabulary)
                alertDialog.dismiss()
            } else {
                // Xử lý trường hợp các trường nhập liệu trống
                Toast.makeText(this, "Please enter both English word and Vietnamese meaning", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.show()
    }

    private fun addVocabularyToFirebase(newVocabulary: Vocabulary) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("vocabulary")
        val newVocabularyReference = databaseReference.push()

        // Set a unique ID for the new vocabulary
        val vocabularyId = newVocabularyReference.key ?: ""

        // Set the ID for the new vocabulary
        newVocabularyReference.child("id").setValue(vocabularyId)

        // Lưu trữ vocabulary vào database
        newVocabularyReference.setValue(newVocabulary)

        // Cập nhật RecyclerView
        displayVocabularyListByTopic()
    }
    private fun speak(word: String) {
        val params = HashMap<String, String>()
        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "word"
        textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, params)
    }
    private fun showUpdateDeleteDialog(selectedVocabulary: Vocabulary) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_delete, null)
        val englishWordEditText = dialogView.findViewById<EditText>(R.id.englishWordEditText)
        val vietnameseMeaningEditText = dialogView.findViewById<EditText>(R.id.vietnameseMeaningEditText)
        val updateButton = dialogView.findViewById<Button>(R.id.updateButton)
        val deleteButton = dialogView.findViewById<Button>(R.id.deleteButton)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Update/Delete Vocabulary")

        val alertDialog = dialogBuilder.create()

        // Đặt giá trị của từ vựng được chọn vào các ô nhập liệu
        englishWordEditText.setText(selectedVocabulary.englishWord)
        vietnameseMeaningEditText.setText(selectedVocabulary.vietnameseMeaning)

        updateButton.setOnClickListener {
            val newEnglishWord = englishWordEditText.text.toString().trim()
            val newVietnameseMeaning = vietnameseMeaningEditText.text.toString().trim()

            if (newEnglishWord.isNotEmpty() && newVietnameseMeaning.isNotEmpty()) {
                // Update từ vựng trong Firebase
                selectedVocabulary.englishWord = newEnglishWord
                selectedVocabulary.vietnameseMeaning = newVietnameseMeaning
                updateVocabulary(selectedVocabulary)
                alertDialog.dismiss()
            } else {
                // Xử lý trường hợp các trường nhập liệu trống
                Toast.makeText(
                    this,
                    "Please enter both English word and Vietnamese meaning",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        deleteButton.setOnClickListener {
            // Xử lý khi nút Delete được nhấn
            deleteVocabulary(selectedVocabulary)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
    private fun updateVocabulary(updatedVocabulary: Vocabulary) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("vocabulary")
        databaseReference.child(updatedVocabulary.id.toString()).setValue(updatedVocabulary)

        displayVocabularyListByTopic()
    }
    private fun deleteVocabulary(selectedVocabulary: Vocabulary) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("vocabulary")
        databaseReference.child(selectedVocabulary.id.toString()).removeValue()

        displayVocabularyListByTopic()
    }
    private fun startFlashcardMode() {
        val intent = Intent(this, FlashcardActivity::class.java)
        intent.putParcelableArrayListExtra("vocabularyList", ArrayList(vocabularyList))
        startActivity(intent)
        Toast.makeText(this, "Flashcard Mode", Toast.LENGTH_SHORT).show()
    }

    private fun startQuizMode() {
        val intent = Intent(this, QuizActivity::class.java)
        intent.putParcelableArrayListExtra("vocabularyList", ArrayList(vocabularyList))
        startActivity(intent)
        Toast.makeText(this, "Quiz Mode", Toast.LENGTH_SHORT).show()
    }


    private fun startTypingMode() {
        val intent = Intent(this, TypingActivity::class.java)
        intent.putParcelableArrayListExtra("vocabularyList", ArrayList(vocabularyList))
        startActivity(intent)
        Toast.makeText(this, "Typing Mode", Toast.LENGTH_SHORT).show()
    }

}

