package com.example.todoapp.ui.tasks

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.model.Task
import com.example.todoapp.data.model.TaskCreateRequest
import com.example.todoapp.data.model.TaskUpdateRequest
import com.example.todoapp.data.remote.ApiService
import com.example.todoapp.data.remote.RetrofitClient
import com.example.todoapp.util.TokenManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TasksViewModel(
    private val apiService: ApiService, // Esta será a instância autenticada
    private val tokenManager: TokenManager // Adicionado para poder fazer logout
) : ViewModel() {

    val tasks = mutableStateListOf<Task>()
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var newTaskTitle by mutableStateOf("") // Para a caixa de texto de nova tarefa

    init {
        loadTasks()
    }

    fun loadTasks() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val response = apiService.getTasks()
                if (response.isSuccessful) {
                    tasks.clear()
                    tasks.addAll(response.body() ?: emptyList())
                    isLoading = false
                } else {
                    errorMessage = response.errorBody()?.string() ?: "Erro ao carregar tarefas"
                    isLoading = false
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Erro de rede ou inesperado ao carregar tarefas"
                isLoading = false
            }
        }
    }

    fun addTask() {
        if (newTaskTitle.isBlank()) {
            errorMessage = "O título da tarefa não pode ser vazio."
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val request = TaskCreateRequest(title = newTaskTitle)
                val response = apiService.createTask(request)

                if (response.isSuccessful) {
                    response.body()?.let { createdTask ->
                        tasks.add(createdTask)
                        newTaskTitle = "" // Limpa o campo
                        isLoading = false
                    } ?: run {
                        errorMessage = "Resposta vazia ao criar tarefa"
                        isLoading = false
                    }
                } else {
                    errorMessage = response.errorBody()?.string() ?: "Erro ao adicionar tarefa"
                    isLoading = false
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Erro de rede ou inesperado ao adicionar tarefa"
                isLoading = false
            }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val updatedStatus = !task.isCompleted
                val request = TaskUpdateRequest(isCompleted = updatedStatus)
                val response = apiService.updateTask(task.id, request)

                if (response.isSuccessful) {
                    response.body()?.let { updatedTask ->
                        val index = tasks.indexOfFirst { it.id == updatedTask.id }
                        if (index != -1) {
                            tasks[index] = updatedTask
                        }
                        isLoading = false
                    } ?: run {
                        errorMessage = "Resposta vazia ao atualizar tarefa"
                        isLoading = false
                    }
                } else {
                    errorMessage = response.errorBody()?.string() ?: "Erro ao atualizar tarefa"
                    isLoading = false
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Erro de rede ou inesperado ao atualizar tarefa"
                isLoading = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearAuthToken()
            // Não é necessário mudar isAuthenticated aqui, pois o AuthViewModel gerencia isso
            // e a navegação cuidará de ir para a tela de login.
        }
    }


    // Factory para instanciar o ViewModel com a dependência
    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val tokenManager = TokenManager(context)
                    // Precisa de um token para instanciar o ApiService para tarefas
                    val apiService = runBlocking {
                        val token = tokenManager.getAuthToken() ?: throw IllegalStateException("Token de autenticação não encontrado. Faça login novamente.")
                        RetrofitClient.createAuthenticatedApiService(token)
                    }
                    return TasksViewModel(apiService, tokenManager) as T
                }
            }
    }
}