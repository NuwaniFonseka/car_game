package com.littlelemon.cargame

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.view.MotionEvent
import android.view.View

class GameView(var c: Context, var gameTask: GameTask) : View(c), GameSoundListener {
    private var myPaint: Paint? = null // for controlling appearance and style of the graphics drawn on the canvas
    private var speed = 1
    private var time = 0
    private var score = 0
    private var myCarPosition = 0
    private var otherCars = ArrayList<HashMap<String, Any>>()
    private var mediaPlayerJump: MediaPlayer? = null
    private var isSoundOn: Boolean = false


    var viewWidth = 0 //dimentions of the view
    var viewHeight = 0
    private var isGameOver = false

    init {
        myPaint = Paint()
        mediaPlayerJump = MediaPlayer.create(c, R.raw.jump)
    }
    override fun onSoundStateChanged(isSoundOn: Boolean) {
        this.isSoundOn = isSoundOn
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Release the MediaPlayer to free up resources
        mediaPlayerJump?.release()
    }



    fun resetGame() {
        // Reset game variables
        speed = 1
        time = 0
        score = 0
        myCarPosition = 0
        otherCars.clear()
        isGameOver = false
        // Invalidate the view to force a redraw
        invalidate()
    }

    override fun onDraw(canvas: Canvas) { //render game graphics
        super.onDraw(canvas)
        viewWidth = this.measuredWidth
        viewHeight = this.measuredHeight

        if (!isGameOver && time % 700 < 10 + speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherCars.add(map)
        }
        time = time + 10 + speed
        val carWidth = viewWidth / 5
        val carHeight = carWidth + 10
        myPaint!!.style = Paint.Style.FILL
        val d = resources.getDrawable(R.drawable.red, null)

        d.setBounds(
            myCarPosition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight - 2 - carHeight,
            myCarPosition * viewWidth / 3 + viewWidth / 15 + carWidth - 25,
            viewHeight - 2

        )
        d.draw(canvas!!)
        myPaint!!.color = Color.GREEN
        var highScore = 0

        for (i in otherCars.indices) {
            try { //calculate the position of the cars based on the elapsed time based on the start
                // carX - based on lane of the car and the width of the view, dividing the view into three equal parts
                // carY - by subtracting the start time of the car from the current time
                val carX = otherCars[i]["lane"] as Int * viewWidth / 3 + viewWidth / 15
                val carY = time - otherCars[i]["startTime"] as Int
                val d2 = resources.getDrawable(R.drawable.green, null)

                d2.setBounds(
                    carX + 25, carY - carHeight, carX + carWidth - 25, carY
                )
                d2.draw(canvas)  //collission detection
                // checks if the current car is in the same lane as player's car
                if (otherCars[i]["lane"] as Int == myCarPosition) {
                    if (carY > viewHeight - 2 - carHeight && carY < viewHeight - 2) {
                        gameTask.closeGame(score)
                        isGameOver = true
                        return
                    }
                }
                //checks if the other car has moved completely off the screen vertically,
                // indicating that it's no longer visible to the player.
                if (carY > viewHeight + carHeight) {
                    otherCars.removeAt(i)
                    score++
                    //calculates how many times the player's score can be divided by 8
                    // without taking into account any remainder.
                    speed = 1 + Math.abs(score / 8)
                    if (score > highScore) {
                        highScore = score
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        myPaint!!.color = Color.WHITE
        myPaint!!.textSize = 40f
        canvas.drawText("Score : $score", 80f, 80f, myPaint!!)
        canvas.drawText("Speed : $speed", 380f, 80f, myPaint!!)
        if (!isGameOver) {
            invalidate()
            //triggers a redraw of the view, ensuring that the displayed score and speed are updated
        // continuously as the game progresses.
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                // Store the initial touch position
                val x1 = event.x
                if (x1 < viewWidth / 2) {
                    if (myCarPosition > 0) {
                        myCarPosition--

                        playJumpSoundIfEnabled() //if sound enabled in settings
                    }
                }
                if (x1 > viewWidth / 2) {
                    if (myCarPosition < 2) {
                        myCarPosition++

                        playJumpSoundIfEnabled()
                    }
                }
                invalidate() // Redraw the view to update the car position
            }

            MotionEvent.ACTION_MOVE -> {
                // Update the car position while swiping
                val x = event.x
                val laneWidth = viewWidth / 3 // Assuming the screen is divided into three lanes
                val lane = (x / laneWidth).toInt() // Determine which lane the touch is in
                myCarPosition = lane.coerceIn(0, 2) // Ensure the car position stays within the valid range of lanes
                invalidate() // Redraw the view to update the car position
            }
        }
        return true
    }

    private fun playJumpSoundIfEnabled() {
        val soundEnabled = getSoundSettings()
        if (soundEnabled) {
            mediaPlayerJump?.start()
        }
    }

    private fun getSoundSettings(): Boolean {
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("sounds_switch_state", false)
    }
}