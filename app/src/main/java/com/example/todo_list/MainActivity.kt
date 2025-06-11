package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.ui.TodoNavGraph
import com.example.todoapp.ui.the

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoApp()
        }
    }
}

@Composable
fun TodoApp() {
    // Aplica o tema definido no seu aplicativo
     TodoTheme {
        // Um Surface é um contêiner básico que usa as cores do tema
        Surface(
            modifier = Modifier.fillMaxSize(), // Preenche toda a tela
            color = MaterialTheme.colorScheme.background // Usa a cor de fundo do tema
        ) {
            // Cria e lembra um NavController para gerenciar a navegação entre as telas
            val navController = rememberNavController()
            // Inicia o gráfico de navegação do seu aplicativo
            TodoNavGraph(navController = navController)
        }
    }
}
