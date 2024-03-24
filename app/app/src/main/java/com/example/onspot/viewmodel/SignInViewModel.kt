package com.example.onspot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.repository.UserRepository
import com.example.onspot.data.repository.UserRepositoryImpl
import com.example.onspot.ui.states.ResetPasswordState
import com.example.onspot.ui.states.SignInState
import com.example.onspot.utils.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {
    private val repository: UserRepository = UserRepositoryImpl()

    private val _signInState = Channel<SignInState>()
    val signInState = _signInState.receiveAsFlow()

    private val _resetPasswordState = Channel<ResetPasswordState>()
    val resetPasswordState = _resetPasswordState.receiveAsFlow()

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        repository.loginUser(email, password).collect { result ->
            when(result) {
                is Resource.Success -> { _signInState.send(SignInState(isSuccess = "Sign In Success")) }
                is Resource.Loading -> { _signInState.send(SignInState(isLoading = true)) }
                is Resource.Error -> { _signInState.send(SignInState(isError = result.message)) }
            }
        }
    }

    fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        repository.sendPasswordResetEmail(email).collect { result ->
            when(result) {
                is Resource.Success -> { _resetPasswordState.send(ResetPasswordState(isSuccess = "Password reset email successfully sent")) }
                is Resource.Loading -> { _resetPasswordState.send(ResetPasswordState(isLoading = true)) }
                is Resource.Error -> { _resetPasswordState.send(ResetPasswordState(isError = result.message)) }
            }
        }
    }
}