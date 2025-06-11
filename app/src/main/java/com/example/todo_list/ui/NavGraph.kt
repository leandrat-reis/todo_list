package com.example.todoapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.todoapp.ui.auth.AuthScreen
import com.example.todoapp.ui.tasks.TasksScreen

object Routes {
    const val AUTH = "auth"
    const val TASKS = "tasks"
}

@Composable
fun TodoNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.AUTH, // A tela inicial será a de autenticação
        modifier = modifier
    ) {
        composable(Routes.AUTH) {
            AuthScreen(
                onNavigateToTasks = {
                    navController.navigate(Routes.TASKS) {
                        // Isso remove a tela de autenticação da back stack,
                        // para que o usuário não possa voltar para ela após o login.
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.TASKS) {
            TasksScreen(
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.TASKS) { inclusive = true } // Remove a tela de tarefas
                    }
                }
            )
        }
    }
}