package com.example.rewatchparty.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PartyStatus {
    Public,
    Private
}

@Entity(tableName = "party_table")
data class Party (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val password: String,
    val leader: String,
    val status: PartyStatus
)
