package com.littlelemon.cargame

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class SettingsActivity : AppCompatActivity() {
    private lateinit var musicSwitch: Switch
    private lateinit var soundsSwitch: Switch
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize views
        musicSwitch = findViewById(R.id.musicswitch)
        soundsSwitch = findViewById(R.id.soundsswitch)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Load saved switch states
        loadSwitchStates()

        val savebtn = findViewById<TextView>(R.id.savebtn)
        savebtn.setOnClickListener {
            // Save switch states
            saveSwitchStates()

            // Navigate to another activity
            val intent = Intent(this, GetStartedActivity::class.java)
            startActivity(intent)

        }

        val scoreboardbtn = findViewById<Button>(R.id.scoreboardbtn)
        scoreboardbtn.setOnClickListener {
            val Intent = Intent( this, HighScoreActivity::class.java)
            startActivity(Intent)
        }


    }

    private fun saveHighScores(score: Int) {
        val prefs = getSharedPreferences("HighScores", Context.MODE_PRIVATE)
        val editor = prefs.edit() //applies changes
        val currentHighest = prefs.getInt("highest", 0)
        val currentSecondHighest = prefs.getInt("second_highest", 0)
        val currentThirdHighest = prefs.getInt("third_highest", 0)

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

        editor.apply()
    }

    private fun saveSwitchStates() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("music_switch_state", musicSwitch.isChecked)
        editor.putBoolean("sounds_switch_state", soundsSwitch.isChecked)
        editor.apply()

        // Notify the GameView about the state change
        (applicationContext as? GameSoundListener)?.onSoundStateChanged(soundsSwitch.isChecked)
        notifySoundStateChanged(soundsSwitch.isChecked)
    }

    // sends a local broadcast to notify
    // other components about the change in sound state.
    private fun notifySoundStateChanged(isSoundOn: Boolean) {
        val intent = Intent("com.littlelemon.cargame.SOUND_STATE_CHANGED")
        intent.putExtra("isSoundOn", isSoundOn)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
    //sends the broadcast using LocalBroadcastManager.

    private fun loadSwitchStates() {
        musicSwitch.isChecked = sharedPreferences.getBoolean("music_switch_state", false)
        soundsSwitch.isChecked = sharedPreferences.getBoolean("sounds_switch_state", false)
    }

    fun isSoundOn(): Boolean {
        return sharedPreferences.getBoolean("sounds_switch_state", false)
    }
    //returns the current state of the
// sound switch from SharedPreferences

}
