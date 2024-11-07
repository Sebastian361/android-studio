package com.example.ejemplografico2

import android.widget.Button
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    // Declarar variables XML
    lateinit var campo1: EditText
    lateinit var campo2: EditText
    lateinit var btnSuma: Button
    lateinit var btnResta: Button
    lateinit var btnMultiplicacion: Button
    lateinit var btnDivision: Button
    lateinit var resultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Cazamiento - XML - Variables locales
        campo1 = findViewById(R.id.Et_campo1)
        campo2 = findViewById(R.id.Et_campo2)
        btnSuma = findViewById(R.id.btnSumar)
        btnResta = findViewById(R.id.btnRestar)
        btnMultiplicacion = findViewById(R.id.btnMultiplicar)
        btnDivision = findViewById(R.id.btnDividir)
        resultado = findViewById(R.id.txtResultado)

        // Llamada a funciones - Evento OnClick para cada operación
        btnSuma.setOnClickListener {
            realizarOperacion("suma")
        }

        btnResta.setOnClickListener {
            realizarOperacion("resta")
        }

        btnMultiplicacion.setOnClickListener {
            realizarOperacion("multiplicacion")
        }

        btnDivision.setOnClickListener {
            realizarOperacion("division")
        }
    }

    // Función para realizar la operación seleccionada
    private fun realizarOperacion(operacion: String) {
        val num1 = campo1.text.toString().toIntOrNull() ?: 0
        val num2 = campo2.text.toString().toIntOrNull() ?: 0
        val resultadoOperacion = when (operacion) {
            "suma" -> sumar(num1, num2)
            "resta" -> restar(num1, num2)
            "multiplicacion" -> multiplicar(num1, num2)
            "division" -> dividir(num1, num2)
            else -> 0
        }
        resultado.text = "Resultado: $resultadoOperacion"
    }

    // Funciones de cada operación
    private fun sumar(num1: Int, num2: Int): Int {
        return num1 + num2
    }

    private fun restar(num1: Int, num2: Int): Int {
        return num1 - num2
    }

    private fun multiplicar(num1: Int, num2: Int): Int {
        return num1 * num2
    }

    private fun dividir(num1: Int, num2: Int): String {
        return if (num2 != 0) {
            (num1 / num2).toString()
        } else {
            "Error: División por cero"
        }
    }
}
