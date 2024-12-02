package com.example.ejemplografico2

data class Usuario(
    var uid: String = "",           // UID generado por Firebase
    var nombre: String = "",        // Nombre del usuario
    val email: String = "",         // Email del usuario
    val rol: String = "usuario",    // Rol del usuario, por defecto es "usuario"
    val fechaCreacion: Long = 0L    // Fecha de creación en formato timestamp
) {
    // Constructor vacío requerido por Firebase
    constructor() : this("", "", "", "usuario", 0L)
}
