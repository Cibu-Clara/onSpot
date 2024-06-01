package com.example.onspot.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.data.model.User
import com.example.onspot.data.model.Vehicle
import com.example.onspot.data.repository.ParkingSpotRepository
import com.example.onspot.data.repository.ParkingSpotRepositoryImpl
import com.example.onspot.data.repository.UserRepository
import com.example.onspot.data.repository.UserRepositoryImpl
import com.example.onspot.data.repository.VehicleRepository
import com.example.onspot.data.repository.VehicleRepositoryImpl
import com.example.onspot.ui.states.ChangePasswordState
import com.example.onspot.ui.states.DeleteAccountState
import com.example.onspot.ui.states.ProfilePictureState
import com.example.onspot.ui.states.SignOutState
import com.example.onspot.ui.states.UpdateUserDetailsState
import com.example.onspot.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {
    private val userRepository: UserRepository = UserRepositoryImpl()
    private val parkingSpotRepository: ParkingSpotRepository = ParkingSpotRepositoryImpl()
    private val vehicleRepository: VehicleRepository = VehicleRepositoryImpl()

    private val _currentUserDetails = MutableStateFlow<Resource<User>>(Resource.Loading())
    val currentUserDetails: StateFlow<Resource<User>> = _currentUserDetails.asStateFlow()

    private val _userDetails = MutableStateFlow<Resource<User>>(Resource.Loading())
    val userDetails: StateFlow<Resource<User>> = _userDetails.asStateFlow()

    private val _parkingSpots = MutableStateFlow<Resource<List<ParkingSpot>>>(Resource.Loading())
    private val _vehicles = MutableStateFlow<Resource<List<Vehicle>>>(Resource.Loading())

    private val _deleteAccountState = Channel<DeleteAccountState>()
    val deleteAccountState = _deleteAccountState.receiveAsFlow()

    private val _logoutState = Channel<SignOutState>()
    val logoutState = _logoutState.receiveAsFlow()

    private val _changePasswordState = Channel<ChangePasswordState>()
    val changePasswordState = _changePasswordState.receiveAsFlow()

    private val _changeProfilePictureState = Channel<ProfilePictureState>()
    val changeProfilePictureState = _changeProfilePictureState.receiveAsFlow()

    private val _deleteProfilePictureState = Channel<ProfilePictureState>()
    val deleteProfilePictureState = _deleteProfilePictureState.receiveAsFlow()

    private val _updateUserDetailsState = Channel<UpdateUserDetailsState>()
    val updateUserDetailsState = _updateUserDetailsState.receiveAsFlow()

    val combinedLoadState: StateFlow<Resource<Triple<User, List<ParkingSpot>, List<Vehicle>>>> =
        combine(_currentUserDetails, _parkingSpots, _vehicles) { userDetails, parkingSpots, vehicles ->
            when {
                userDetails is Resource.Loading || parkingSpots is Resource.Loading || vehicles is Resource.Loading -> Resource.Loading()
                userDetails is Resource.Error -> Resource.Error(userDetails.message ?: "Error fetching user details")
                parkingSpots is Resource.Error -> Resource.Error(parkingSpots.message ?: "Error fetching parking spots")
                vehicles is Resource.Error -> Resource.Error(vehicles.message ?: "Error fetching vehicles")
                userDetails is Resource.Success && parkingSpots is Resource.Success && vehicles is Resource.Success -> {
                    if (userDetails.data != null && parkingSpots.data != null && vehicles.data != null) {
                        Resource.Success(Triple(userDetails.data, parkingSpots.data, vehicles.data))
                    } else {
                        Resource.Error("Incomplete data")
                    }
                }
                else -> Resource.Error("Unexpected error")
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading())

    init {
        fetchCurrentUserDetails()
        fetchParkingSpots()
        fetchVehicles()
    }

    private fun fetchCurrentUserDetails() = viewModelScope.launch {
        userRepository.getCurrentUserDetails().collect { userDetailsResource ->
            _currentUserDetails.value = userDetailsResource
        }
    }

    fun fetchOtherUserDetails(userId: String) = viewModelScope.launch {
        userRepository.getUserById(userId).collect { userDetailsResource ->
            _userDetails.value = userDetailsResource
        }
    }

    private fun fetchParkingSpots() = viewModelScope.launch {
        parkingSpotRepository.getParkingSpots().collect { parkingSpotsResource ->
            _parkingSpots.value = parkingSpotsResource
        }
    }

    private fun fetchVehicles() = viewModelScope.launch {
        vehicleRepository.getVehicles().collect {vehicleResource ->
            _vehicles.value = vehicleResource
        }
    }

    fun logoutUser(googleSignInClient: GoogleSignInClient?) = viewModelScope.launch {
        userRepository.logoutUser(googleSignInClient).collect { result ->
            when(result) {
                is Resource.Loading -> { _logoutState.send(SignOutState(isLoading = true)) }
                is Resource.Success -> { _logoutState.send(SignOutState(isSuccess = "Logged out successfully")) }
                is Resource.Error -> { _logoutState.send(SignOutState(isError = result.message)) }
            }
        }
    }

    fun deleteUserAccount(googleSignInClient: GoogleSignInClient?) = viewModelScope.launch {
        userRepository.deleteUserAccount(googleSignInClient).collect { result ->
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

    fun changeUserPassword(currentPassword: String, newPassword: String) = viewModelScope.launch {
        userRepository.changePassword(currentPassword, newPassword).collect { result ->
            when(result) {
                is Resource.Loading -> { _changePasswordState.send(ChangePasswordState(isLoading = true)) }
                is Resource.Success -> { _changePasswordState.send(ChangePasswordState(isSuccess = "Password changed successfully")) }
                is Resource.Error -> { _changePasswordState.send(ChangePasswordState(isError = result.message)) }
            }
        }
    }

    fun navigateForPasswordChange(callback: (Boolean, String?) -> Unit) = viewModelScope.launch {
        when (userRepository.getCurrentUserAuthProvider()) {
            "password" -> { callback(true, null) }
            "google.com" -> { callback(false, "For security reasons, please change your password directly via Google's account management.") }
        }
    }

    fun verifyAuthProvider(callback: (Boolean) -> Unit) = viewModelScope.launch {
        when (userRepository.getCurrentUserAuthProvider()) {
            "password" -> { callback(true) }
            else -> { callback(false) }
        }
    }

    fun updateUserProfilePictureUrl(imageUri: Uri) = viewModelScope.launch {
        userRepository.updateUserProfilePicture(imageUri).collect { result ->
            when (result) {
                is Resource.Loading -> { _changeProfilePictureState.send(ProfilePictureState(isLoading = true)) }
                is Resource.Success -> {
                    val currentData = _currentUserDetails.value.data
                    val profilePictureUrl = result.data
                    if (currentData != null && profilePictureUrl != null) {
                        val updatedUser = currentData.copy(profilePictureUrl = result.data)
                        _currentUserDetails.value = Resource.Success(updatedUser)
                        _changeProfilePictureState.send(ProfilePictureState(isSuccess = "Profile picture successfully updated"))
                    } else {
                        _changeProfilePictureState.send(ProfilePictureState(isError = "Cannot update profile picture"))
                    }
                }
                is Resource.Error -> { _changeProfilePictureState.send(ProfilePictureState(isError = result.message)) }
            }
        }
    }

    fun deleteUserProfilePictureUrl() = viewModelScope.launch {
        userRepository.deleteUserProfilePicture().collect { result ->
            when (result) {
                is Resource.Loading -> { _deleteProfilePictureState.send(ProfilePictureState(isLoading = true)) }
                is Resource.Success -> {
                    val currentData = _currentUserDetails.value.data
                    if (currentData != null) {
                        val updatedUser = currentData.copy(profilePictureUrl = "")
                        _currentUserDetails.value = Resource.Success(updatedUser)
                        _deleteProfilePictureState.send(ProfilePictureState(isSuccess = "Profile picture successfully deleted"))
                    } else {
                        _deleteProfilePictureState.send(ProfilePictureState(isError = "Cannot delete profile picture"))
                    }
                }
                is Resource.Error -> { _deleteProfilePictureState.send(ProfilePictureState(isError = result.message)) }
            }
        }
    }

    fun updateUserDetails(firstName: String, lastName: String) = viewModelScope.launch {
        val currentUser = currentUserDetails.value.data
        if (currentUser != null) {
            userRepository.updateUserDetails(currentUser.uuid, firstName, lastName).collect { result ->
                when (result) {
                    is Resource.Loading -> { _updateUserDetailsState.send(UpdateUserDetailsState(isLoading = true)) }
                    is Resource.Success -> {
                        val updatedUser = currentUser.copy(firstName = firstName, lastName = lastName)
                        _currentUserDetails.value = Resource.Success(updatedUser)
                        _updateUserDetailsState.send(UpdateUserDetailsState(isSuccess = "User details successfully updated"))
                    }
                    is Resource.Error -> { _updateUserDetailsState.send(UpdateUserDetailsState(isError = result.message)) }
                }
            }
        }
    }
}