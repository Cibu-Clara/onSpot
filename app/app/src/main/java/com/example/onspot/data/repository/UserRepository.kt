package com.example.onspot.data.repository

import com.example.onspot.data.model.User
import com.example.onspot.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun loginWithEmailAndPassword(email: String, password: String): Flow<Resource<AuthResult>>
    fun connectWithGoogle(account: GoogleSignInAccount): Flow<Resource<FirebaseUser>>
    suspend fun logoutUser(googleSignInClient: GoogleSignInClient?): Flow<Resource<Void?>>
    fun registerUser(email: String, password: String, user: User): Flow<Resource<AuthResult>>
    fun deleteUserAccount(googleSignInClient: GoogleSignInClient?): Flow<Resource<Void?>>
    suspend fun verifyPassword(password: String): Boolean
    fun sendPasswordResetEmail(email: String): Flow<Resource<Void?>>
    fun changePassword(currentPassword: String, newPassword: String): Flow<Resource<Void?>>
    suspend fun getCurrentUserAuthProvider(): String?
}