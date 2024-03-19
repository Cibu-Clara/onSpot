package com.example.parkingspots.viewmodel

import androidx.lifecycle.ViewModel
import com.example.parkingspots.data.repository.UserRepository
import com.example.parkingspots.data.repository.UserRepositoryImpl

class UserProfileViewModel : ViewModel() {
    private val userRepository: UserRepository = UserRepositoryImpl()

    fun logoutUser() {
        userRepository.logoutUser()
    }
}