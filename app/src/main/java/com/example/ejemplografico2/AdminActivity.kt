package com.example.ejemplografico2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ejemplografico2.databinding.ActivityAdminBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    private val usuarios = mutableListOf<Usuario>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verifica si es el admin
        val emailUsuario = FirebaseAuth.getInstance().currentUser?.email
        if (emailUsuario != "zaanchez123@gmail.com") {
            finish()
            return
        }

        // Título "Clientes" sobre los usuarios
        binding.tituloClientes.text = "Clientes"

        // Configurar RecyclerView
        binding.recyclerViewUsuarios.layoutManager = LinearLayoutManager(this)
        val adapter = UsuariosAdapter(usuarios, this::modificarUsuario, this::eliminarUsuario)
        binding.recyclerViewUsuarios.adapter = adapter

        // Cargar usuarios desde Firestore
        cargarUsuarios(adapter)

        // Funcionalidad del botón flotante (+) para agregar un nuevo usuario
        binding.botonAgregar.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Agregar nuevo usuario")

            // Crear un diseño vertical para los campos de entrada
            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL

            val inputNombre = EditText(this)
            inputNombre.hint = "Nombre del usuario"
            layout.addView(inputNombre)
            // Cambiar el color del texto
            inputNombre.setTextColor(ContextCompat.getColor(this, R.color.text_black)) // Texto negro
            inputNombre.setHintTextColor(ContextCompat.getColor(this, R.color.text_black))

            val inputEmail = EditText(this)
            inputEmail.hint = "Email del usuario"
            layout.addView(inputEmail)
            // Cambiar el color del texto
            inputEmail.setTextColor(ContextCompat.getColor(this, R.color.text_black)) // Texto negro
            inputEmail.setHintTextColor(ContextCompat.getColor(this, R.color.text_black))

            val inputContraseña = EditText(this)
            inputContraseña.hint = "Contraseña del usuario"
            inputContraseña.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            layout.addView(inputContraseña)
            // Cambiar el color del texto
            inputContraseña.setTextColor(ContextCompat.getColor(this, R.color.text_black)) // Texto negro
            inputContraseña.setHintTextColor(ContextCompat.getColor(this, R.color.text_black))

            builder.setView(layout)

            builder.setPositiveButton("Agregar") { _, _ ->
                val nombre = inputNombre.text.toString().trim()
                val email = inputEmail.text.toString().trim()
                val contraseña = inputContraseña.text.toString().trim()

                if (nombre.isNotEmpty() && email.isNotEmpty() && contraseña.isNotEmpty()) {
                    // Crear usuario en Firebase Authentication
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, contraseña)
                        .addOnSuccessListener { authResult ->
                            val nuevoUsuario = Usuario(
                                uid = authResult.user?.uid ?: UUID.randomUUID().toString(),
                                nombre = nombre,
                                email = email,
                                rol = "usuario",  // Asignar rol 'usuario' por defecto
                                fechaCreacion = System.currentTimeMillis() // Agregar la fecha de creación
                            )

                            // Guardar el nuevo usuario en Firestore
                            db.collection("usuarios")
                                .document(nuevoUsuario.uid)
                                .set(nuevoUsuario)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Usuario agregado exitosamente", Toast.LENGTH_SHORT).show()
                                    usuarios.add(nuevoUsuario)  // Añadir el usuario a la lista local
                                    adapter.notifyItemInserted(usuarios.size - 1)  // Notificar al adapter
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error al agregar usuario en Firestore", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error al crear usuario en Firebase Authentication: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setNegativeButton("Cancelar", null)
            builder.show()
        }

        // Funcionalidad para cerrar sesión
        binding.btnCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()  // Cerrar sesión
            finish()  // Cerrar la actividad actual
            startActivity(Intent(this, AuthActivity::class.java))  // Ir a la página de inicio de sesión
        }
        // Funcionalidad para gestión de chat
        binding.btnGestionChat.setOnClickListener {
            startActivity(Intent(this, GestionChatAdminActivity::class.java))  // Redirigir a GestionChatAdminActivity
        }
    }

    private fun cargarUsuarios(adapter: UsuariosAdapter) {
        db.collection("usuarios").get()
            .addOnSuccessListener { result ->
                usuarios.clear()
                for (document in result) {
                    val usuario = document.toObject(Usuario::class.java)
                    usuario.uid = document.id  // Asignar el uid de Firebase al campo uid en la clase Usuario
                    usuarios.add(usuario)
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun modificarUsuario(usuario: Usuario) {
        // Crear un cuadro de diálogo para modificar el nombre del usuario
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Modificar nombre del usuario")

        // Campo de entrada para el nuevo nombre
        val inputNombre = EditText(this)
        inputNombre.hint = "Nuevo nombre"
        inputNombre.setText(usuario.nombre) // Mostrar el nombre actual como predeterminado

// Cambiar el color del texto
        inputNombre.setTextColor(ContextCompat.getColor(this, R.color.text_black)) // Texto negro

// Cambiar el color del hint (placeholder)
        inputNombre.setHintTextColor(ContextCompat.getColor(this, R.color.text_black))


        builder.setView(inputNombre)

        builder.setPositiveButton("Guardar") { _, _ ->
            val nuevoNombre = inputNombre.text.toString().trim()

            if (nuevoNombre.isNotEmpty()) {
                // Actualizar el nombre del usuario en Firestore
                db.collection("usuarios")
                    .document(usuario.uid)
                    .update("nombre", nuevoNombre)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show()

                        // Actualizar el nombre en la lista local y notificar al adapter
                        usuario.nombre = nuevoNombre
                        binding.recyclerViewUsuarios.adapter?.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar el nombre", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }


    private fun eliminarUsuario(usuario: Usuario) {
        db.collection("usuarios").document(usuario.uid).delete()  // Usar uid para eliminar
            .addOnSuccessListener {
                usuarios.remove(usuario)
                binding.recyclerViewUsuarios.adapter?.notifyDataSetChanged()
            }
    }
}
