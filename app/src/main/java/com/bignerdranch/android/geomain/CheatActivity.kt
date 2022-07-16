package com.bignerdranch.android.geomain

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

private const val EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geomain.answer_is_true"
const val EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown"
private const val KEY_ANSWER_WAS_SHOWN = "AnswerWasShown"



class CheatActivity : AppCompatActivity() {

    private lateinit var showAnswerButton: Button
    private lateinit var answerTextView: TextView
    private var answerIsTrue = false

    private val cheatActivityViewModel: CheatActivityViewModel by lazy {
        ViewModelProviders.of(this).get(CheatActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        val answerWasShown = savedInstanceState?.getBoolean(KEY_ANSWER_WAS_SHOWN) ?: false
        cheatActivityViewModel.answerWasShown = answerWasShown

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        showAnswerButton = findViewById(R.id.show_answer_button)
        answerTextView = findViewById(R.id.answer_text_view)



        showAnswerButton.setOnClickListener {
            cheatActivityViewModel.answerWasShown = true
            setAnswerText()
        }
        if (cheatActivityViewModel.answerWasShown) {
            setAnswerText()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_ANSWER_WAS_SHOWN, cheatActivityViewModel.answerWasShown)
    }

    private fun setAnswerText() {
        val answerText = when {
            answerIsTrue ->
                R.string.true_button
            else ->
                R.string.false_button
        }
        answerTextView.setText(answerText)
        setAnswerShownResult(true)
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }

        }
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(RESULT_OK, data)
    }
}