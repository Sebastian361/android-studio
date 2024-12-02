package com.example.ejemplografico2

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor
import okhttp3.Request

object ChatwootApiClient {

    private const val BASE_URL = "https://app.chatwoot.com/"
    private const val API_KEY = "AuhKok2VjBH4VHQ3zchvX7eE" // Reemplaza con tu clave válida

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain: Interceptor.Chain ->
            val request: Request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $API_KEY")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val chatwootApi: ChatwootApi = retrofit.create(ChatwootApi::class.java)
}
