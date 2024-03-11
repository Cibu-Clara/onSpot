package com.example.parkingspots.data.repository

import com.example.parkingspots.data.model.User
import com.example.parkingspots.utils.Resource
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun loginUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun registerUser(email: String, password: String, user: User): Flow<Resource<AuthResult>>
}