package com.example.onspot.data.repository

import com.example.onspot.data.model.User
import com.example.onspot.utils.Resource
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun loginUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun logoutUser()
    fun registerUser(email: String, password: String, user: User): Flow<Resource<AuthResult>>
    fun deleteUserAccount(): Flow<Resource<Void?>>
    suspend fun verifyPassword(password: String): Boolean
    fun sendPasswordResetEmail(email: String): Flow<Resource<Void?>>
}