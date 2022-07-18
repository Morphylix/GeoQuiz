package com.bignerdranch.android.geomain

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"
const val HINTS_LEFT = 3

class QuizViewModel: ViewModel() {

    val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    var wasAnsweredList = MutableList(questionBank.size) {false}
    private var wasCheatedList = MutableList(questionBank.size) {false}
    var currentIndex = 0
    var correctAnswers = 0
    var isCheater = false
    var hintsLeft = HINTS_LEFT
    val currentQuestionAnswer: Boolean
    get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
    get() = questionBank[currentIndex].textResId

    var currentWasAnswered: Boolean
    get() = wasAnsweredList[currentIndex]
    set(value) { wasAnsweredList[currentIndex] = value }

    var currentWasCheated: Boolean
    get() = wasCheatedList[currentIndex]
    set(value) { wasCheatedList[currentIndex] = value }

    fun moveToNext(dir: Boolean) {
        currentIndex = if (dir) {
            (currentIndex + 1) % questionBank.size
        } else {
            val nextId = currentIndex - 1
            if (nextId >= 0) nextId
            else questionBank.size - 1
        }
        isCheater = wasCheatedList[currentIndex]
    }

    fun updateWasCheatedList() {
        wasCheatedList[currentIndex] = true
    }

}