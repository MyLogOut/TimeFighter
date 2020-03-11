package com.siriosstudio.timefighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    internal var score = 0

    internal lateinit var tapMeButton: Button
    internal lateinit var scoreTextView: TextView
    internal lateinit var timerTextView: TextView

    internal var gameStarted = false

    internal lateinit var countDownTimer: CountDownTimer
    internal val initialCountDown: Long = 60000
    internal val countDownInterval: Long = 1000
    internal var timeLeftOnTimer: Long = 60000

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
        private const val GAME_STATE_KEY = "GAME_STATE_KEY"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate called. Score is: $score")
        tapMeButton = findViewById(R.id.timeFighterTapMeButton)
        scoreTextView = findViewById(R.id.timeFighterScoreTextView)
        timerTextView = findViewById(R.id.timeFighterTimerTextView)

        tapMeButton.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)

            incrementScore()
        }

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            gameStarted = savedInstanceState.getBoolean(GAME_STATE_KEY)
            Log.d(TAG, "savedInstanceState= NotNull, Game Started: $gameStarted . values passed: Score: $score, TimeLeft: $timeLeftOnTimer")
        } else resetGame()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        if (item?.itemId == R.id.actionAbout) showInfo()
        return true
    }

    private fun showInfo() {

        val dialogTitle = getString(R.string.aboutTitle, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.aboutMessage)

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(dialogTitle)
        dialogBuilder.setMessage(dialogMessage)
        dialogBuilder.create().show()
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(SCORE_KEY, score)
        outState?.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
        outState?.putBoolean(GAME_STATE_KEY, gameStarted)
        countDownTimer.cancel()

        Log.d(TAG, "onSavedInstanceState: Saving score: $score and timeLeft: $timeLeftOnTimer")
    }

    override fun onResume() {
        super.onResume()
        if (gameStarted) restoreGame()
        else resetGame()
    }

    private fun resetGame() {
        score = 0
        scoreTextView.text = getString(R.string.timeFighterScore, score)
        val initialTimeLeft = initialCountDown / 1000
        timerTextView.text = getString(R.string.timeFighterTimer, initialTimeLeft)
        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                timerTextView.text = getString(R.string.timeFighterTimer, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }

        gameStarted = false
    }

    private fun incrementScore() {
        if (!gameStarted) startGame()
        score +=1
        val newScore = getString(R.string.timeFighterScore, score)
        scoreTextView.text = newScore
        val fadeInOutAnimation = AnimationUtils.loadAnimation(scoreTextView.context, R.anim.fade_in_out)
        scoreTextView.startAnimation(fadeInOutAnimation)
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun restoreGame() {
        timeFighterScoreTextView.text = getString(R.string.timeFighterScore, score)
        val restoredTime = timeLeftOnTimer / 1000
        timeFighterTimerTextView.text = getString(R.string.timeFighterTimer, restoredTime)

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                timeFighterTimerTextView.text = getString(R.string.timeFighterTimer, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }

        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame() {
        Toast
            .makeText(this, getString(R.string.gameOverMessage, score), Toast.LENGTH_LONG)
            .show()
        resetGame()
    }
}
