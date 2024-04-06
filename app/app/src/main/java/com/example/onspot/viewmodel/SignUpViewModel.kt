package com.example.onspot.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.model.User
import com.example.onspot.data.repository.UserRepository
import com.example.onspot.data.repository.UserRepositoryImpl
import com.example.onspot.ui.states.SignUpState
import com.example.onspot.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val repository: UserRepository = UserRepositoryImpl()

    private val _signUpState = Channel<SignUpState>()
    val signUpState = _signUpState.receiveAsFlow()

    private val _googleSignUpState = Channel<SignUpState>()
    val googleSignUpState = _googleSignUpState.receiveAsFlow()

    fun registerUser(email: String, password: String, firstName: String, lastName: String) = viewModelScope.launch {
        val newUser = User(uuid = "", firstName = firstName, lastName = lastName, email = email, isAdmin = false, creationTimestamp = 0L)

        repository.registerUser(email, password, newUser).collect { result ->
            when(result) {
                is Resource.Loading -> { _signUpState.send(SignUpState(isLoading = true)) }
                is Resource.Success -> { _signUpState.send(SignUpState(isSuccess = "Sign Up Success")) }
                is Resource.Error -> { _signUpState.send(SignUpState(isError = result.message)) }
            }
        }
    }

    fun handleGoogleSignUpResult(data: Intent?) = viewModelScope.launch {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            repository.connectWithGoogle(account).collect { result ->
                when (result) {
                    is Resource.Success -> { _googleSignUpState.send(SignUpState(isSuccess = "Google Authentication Success")) }
                    is Resource.Loading -> { _googleSignUpState.send(SignUpState(isLoading = true)) }
                    is Resource.Error -> { _googleSignUpState.send(SignUpState(isError = result.message)) }
                }
            }
        } catch (e: ApiException) {
            _googleSignUpState.send(SignUpState(isError = e.localizedMessage))
        }
    }
}