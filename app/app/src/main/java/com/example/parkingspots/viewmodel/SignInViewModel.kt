package com.example.parkingspots.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingspots.data.repository.UserRepository
import com.example.parkingspots.data.repository.UserRepositoryImpl
import com.example.parkingspots.ui.states.SignInState
import com.example.parkingspots.utils.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {
    private val repository: UserRepository = UserRepositoryImpl()

    private val _signInState = Channel<SignInState>()
    val signInState = _signInState.receiveAsFlow()

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        repository.loginUser(email, password).collect { result ->
            when(result) {
                is Resource.Success -> { _signInState.send(SignInState(isSuccess = "Sign In Success")) }
                is Resource.Loading -> { _signInState.send(SignInState(isLoading = true)) }
                is Resource.Error -> { _signInState.send(SignInState(isError = result.message)) }
            }
        }
    }
}