package com.example.ejemplografico2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ejemplografico2.databinding.ActivityOfertasBinding

class OfertasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOfertasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfertasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar productos en oferta
        val ofertas = listOf(
            "Producto 1 - 20% de descuento",
            "Producto 2 - 15% de descuento",
            "Producto 3 - 30% de descuento"
        )
        binding.listaOfertas.text = ofertas.joinToString("\n")
    }
}
