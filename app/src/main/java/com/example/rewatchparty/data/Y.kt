package com.example.rewatchparty.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity (
    @PrimaryKey
    val videoId: String,
    val title: String,
    val description: String,
    val channelId: String)
}