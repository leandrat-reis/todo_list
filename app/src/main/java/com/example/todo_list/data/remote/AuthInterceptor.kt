package com.example.todoapp.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authToken: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Adiciona o cabeçalho de autorização se o token existir
        val requestBuilder = originalRequest.newBuilder().apply {
            authToken?.let {
                header("Authorization", "Token $it")
            }
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}