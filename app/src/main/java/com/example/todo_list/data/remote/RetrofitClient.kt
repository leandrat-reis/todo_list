package com.example.todoapp.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8000/" // Para emulador Android. Use o IP da sua máquina para dispositivo físico.

    // Configuração do OkHttpClient para requisições sem autenticação (login/registro)
    private val unauthenticatedHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Função para criar o ApiService SEM token (para login e registro)
    val unauthenticatedApiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(unauthenticatedHttpClient)
            .build()
            .create(ApiService::class.java)
    }

    // Função para criar o ApiService COM token (para tarefas)
    fun createAuthenticatedApiService(authToken: String): ApiService {
        val authenticatedHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
            .addInterceptor(AuthInterceptor(authToken)) // Adiciona o interceptor de autenticação
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(authenticatedHttpClient)
            .build()
            .create(ApiService::class.java)
    }
}