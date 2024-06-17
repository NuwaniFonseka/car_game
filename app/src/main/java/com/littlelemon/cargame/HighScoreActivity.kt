package com.littlelemon.cargame

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class HighScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_score)

        val prefs = getSharedPreferences("HighScores", Context.MODE_PRIVATE)
        val highestScore = prefs.getInt("highest", 0)
        val secondHighestScore = prefs.getInt("second_highest", 0)
        val thirdHighestScore = prefs.getInt("third_highest", 0)

        val score1 = findViewById<TextView>(R.id.score1)
        val score2 = findViewById<TextView>(R.id.score2)
        val score3 = findViewById<TextView>(R.id.score3)

        score1.text = "1st Score: $highestScore"
        score2.text = "2nd Score: $secondHighestScore"
        score3.text = "3rd Score: $thirdHighestScore"

    }
}