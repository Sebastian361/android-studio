package com.example.ejemplografico2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Path

// Clase envoltorio genérica para manejar respuestas con datos
data class ApiResponse<T>(
    val data: T? = null
)

interface ChatwootApi {

    // Obtener conversaciones (respuesta envuelta en ApiResponse)
    @GET("api/v1/accounts/{account_id}/conversations")
    @Headers("Authorization: Bearer AuhKok2VjBH4VHQ3zchvX7eE") // Usa tu API Key de Chatwoot
    fun obtenerConversaciones(
        @Path("account_id") accountId: String
    ): Call<ApiResponse<List<Conversacion>>>

    // Enviar respuesta
    @POST("api/v1/accounts/{account_id}/conversations/{conversation_id}/messages")
    @Headers("Authorization: Bearer AuhKok2VjBH4VHQ3zchvX7eE")
    fun enviarRespuesta(
        @Path("account_id") accountId: String,
        @Path("conversation_id") conversationId: String,
        @Body mensaje: Mensaje
    ): Call<Void>
}
