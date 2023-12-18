package com.example.finalexam

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class QuizActivity : AppCompatActivity() {
    private lateinit var vocabularyList: List<Vocabulary>
    private var currentPosition: Int = 0
    private var defaultButtonColor: Int = 0
    private lateinit var currentTopicName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Retrieve vocabulary list from Intent
        vocabularyList = intent.getParcelableArrayListExtra("vocabularyList") ?: emptyList()

        // Initialize quiz UI components and display the first question
        initializeQuizUI()
        displayQuestion(currentPosition)
        defaultButtonColor = ContextCompat.getColor(this, R.color.defaultButtonColor)
    }

    private fun initializeQuizUI() {
        // Add your UI initialization code here
        findViewById<Button>(R.id.nextButton).setOnClickListener {
            showNextQuestion()
        }
    }

    private fun displayQuestion(position: Int) {
        if (position < vocabularyList.size) {
            val currentVocabulary = vocabularyList[position]
            val vietnameseWordTextView = findViewById<TextView>(R.id.vietnameseWordTextView)
            val option1Button = findViewById<Button>(R.id.option1Button)
            val option2Button = findViewById<Button>(R.id.option2Button)
            val option3Button = findViewById<Button>(R.id.option3Button)
            val option4Button = findViewById<Button>(R.id.option4Button)

            vietnameseWordTextView.text = currentVocabulary.vietnameseMeaning

            // Pass the list of all vocabularies to getIncorrectOptions
            val options = (currentVocabulary.getIncorrectOptions(vocabularyList, 3) + currentVocabulary.englishWord).toMutableList()
            options.shuffle()

            option1Button.text = options[0]
            option2Button.text = options[1]
            option3Button.text = options[2]
            option4Button.text = options[3]

            // Obtain the topicName from the first vocabulary in the list
            currentTopicName = vocabularyList[0].topicName.toString()

            setOptionClickListeners(currentVocabulary, option1Button, option2Button, option3Button, option4Button)
        } else {
            // Quiz completed
            Toast.makeText(this, "Quiz completed", Toast.LENGTH_SHORT).show()

            // Navigate back to VocabularyActivity with the selected topic
            val intent = Intent(this, VocabularyActivity::class.java)
            intent.putExtra("selectedTopic", currentTopicName)
            startActivity(intent)

            // Finish the current activity (QuizActivity)
            finish()
        }
    }



    private fun setOptionClickListeners(vocabulary: Vocabulary, vararg buttons: Button) {
        for (button in buttons) {
            button.setOnClickListener {
                checkAnswer(vocabulary, button)
            }
        }
    }

    private fun checkAnswer(vocabulary: Vocabulary, selectedButton: Button) {
        val correctAnswer = vocabulary.englishWord
        val selectedAnswer = selectedButton.text.toString()

        if (correctAnswer == selectedAnswer) {
            // Correct answer
            selectedButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
            selectedButton.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        } else {
            // Incorrect answer
            selectedButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light))

            // Highlight the correct answer (if needed)
            highlightCorrectAnswer(correctAnswer.toString())
        }

        // Highlight the correct answer
        val correctButton = findCorrectButton(correctAnswer.toString())
        correctButton?.let {
            it.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
            it.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        }

        // Disable further clicks on options
        disableOptionClicks()

        // Show the next button
        findViewById<Button>(R.id.nextButton).visibility = View.VISIBLE
    }


    private fun findCorrectButton(correctAnswer: String): Button? {
        val option1Button = findViewById<Button>(R.id.option1Button)
        val option2Button = findViewById<Button>(R.id.option2Button)
        val option3Button = findViewById<Button>(R.id.option3Button)
        val option4Button = findViewById<Button>(R.id.option4Button)

        return when (correctAnswer) {
            option1Button.text.toString() -> option1Button
            option2Button.text.toString() -> option2Button
            option3Button.text.toString() -> option3Button
            option4Button.text.toString() -> option4Button
            else -> null
        }
    }



    private fun highlightCorrectAnswer(correctAnswer: String) {
        // Add logic to highlight the correct answer (if needed)
    }

    private fun disableOptionClicks() {
        val option1Button = findViewById<Button>(R.id.option1Button)
        val option2Button = findViewById<Button>(R.id.option2Button)
        val option3Button = findViewById<Button>(R.id.option3Button)
        val option4Button = findViewById<Button>(R.id.option4Button)

        option1Button.isClickable = false
        option2Button.isClickable = false
        option3Button.isClickable = false
        option4Button.isClickable = false
    }

    private fun showNextQuestion() {
        // Reset UI for the next question
        resetQuizUI()

        if (currentPosition < vocabularyList.size - 1) {
            currentPosition++
            displayQuestion(currentPosition)
        } else {
            // Quiz completed
            Toast.makeText(this, "Quiz completed", Toast.LENGTH_SHORT).show()
            // Add your completion logic here
            val intent = Intent(this, VocabularyActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun resetQuizUI() {
        val option1Button = findViewById<Button>(R.id.option1Button)
        val option2Button = findViewById<Button>(R.id.option2Button)
        val option3Button = findViewById<Button>(R.id.option3Button)
        val option4Button = findViewById<Button>(R.id.option4Button)

        // Reset button colors and text
        resetButton(option1Button)
        resetButton(option2Button)
        resetButton(option3Button)
        resetButton(option4Button)

        // Enable option clicks
        enableOptionClicks()

        // Set the background color of the options to white
        option1Button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        option2Button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        option3Button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        option4Button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))

        // Set the visibility of the options to VISIBLE
        option1Button.visibility = View.VISIBLE
        option2Button.visibility = View.VISIBLE
        option3Button.visibility = View.VISIBLE
        option4Button.visibility = View.VISIBLE

        // Hide the next button
        findViewById<Button>(R.id.nextButton).visibility = View.INVISIBLE
    }



    private fun resetButton(button: Button) {
        button.setBackgroundColor(defaultButtonColor)
        button.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        button.text = ""
    }


    private fun enableOptionClicks() {
        val option1Button = findViewById<Button>(R.id.option1Button)
        val option2Button = findViewById<Button>(R.id.option2Button)
        val option3Button = findViewById<Button>(R.id.option3Button)
        val option4Button = findViewById<Button>(R.id.option4Button)

        option1Button.isClickable = true
        option2Button.isClickable = true
        option3Button.isClickable = true
        option4Button.isClickable = true
    }

}
