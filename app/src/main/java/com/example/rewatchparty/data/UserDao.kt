package com.example.rewatchparty.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user_table ORDER BY id ASC")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE email = :email")
    fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM user_table WHERE userName = :userName")
    fun getUserByUserName(userName: String): User?

    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password")
    fun getUserByEmailAndPassword(email: String, password: String): User?


    @Query("UPDATE user_table SET roomID = :roomID WHERE email = :email")
    fun updateUserRoomID(email: String, roomID: String)






}