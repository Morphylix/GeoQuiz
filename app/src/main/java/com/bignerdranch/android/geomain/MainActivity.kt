package com.bignerdranch.android.geomain

import android.app.ActivityOptions
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import org.w3c.dom.Text

private const val TAG = "MainActivity"
private const val KEY_INDEX = "Index"
private const val KEY_JUDGEMENT = "Judgement"
private const val KEY_CORRECT = "Correct"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var cheatButton: Button

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate(Bundle?) called")
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        val correctAnswers = savedInstanceState?.getInt(KEY_CORRECT, 0) ?: 0
        quizViewModel.correctAnswers = correctAnswers

        val judgementCount = savedInstanceState?.getInt(KEY_JUDGEMENT, 0) ?: 0
        quizViewModel.judgementCount = judgementCount

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatButton = findViewById(R.id.cheat_button)

        trueButton.setOnClickListener {
            checkAnswer(true)
            quizViewModel.currentWasAnswered = true
            checkFinished()
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
            quizViewModel.currentWasAnswered = true
            checkFinished()
        }

        questionTextView.setOnClickListener {
            quizViewModel.moveToNext(true)
            nextQuestion()
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext(true)
            nextQuestion()
        }

        prevButton.setOnClickListener {
            quizViewModel.moveToNext(false)
            nextQuestion()
        }

        cheatButton.setOnClickListener { view ->
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            val options = ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
            startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
        }

        updateQuestion()

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,
            "onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG,
            "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG,
            "onPause() called")
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG,
            "onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,
            "onDestroy() called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "OnSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        savedInstanceState.putInt(KEY_JUDGEMENT, quizViewModel.judgementCount)
        savedInstanceState.putInt(KEY_CORRECT, quizViewModel.correctAnswers)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            quizViewModel.updateWasCheatedList()
        }
    }

    private fun checkFinished() {
        var allAnswered = true
        for (wasAnswered in quizViewModel.wasAnsweredList) {
            if (!wasAnswered) {
                allAnswered = false
            }
        }
        if (allAnswered) {
            val result: Double = quizViewModel.correctAnswers.toDouble() / quizViewModel.questionBank.size.toDouble() * 100
            Toast.makeText(this, "$result% correct", Toast.LENGTH_LONG).show()
            quizViewModel.correctAnswers = 0
            quizViewModel.wasAnsweredList = MutableList(quizViewModel.questionBank.size) {false}
        }
    }

    private fun nextQuestion() {
        updateQuestion()
        setButtons(false)
        if (!quizViewModel.currentWasAnswered) {
            setButtons(true)
        }
    }

    private fun setButtons(isEnabled: Boolean) {
        trueButton.isEnabled = isEnabled
        falseButton.isEnabled = isEnabled
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId: Int
        if (quizViewModel.currentWasCheated) {
            messageResId = R.string.judgment_toast
            quizViewModel.judgementCount++
        }
        else if (userAnswer == correctAnswer) {
            messageResId = R.string.correct_toast
            quizViewModel.correctAnswers++
        }
        else {
            messageResId = R.string.incorrect_toast
        }
        setButtons(false)
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }
}