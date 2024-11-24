package com.example.rewatchparty.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy


@Dao
interface PartyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertParty(party: Party)

    @Query("SELECT * FROM party_table ORDER BY id ASC")
    fun getAllParties(): List<Party>




}