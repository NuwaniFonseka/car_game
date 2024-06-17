package com.littlelemon.cargame

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class GetStartedActivity : AppCompatActivity(), GameTask {
    lateinit var rootLayout: ConstraintLayout
    lateinit var startBtn: Button
    lateinit var mGameView: GameView
    lateinit var score: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)
        startBtn = findViewById(R.id.startBtn)
        rootLayout = findViewById(R.id.rootLayout)
        score = findViewById(R.id.score)


        // Create the GameView instance only once when the activity is created
        mGameView = GameView(this, this)

        startBtn.setOnClickListener {
            // Check if the GameView is already added to the layout
            if (rootLayout.indexOfChild(mGameView) == -1) {

                mGameView.apply {
                    // Set the background to the moving_lane animation list
                    val animationDrawable = ContextCompat.getDrawable(context, R.drawable.moving_lane) as AnimationDrawable
                    background = animationDrawable
                    animationDrawable.start()
                }
                rootLayout.addView(mGameView)
            }

            // Reset the game variables
            mGameView.resetGame()

            // Hide start button and score
            startBtn.visibility = View.GONE
            score.visibility = View.GONE
        }

        val settingsBtn = findViewById<Button>(R.id.settingsbtn)
        settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }


    }

    override fun closeGame(mScore: Int) {
        score.text = "     $mScore"
        // Remove the GameView from the layout
        rootLayout.removeView(mGameView)
        // Show start button and score
        startBtn.visibility = View.VISIBLE
        score.visibility = View.VISIBLE
        saveScore(mScore)
    }

    override fun saveScore(score: Int) {
        // Save the score using SharedPreferences
        val settings = getSharedPreferences("HighScores", Context.MODE_PRIVATE)
        val editor = settings.edit() //Retrieves an editor object to modify shared preferences

        val currentHighest = settings.getInt("highest", 0)
        val currentSecondHighest = settings.getInt("second_highest", 0)
        val currentThirdHighest = settings.getInt("third_highest", 0)

        if (score > currentHighest) {
            editor.putInt("highest", score)
            editor.putInt("second_highest", currentHighest)
            editor.putInt("third_highest", currentSecondHighest)
        } else if (score > currentSecondHighest) {
            editor.putInt("second_highest", score)
            editor.putInt("third_highest", currentSecondHighest)
        } else if (score > currentThirdHighest) {
            editor.putInt("third_highest", score)
        }

        editor.apply() //commits changes to shared preferences.
    }
}