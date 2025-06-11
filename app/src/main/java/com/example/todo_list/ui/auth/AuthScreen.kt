package com.example.todo_list.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo_list.ui.theme.TodoAppTheme
import com.example.todoapp.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onNavigateToTasks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // A classe AuthViewModel está no mesmo pacote, então não precisa de importação explícita
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.provideFactory(context))

    var isLoginMode by remember { mutableStateOf(true) }

    LaunchedEffect(authViewModel.isAuthenticated) {
        if (authViewModel.isAuthenticated) {
            onNavigateToTasks()
        }
    }

    LaunchedEffect(authViewModel.registrationSuccess) {
        if (authViewModel.registrationSuccess) {
            isLoginMode = true
            authViewModel.resetRegistrationState()
            authViewModel.errorMessage = "Cadastro realizado com sucesso! Faça login."
        }
    }

    TodoAppTheme { // Chamada do tema, que agora deve ser resolvida
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = { Text(if (isLoginMode) "Login" else "Cadastro") })
            }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isLoginMode) {
                    LoginContent(authViewModel) {
                        authViewModel.login()
                    }
                } else {
                    RegisterContent(authViewModel) {
                        authViewModel.register()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (authViewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }

                authViewModel.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isLoginMode) "Não tem uma conta? Cadastre-se" else "Já tem uma conta? Faça login",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        isLoginMode = !isLoginMode
                        authViewModel.errorMessage = null
                        authViewModel.username = ""
                        authViewModel.password = ""
                        authViewModel.firstName = ""
                        authViewModel.lastName = ""
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginContent(authViewModel: AuthViewModel, onLoginClick: () -> Unit) {
    OutlinedTextField(
        value = authViewModel.username,
        onValueChange = { authViewModel.username = it },
        label = { Text("Nome de Usuário") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = authViewModel.password,
        onValueChange = { authViewModel.password = it },
        label = { Text("Senha") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = onLoginClick,
        enabled = !authViewModel.isLoading,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Entrar")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterContent(authViewModel: AuthViewModel, onRegisterClick: () -> Unit) {
    OutlinedTextField(
        value = authViewModel.username,
        onValueChange = { authViewModel.username = it },
        label = { Text("Nome de Usuário") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = authViewModel.password,
        onValueChange = { authViewModel.password = it },
        label = { Text("Senha") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = authViewModel.firstName,
        onValueChange = { authViewModel.firstName = it },
        label = { Text("Primeiro Nome (Opcional)") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = authViewModel.lastName,
        onValueChange = { authViewModel.lastName = it },
        label = { Text("Sobrenome (Opcional)") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = onRegisterClick,
        enabled = !authViewModel.isLoading,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Cadastrar")
    }
}
