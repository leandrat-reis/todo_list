package com.example.todoapp.data.remote

import com.example.todoapp.data.model.AuthResponse
import com.example.todoapp.data.model.Task
import com.example.todoapp.data.model.TaskCreateRequest
import com.example.todoapp.data.model.TaskUpdateRequest
import com.example.todoapp.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // Endpoint para geração de token (Login)
    @POST("api/token/")
    suspend fun login(@Body user: User): Response<AuthResponse>

    // Endpoint para criação de um usuário (Cadastro)
    @POST("api/users/")
    suspend fun registerUser(@Body user: User): Response<User>

    // Endpoint para listar tarefas
    @GET("api/tasks/")
    suspend fun getTasks(): Response<List<Task>>

    // Endpoint para criar uma nova tarefa
    @POST("api/tasks/")
    suspend fun createTask(@Body task: TaskCreateRequest): Response<Task>

    // Endpoint para atualizar uma tarefa (ex: marcar como concluída)
    @PUT("api/tasks/{id}/")
    suspend fun updateTask(
        @Path("id") taskId: Int,
        @Body task: TaskUpdateRequest
    ): Response<Task>
}