package com.example.rewatchparty.data

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {

    val getAllData: LiveData<List<User>> = userDao.getAllUsers()

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

}