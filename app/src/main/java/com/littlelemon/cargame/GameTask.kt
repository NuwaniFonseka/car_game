package com.littlelemon.cargame

interface GameTask {
    fun closeGame(score: Int)
    fun saveScore(score: Int)
}
