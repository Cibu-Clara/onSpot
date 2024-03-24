package com.example.onspot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.repository.UserRepository
import com.example.onspot.data.repository.UserRepositoryImpl
import com.example.onspot.ui.states.DeleteAccountState
import com.example.onspot.utils.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {
    private val userRepository: UserRepository = UserRepositoryImpl()

    private val _deleteAccountState = Channel<DeleteAccountState>()
    val deleteAccountState = _deleteAccountState.receiveAsFlow()

    fun logoutUser() {
        userRepository.logoutUser()
    }
    fun deleteUserAccount() = viewModelScope.launch {
        userRepository.deleteUserAccount().collect { result ->
            when(result) {
                is Resource.Loading -> { _deleteAccountState.send(DeleteAccountState(isLoading = true)) }
                is Resource.Success -> { _deleteAccountState.send(DeleteAccountState(isSuccess = "Account deleted successfully")) }
                is Resource.Error -> { _deleteAccountState.send(DeleteAccountState(isError = result.message)) }
            }
        }
    }

    fun verifyPassword(password: String, callback: (Boolean) -> Unit) = viewModelScope.launch {
        val isPasswordCorrect = userRepository.verifyPassword(password)
        callback(isPasswordCorrect)
    }
}