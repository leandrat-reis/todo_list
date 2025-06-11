package com.example.todoapp.ui.auth

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.model.User
import com.example.todoapp.data.remote.RetrofitClient
import com.example.todoapp.util.TokenManager
import kotlinx.coroutines.launch

class AuthViewModel(
    private val tokenManager: TokenManager,
    private val apiService: RetrofitClient
) : ViewModel() {

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var firstName by mutableStateOf("") // Para registro
    var lastName by mutableStateOf("") // Para registro

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isAuthenticated by mutableStateOf(false) // Para verificar se o usuário está logado
    var registrationSuccess by mutableStateOf(false) // Para indicar sucesso no registro

    init {
        // Verifica o token salvo ao iniciar o ViewModel
        viewModelScope.launch {
            isAuthenticated = tokenManager.getAuthToken() != null
        }
    }

    fun login() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val user = User(username = username, password = password)
                val response = apiService.unauthenticatedApiService.login(user)

                if (response.isSuccessful) {
                    response.body()?.let { authResponse ->
                        tokenManager.saveAuthToken(authResponse.token)
                        isAuthenticated = true
                        isLoading = false
                    } ?: run {
                        errorMessage = "Resposta vazia do login"
                        isLoading = false
                    }
                } else {
                    errorMessage = response.errorBody()?.string() ?: "Erro desconhecido no login"
                    isLoading = false
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Erro de rede ou inesperado ao fazer login"
                isLoading = false
            }
        }
    }

    fun register() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val user = User(
                    username = username,
                    password = password,
                    firstName = firstName,
                    lastName = lastName
                )
                val response = apiService.unauthenticatedApiService.registerUser(user)

                if (response.isSuccessful) {
                    registrationSuccess = true
                    isLoading = false
                    // Opcional: Logar o usuário automaticamente após o registro
                    // login()
                } else {
                    errorMessage = response.errorBody()?.string() ?: "Erro desconhecido ao registrar"
                    isLoading = false
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Erro de rede ou inesperado ao registrar"
                isLoading = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearAuthToken()
            isAuthenticated = false
            // Limpar estados
            username = ""
            password = ""
            firstName = ""
            lastName = ""
            errorMessage = null
            registrationSuccess = false
        }
    }

    fun resetRegistrationState() {
        registrationSuccess = false
    }

    // Factory para instanciar o ViewModel com as dependências
    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(
                        tokenManager = TokenManager(context),
                        apiService = RetrofitClient
                    ) as T
                }
            }
    }
}