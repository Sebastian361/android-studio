package com.example.ejemplografico2

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ejemplografico2.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val db = FirebaseFirestore.getInstance() // Inicializar Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Configurar inicio de sesión con Google
        configureGoogleSignIn()

        // Referencias a los elementos de la interfaz
        val emailField = findViewById<EditText>(R.id.editTextEmail)
        val passwordField = findViewById<EditText>(R.id.editTextPassword)
        val nameField = findViewById<EditText>(R.id.editTextName) // Campo para capturar el nombre
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val registerButton = findViewById<Button>(R.id.btnRegister)
        val googleButton = findViewById<Button>(R.id.btnGoogleSignIn) // Botón de inicio con Google
        val guestButton = findViewById<Button>(R.id.btnModoInvitado)  // Botón de Modo Invitado

        // Acciones para el botón de inicio de sesión
        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (validateInput(email, password)) {
                loginUser(email, password)
            }
        }

        // Acciones para el botón de registro
        registerButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val name = nameField.text.toString().trim() // Capturar el nombre

            if (validateInput(email, password) && validateName(name)) {
                registerUser(email, password, name)
            }
        }

        // Acción para el botón de Modo Invitado
        guestButton.setOnClickListener {
            // Redirigir al usuario a la PantallaPrincipalActivity sin autenticación
            val intent = Intent(this, PantallaPrincipalActivity::class.java)
            startActivity(intent)
            finish()  // Evitar que el usuario vuelva a la pantalla de inicio de sesión con el botón atrás
        }

        // Acción para el botón de inicio de sesión con Google
        googleButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty() || password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validateName(name: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    userId?.let {
                        db.collection("usuarios").document(it).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val userRole = document.getString("rol")
                                    if (userRole == "admin") {
                                        startActivity(Intent(this, AdminActivity::class.java))
                                    } else {
                                        startActivity(Intent(this, PantallaPrincipalActivity::class.java))
                                    }
                                    finish()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Error al iniciar sesión: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun registerUser(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid

                    // Asignar rol de admin si el correo es el especificado
                    val userRole = if (email == "zaanchez123@gmail.com") "admin" else "usuario"

                    val userData = hashMapOf(
                        "nombre" to name,
                        "email" to email,
                        "rol" to userRole, // Asignar el rol
                        "fechaCreacion" to System.currentTimeMillis()
                    )

                    userId?.let {
                        db.collection("usuarios").document(it).set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registro exitoso y datos almacenados", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al almacenar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun signOutGoogle() {
        googleSignInClient.signOut()
            .addOnCompleteListener {
                Toast.makeText(this, "Sesión de Google cerrada", Toast.LENGTH_SHORT).show()
            }
    }

    private fun logoutUser() {
        auth.signOut()
        signOutGoogle()

        Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
                account?.let { firebaseAuthWithGoogle(it) }
            } else {
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
            }
        }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid
                    val userData = hashMapOf(
                        "nombre" to (user?.displayName ?: "Nombre Desconocido"),
                        "email" to (user?.email ?: "Correo Desconocido"),
                        "rol" to "usuario",
                        "fechaCreacion" to System.currentTimeMillis()
                    )

                    userId?.let {
                        db.collection("usuarios").document(it).get()
                            .addOnSuccessListener { document ->
                                if (!document.exists()) {
                                    db.collection("usuarios").document(it).set(userData, SetOptions.merge())
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Inicio exitoso y datos almacenados", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this, PantallaPrincipalActivity::class.java))
                                            finish()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Error al almacenar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    val userRole = document.getString("rol")
                                    if (userRole == "admin") {
                                        startActivity(Intent(this, AdminActivity::class.java))
                                    } else {
                                        startActivity(Intent(this, PantallaPrincipalActivity::class.java))
                                    }
                                    finish()
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
