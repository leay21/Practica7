package com.example.practica7

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val fcmToken: String = "",
    var isSelected: Boolean = false // Para saber si el admin lo marcó en la lista
)