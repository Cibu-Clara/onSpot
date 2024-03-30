package com.example.onspot.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.repository.UserRepository
import com.example.onspot.data.repository.UserRepositoryImpl
import com.example.onspot.ui.states.ResetPasswordState
import com.example.onspot.ui.states.SignInState
import com.example.onspot.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {
    private val repository: UserRepository = UserRepositoryImpl()

    private val _signInState = Channel<SignInState>()
    val signInState = _signInState.receiveAsFlow()

    private val _resetPasswordState = Channel<ResetPasswordState>()
    val resetPasswordState = _resetPasswordState.receiveAsFlow()

    private val _googleSignInState = Channel<SignInState>()
    val googleSignInState = _googleSignInState.receiveAsFlow()

    fun loginWithEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        repository.loginWithEmailAndPassword(email, password).collect { result ->
            when(result) {
                is Resource.Success -> { _signInState.send(SignInState(isSuccess = "Sign In Success")) }
                is Resource.Loading -> { _signInState.send(SignInState(isLoading = true)) }
                is Resource.Error -> { _signInState.send(SignInState(isError = result.message)) }
            }
        }
    }

    fun handleGoogleSignInResult(data: Intent?) = viewModelScope.launch {
        try {
            _googleSignInState.send(SignInState(isLoading = true))
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            repository.connectWithGoogle(account).collect { result ->
                when (result) {
                    is Resource.Success -> { _googleSignInState.send(SignInState(isSuccess = "Google Authentication Success")) }
                    is Resource.Loading -> { _googleSignInState.send(SignInState(isLoading = true)) }
                    is Resource.Error -> { _googleSignInState.send(SignInState(isError = result.message)) }
                }
            }
        } catch (e: ApiException) {
            _googleSignInState.send(SignInState(isError = e.localizedMessage))
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