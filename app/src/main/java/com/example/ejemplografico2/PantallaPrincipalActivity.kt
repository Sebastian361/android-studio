package com.example.ejemplografico2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.ejemplografico2.databinding.ActivityPantallaPrincipalBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import android.webkit.CookieManager
import android.webkit.WebView

class PantallaPrincipalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPantallaPrincipalBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private var notificaciones: MutableList<String> = mutableListOf() // Lista de notificaciones

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el cliente de inicio de sesión de Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Verificar si hay un usuario autenticado
        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            val emailUsuario = auth.currentUser!!.email

            // Si el correo pertenece al administrador, redirigir a AdminActivity
            if (emailUsuario == "zaanchez123@gmail.com") {
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
                finish() // Finalizar esta actividad para evitar regresar a ella
                return
            }
        }

        // Configurar el Toolbar con el menú
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        // Manejo del ícono de campana
        binding.iconoCampana.setOnClickListener {
            mostrarNotificaciones()
        }

        // Manejo del botón Ofertas
        binding.botonOfertas.setOnClickListener {
            val intent = Intent(this, OfertasActivity::class.java)
            startActivity(intent)
        }

        // Configurar Chatwoot WebView
        configurarChat()
    }

    // Inflar el menú
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Manejar las opciones seleccionadas del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_inicio_sesion -> {
                // Acción de iniciar sesión o registrarse
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_cerrar_sesion -> {
                // Acción de cerrar sesión
                logoutUser()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun logoutUser() {
        // Cerrar sesión de Firebase
        FirebaseAuth.getInstance().signOut()

        // Cerrar sesión de Google
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()

            // Redirigir al usuario a la pantalla de inicio de sesión
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish() // Cerrar la actividad actual
        }
    }

    private fun mostrarNotificaciones() {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_notificaciones, null)
        val popupWindow = PopupWindow(
            popupView,
            binding.root.width - 100,
            600,
            true
        )

        val textViewNotificaciones = popupView.findViewById<TextView>(R.id.textoNotificaciones)
        if (notificaciones.isEmpty()) {
            textViewNotificaciones.text = "No hay notificaciones"
        } else {
            textViewNotificaciones.text = notificaciones.joinToString("\n")
        }

        popupWindow.showAtLocation(binding.root, Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 200)
    }

    private fun configurarChat() {
        val webView = findViewById<WebView>(R.id.chatWebView)

        // Configuración del WebView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true

        // Configurar WebViewClient
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e("WebViewError", "Error de carga: $description")
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d("WebView", "Cargando URL: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("WebView", "Carga finalizada para: $url")
            }
        }

        // Cargar el archivo HTML desde assets
        webView.loadUrl("file:///android_asset/chatwoot_test.html")



    // Inyectar el script de Chatwoot
                webView.evaluateJavascript("""
                (function() {
                    var BASE_URL = "https://app.chatwoot.com";
                    var g = document.createElement("script"), s = document.getElementsByTagName("script")[0];
                    g.src = BASE_URL + "/packs/js/sdk.js";
                    g.defer = true;
                    g.async = true;
                    s.parentNode.insertBefore(g, s);
                    g.onload = function() {
                        window.chatwootSDK.run({
                            websiteToken: 'kL48WxKEJ4wBkyQ92uSuWSRP',
                            baseUrl: BASE_URL
                        });
                    }
                })();
            """, null)
            }
        }

        // Cargar una URL genérica para inicializar el WebView

