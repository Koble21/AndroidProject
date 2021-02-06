package com.example.restaurantproject.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application): AndroidViewModel(application) {

    private val readAllData: LiveData<List<User>>
    private val repository: UserRepository

    init{
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        readAllData = repository.readAllData
    }

    fun addUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(user)
        }
    }
    fun deleteTheUsers(){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteTheUsers()
        }
    }

    fun readUserData(): LiveData<User>{
        return repository.readUserData()
    }

    fun updateUserDatas(name: String, address: String, email: String, phone: String, image: ByteArray){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateUserDatas(name, address, email, phone, image)
        }
    }
}