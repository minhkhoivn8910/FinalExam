package com.example.finalexam

import android.os.Parcel
import android.os.Parcelable

data class Vocabulary(
    var id: String? = null,
    var englishWord: String? = null,
    var vietnameseMeaning: String? = null,
    val topicName: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(englishWord)
        parcel.writeString(vietnameseMeaning)
        parcel.writeString(topicName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Vocabulary> {
        override fun createFromParcel(parcel: Parcel): Vocabulary {
            return Vocabulary(parcel)
        }

        override fun newArray(size: Int): Array<Vocabulary?> {
            return arrayOfNulls(size)
        }
    }
    fun getIncorrectOptions(allVocabularies: List<Vocabulary>, count: Int): List<String> {
        val incorrectOptions = mutableListOf<String>()
        val allEnglishWords = allVocabularies.map { it.englishWord ?: "" }

        while (incorrectOptions.size < count) {
            val randomWord = allEnglishWords.random()
            if (randomWord != this.englishWord && randomWord !in incorrectOptions) {
                incorrectOptions.add(randomWord)
            }
        }

        return incorrectOptions
    }
}

