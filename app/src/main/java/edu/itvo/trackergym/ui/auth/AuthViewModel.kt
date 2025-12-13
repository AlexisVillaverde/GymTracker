package edu.itvo.trackergym.ui.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.itvo.trackergym.domain.model.UserProfile
import edu.itvo.trackergym.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val googleSignInClient: GoogleSignInClient
) : ViewModel() {

    val currentUser = repository.currentUser

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    val googleSignInIntent: Intent
        get() = googleSignInClient.signInIntent

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Por favor completa los campos")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.loginWithEmail(email, pass)
            if (result.isFailure) {
                _uiState.value = AuthUiState.Error("Error de autenticación: Verifica tus datos")
            } else {
                _uiState.value = AuthUiState.Idle // Reset on success
            }
        }
    }

    fun register(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Por favor completa los campos")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.registerWithEmail(email, pass)
            if (result.isFailure) {
                _uiState.value = AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error al registrarse")
            } else {
                 _uiState.value = AuthUiState.Idle // Reset on success
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.loginWithGoogleCredential(idToken)
            if (result.isFailure) {
                _uiState.value = AuthUiState.Error("Fallo el inicio con Google")
            } else {
                _uiState.value = AuthUiState.Idle // Reset on success
            }
        }
    }

    fun saveProfileData(age: Int, weight: Double, height: Double, goal: String) {
        viewModelScope.launch {
            val newProfile = UserProfile(
                age = age, weight = weight, height = height, goal = goal, completedSetup = true
            )
            repository.saveUserProfile(newProfile)
        }
    }

    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    fun signOut() {
        viewModelScope.launch { 
            repository.logout()
            _uiState.value = AuthUiState.Idle // Reset state
        }
    }
}