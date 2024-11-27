package com.example.rewatchparty

data class Room(
    val roomId: String = "",
    val password: String = "",
    val leaderStatus: Boolean = false,
    val currentTime: Double = 0.0,
    val isPaused: Boolean = false
)
