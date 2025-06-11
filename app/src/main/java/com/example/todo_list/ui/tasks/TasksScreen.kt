package com.example.todoapp.ui.tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.data.model.Task
import com.example.todoapp.ui.theme.TodoAppTheme
import androidx.compose.material.icons.automirrored.filled.ExitToApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tasksViewModel: TasksViewModel = viewModel(factory = TasksViewModel.provideFactory(context))

    TodoAppTheme { // <-- Chamada do tema
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Minhas Tarefas") },
                    actions = {
                        IconButton(onClick = {
                            tasksViewModel.logout()
                            onLogout()
                        }) {
                            // Uso do ícone AutoMirrored para acessibilidade LTR/RTL
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sair")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { tasksViewModel.addTask() }) {
                    Icon(Icons.Filled.Add, "Adicionar nova tarefa")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = tasksViewModel.newTaskTitle,
                    onValueChange = { tasksViewModel.newTaskTitle = it },
                    label = { Text("Nova Tarefa") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (tasksViewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }

                tasksViewModel.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (tasksViewModel.tasks.isEmpty() && !tasksViewModel.isLoading && tasksViewModel.errorMessage == null) {
                    Text(
                        text = "Nenhuma tarefa encontrada. Adicione uma!",
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tasksViewModel.tasks) { task ->
                            TaskItem(task = task, onToggleCompletion = {
                                tasksViewModel.toggleTaskCompletion(task)
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onToggleCompletion: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleCompletion() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                ),
                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleCompletion() }
            )
        }
    }
}
