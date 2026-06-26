package com.example.rubberscan

import android.app.Application
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rubberscan.db.AppDatabase
import com.example.rubberscan.db.ScanRepository
import com.example.rubberscan.db.entity.User
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val auth = FirebaseAuth.getInstance()
    private val repository: ScanRepository

    init {
        val db = AppDatabase.getInstance(app)
        repository = ScanRepository(db.userDao(), db.scanHistoryDao())
    }

    private val _loginState = MutableStateFlow(AuthUiState())
    val loginState: StateFlow<AuthUiState> = _loginState

    private val _signUpState = MutableStateFlow(AuthUiState())
    val signUpState: StateFlow<AuthUiState> = _signUpState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        // Restore session if user is already logged in
        auth.currentUser?.let { firebaseUser ->
            viewModelScope.launch {
                val stored = repository.getUser(firebaseUser.uid)
                _currentUser.value = stored
            }
        }
    }

    fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = AuthUiState(error = "Please fill in all fields.")
            return
        }
        _loginState.value = AuthUiState(isLoading = true)
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val firebaseUser = result.user ?: return@addOnSuccessListener
                val user = User(
                    userId = firebaseUser.uid,
                    name = firebaseUser.displayName ?: email.substringBefore("@"),
                    email = firebaseUser.email ?: email,
                    profilePicUrl = firebaseUser.photoUrl?.toString() ?: ""
                )
                viewModelScope.launch {
                    repository.saveUser(user)
                    _currentUser.value = user
                    _loginState.value = AuthUiState()
                    onSuccess()
                }
            }
            .addOnFailureListener { e ->
                _loginState.value = AuthUiState(error = friendlyAuthError(e))
            }
    }

    fun signUpWithEmail(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit
    ) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _signUpState.value = AuthUiState(error = "Please fill in all fields.")
            return
        }
        if (password != confirmPassword) {
            _signUpState.value = AuthUiState(error = "Passwords do not match.")
            return
        }
        if (password.length < 6) {
            _signUpState.value = AuthUiState(error = "Password must be at least 6 characters.")
            return
        }
        _signUpState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
                val firebaseUser = result.user ?: return@launch

                // Save display name to Firebase profile
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name.trim())
                    .build()
                firebaseUser.updateProfile(profileUpdate).await()

                val user = User(
                    userId = firebaseUser.uid,
                    name = name.trim(),
                    email = email.trim(),
                    profilePicUrl = ""
                )
                repository.saveUser(user)
                _currentUser.value = user
                _signUpState.value = AuthUiState()
                onSuccess()
            } catch (e: Exception) {
                _signUpState.value = AuthUiState(error = friendlyAuthError(e))
            }
        }
    }

    fun signInWithGoogle(context: Context, isSignUp: Boolean = false, onSuccess: () -> Unit) {
        val state = if (isSignUp) _signUpState else _loginState
        state.value = AuthUiState(isLoading = true)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId("330871577103-1o833fkrfg60nuvq4rjv9q1mhjaf8764.apps.googleusercontent.com")
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                val result = credentialManager.getCredential(context, request)
                val idToken = GoogleIdTokenCredential.createFrom(result.credential.data).idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                val firebaseUser = authResult.user ?: return@launch

                val user = User(
                    userId = firebaseUser.uid,
                    name = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "User",
                    email = firebaseUser.email ?: "",
                    profilePicUrl = firebaseUser.photoUrl?.toString() ?: ""
                )
                repository.saveUser(user)
                _currentUser.value = user
                state.value = AuthUiState()
                onSuccess()
            } catch (e: GetCredentialException) {
                state.value = AuthUiState(error = "Google sign-in was cancelled. Please try again.")
            } catch (e: Exception) {
                state.value = AuthUiState(error = friendlyAuthError(e))
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }

    fun clearLoginError() {
        _loginState.value = _loginState.value.copy(error = null)
    }

    fun clearSignUpError() {
        _signUpState.value = _signUpState.value.copy(error = null)
    }

    private fun friendlyAuthError(e: Exception): String {
        val code = (e as? FirebaseAuthException)?.errorCode ?: ""
        return when (code) {
            "ERROR_INVALID_EMAIL" ->
                "That doesn't look like a valid email address. Please check and try again."
            "ERROR_USER_NOT_FOUND" ->
                "No account found with that email. Would you like to sign up instead?"
            "ERROR_WRONG_PASSWORD",
            "ERROR_INVALID_CREDENTIAL" ->
                "Incorrect email or password. Please try again."
            "ERROR_TOO_MANY_REQUESTS" ->
                "Too many failed attempts. Please wait a moment before trying again."
            "ERROR_USER_DISABLED" ->
                "This account has been disabled. Please contact support for help."
            "ERROR_EMAIL_ALREADY_IN_USE" ->
                "An account with this email already exists. Try logging in instead."
            "ERROR_WEAK_PASSWORD" ->
                "Your password is too weak. Please choose a stronger one."
            "ERROR_NETWORK_REQUEST_FAILED" ->
                "No internet connection. Please check your network and try again."
            else -> "Something went wrong. Please try again."
        }
    }
}
