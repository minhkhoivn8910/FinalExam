package com.example.finalexam

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class FlashcardPagerAdapter(
    fragmentManager: FragmentManager,
    private val vocabularyList: List<Vocabulary>
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val vocabulary = vocabularyList[position]
        return FlashcardFragment.newInstance(vocabulary.englishWord.toString(), vocabulary.vietnameseMeaning.toString())
    }

    override fun getCount(): Int {
        return vocabularyList.size
    }
}