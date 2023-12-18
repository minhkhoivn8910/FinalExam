package com.example.finalexam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class FlashcardFragment : Fragment() {

    companion object {
        private const val ARG_ENGLISH_WORD = "english_word"
        private const val ARG_VIETNAMESE_MEANING = "vietnamese_meaning"

        fun newInstance(englishWord: String, vietnameseMeaning: String): FlashcardFragment {
            val fragment = FlashcardFragment()
            val args = Bundle()
            args.putString(ARG_ENGLISH_WORD, englishWord)
            args.putString(ARG_VIETNAMESE_MEANING, vietnameseMeaning)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var englishWordTextView: TextView
    private lateinit var vietnameseMeaningTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_flashcard, container, false)
        englishWordTextView = view.findViewById(R.id.englishWordTextView)
        vietnameseMeaningTextView = view.findViewById(R.id.vietnameseMeaningTextView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val englishWord = it.getString(ARG_ENGLISH_WORD, "")
            val vietnameseMeaning = it.getString(ARG_VIETNAMESE_MEANING, "")
            englishWordTextView.text = englishWord
            vietnameseMeaningTextView.text = vietnameseMeaning
        }
    }
}