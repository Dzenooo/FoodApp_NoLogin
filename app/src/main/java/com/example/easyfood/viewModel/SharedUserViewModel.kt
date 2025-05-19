package com.example.easyfood.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.easyfood.pojo.User

class SharedUserViewModel : ViewModel() {
    private val _loggedInUser = MutableLiveData<User?>()
    val loggedInUser: LiveData<User?> get() = _loggedInUser

    fun setUser(user: User) {
        _loggedInUser.value = user
    }

    fun clearUser() {
        _loggedInUser.value = null
    }
}
