package com.example.parkingspots.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingspots.data.model.User
import com.example.parkingspots.data.repository.UserRepository
import com.example.parkingspots.data.repository.UserRepositoryImpl
import com.example.parkingspots.ui.states.SignUpState
import com.example.parkingspots.utils.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val repository: UserRepository = UserRepositoryImpl()

    private val _signUpState = Channel<SignUpState>()
    val signUpState = _signUpState.receiveAsFlow()

    fun registerUser(email: String, password: String, firstName: String, lastName: String) = viewModelScope.launch {
        val newUser = User(uuid = "", firstName = firstName, lastName = lastName, email = email, isAdmin = false)

        repository.registerUser(email, password, newUser).collect { result ->
            when(result) {
                is Resource.Loading -> { _signUpState.send(SignUpState(isLoading = true)) }
                is Resource.Success -> { _signUpState.send(SignUpState(isSuccess = "Sign Up Success")) }
                is Resource.Error -> { _signUpState.send(SignUpState(isError = result.message)) }
            }
        }
    }
}