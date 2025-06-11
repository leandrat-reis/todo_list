package com.example.todoapp.data.model

import com.google.gson.annotations.SerializedName

data class Task(
    val id: Int,
    val title: String,
    @SerializedName("is_completed") val isCompleted: Boolean, // Se a tarefa foi concluída
    val owner: Int, // O ID do usuário proprietário da tarefa
    @SerializedName("created_at") val createdAt: String, // Data de criação
    @SerializedName("updated_at") val updatedAt: String? = null // Data de atualização (pode ser nula)
)

// Data class para criar uma nova tarefa (não precisa de ID ao criar)
data class TaskCreateRequest(
    val title: String,
    @SerializedName("is_completed") val isCompleted: Boolean = false // Por padrão, não concluída
)

// Data class para atualizar uma tarefa (geralmente para marcar como concluída)
data class TaskUpdateRequest(
    val title: String? = null, // Pode ser nulo se não for atualizar o título
    @SerializedName("is_completed") val isCompleted: Boolean? = null // Pode ser nulo se não for atualizar o status
)