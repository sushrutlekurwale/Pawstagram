package com.example.pawstagram.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawstagram.data.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = AuthRepository()

    val user: StateFlow<FirebaseUser?> = repo.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repo.currentUser())

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun signInWithEmailPassword(email: String, password: String) {
        viewModelScope.launch {
            _error.value = null
            _loading.value = true
            try {
                repo.signInWithEmailPassword(email.trim(), password)
            } catch (e: Exception) {
                _error.value = getErrorMessage(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun signUpWithEmailPassword(email: String, password: String, username: String) {
        viewModelScope.launch {
            _error.value = null
            _loading.value = true
            try {
                repo.signUpWithEmailPassword(email.trim(), password, username.trim())
            } catch (e: Exception) {
                _error.value = getErrorMessage(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _error.value = null
            _loading.value = true
            try {
                repo.signInWithGoogle(idToken)
            } catch (e: Exception) {
                _error.value = getErrorMessage(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _error.value = null
            _loading.value = true
            try {
                repo.resetPassword(email.trim())
                _error.value = "Password reset email sent. Check your inbox."
            } catch (e: Exception) {
                _error.value = getErrorMessage(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun signOut() {
        repo.signOut()
    }

    private fun getErrorMessage(exception: Exception): String {
        return when (exception) {
            is FirebaseAuthException -> {
                when (exception.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Invalid email address"
                    "ERROR_WRONG_PASSWORD" -> "Wrong password"
                    "ERROR_USER_NOT_FOUND" -> "No account found with this email"
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "Email already in use"
                    "ERROR_WEAK_PASSWORD" -> "Password is too weak"
                    "ERROR_USER_DISABLED" -> "Account disabled"
                    "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Try again later"
                    else -> exception.message ?: "Authentication failed"
                }
            }
            else -> exception.message ?: "An error occurred. Please try again."
        }
    }
}


