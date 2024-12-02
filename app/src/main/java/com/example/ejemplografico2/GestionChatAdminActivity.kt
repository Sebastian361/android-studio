package com.example.ejemplografico2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class GestionChatAdminActivity : AppCompatActivity() {

    private lateinit var recyclerViewConversaciones: RecyclerView
    private lateinit var editTextRespuesta: EditText
    private lateinit var btnEnviarRespuesta: Button

    private lateinit var chatwootApi: ChatwootApi
    private val accountId = "108089"  // account_id de mi cuenta Chatwoot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_chat_admin)

        // Inicializar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://app.chatwoot.com/") // URL correcta de tu instancia de Chatwoot
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        chatwootApi = retrofit.create(ChatwootApi::class.java)

        // Inicializar vistas
        recyclerViewConversaciones = findViewById(R.id.recyclerViewConversaciones)
        editTextRespuesta = findViewById(R.id.editTextRespuesta)
        btnEnviarRespuesta = findViewById(R.id.btnEnviarRespuesta)

        // Configurar RecyclerView con LayoutManager
        recyclerViewConversaciones.layoutManager = LinearLayoutManager(this)

        // Configurar Adaptador
        val adapter = ConversacionesAdapter(listOf()) { conversacion ->
            Toast.makeText(this, "Conversación seleccionada: ${conversacion.id}", Toast.LENGTH_SHORT).show()
        }
        recyclerViewConversaciones.adapter = adapter

        // Obtener conversaciones
        obtenerConversaciones()

        // Manejo del botón para enviar respuesta
        btnEnviarRespuesta.setOnClickListener {
            val respuesta = editTextRespuesta.text.toString()
            if (respuesta.isNotEmpty()) {
                val conversacionSeleccionada = adapter.getSelectedConversation() // Lógica para obtener la conversación seleccionada
                if (conversacionSeleccionada != null) {
                    responderMensaje(conversacionSeleccionada.id, respuesta)
                } else {
                    Toast.makeText(this, "Selecciona una conversación", Toast.LENGTH_SHORT).show()
                }
                editTextRespuesta.text.clear()
            }
        }
    }

    private fun obtenerConversaciones() {
        chatwootApi.obtenerConversaciones("108089").enqueue(object : Callback<ApiResponse<List<Conversacion>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Conversacion>>>,
                response: Response<ApiResponse<List<Conversacion>>>
            ) {
                if (response.isSuccessful) {
                    // Extraer las conversaciones desde el campo `data`
                    val conversaciones = response.body()?.data ?: emptyList()
                    // Actualizar el adaptador con las conversaciones
                    (recyclerViewConversaciones.adapter as ConversacionesAdapter).setConversaciones(conversaciones)
                } else {
                    Toast.makeText(
                        this@GestionChatAdminActivity,
                        "Error al obtener conversaciones: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Conversacion>>>, t: Throwable) {
                Toast.makeText(
                    this@GestionChatAdminActivity,
                    "Fallo en la conexión: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun responderMensaje(conversacionId: String, respuesta: String) {
        val mensaje = Mensaje(content = respuesta)
        chatwootApi.enviarRespuesta(accountId, conversacionId, mensaje).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@GestionChatAdminActivity, "Mensaje enviado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@GestionChatAdminActivity, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@GestionChatAdminActivity, "Fallo en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}


