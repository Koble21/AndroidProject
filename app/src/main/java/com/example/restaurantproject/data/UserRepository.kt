package com.example.restaurantproject.data

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {
    val readAllData: LiveData<List<User>> = userDao.readAllData()

    suspend fun addUser(user: User){
        userDao.addUser(user)
    }
    fun deleteTheUsers(){
        userDao.deleteTheUsers()
    }

    fun readUserData(): LiveData<User> {
        return userDao.readUserData()
    }

    fun updateUserDatas(name: String, address: String, email: String, phone: String, image: ByteArray){
        userDao.updateUserDatas(name,address, email, phone, image)
    }
}