package com.example.practica7

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practica7.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // 1. Validar sesión
        if (user == null) {
            goToLogin()
            return
        }

        // 2. Cargar datos del usuario desde la BD
        loadUserInfo(user.uid)

        // 3. Botón de Cerrar Sesión
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            goToLogin()
        }

        // 4. Botón Panel Admin (Aún no hace nada, es placeholder)
        binding.btnAdminPanel.setOnClickListener {
            Toast.makeText(this, "Próximamente: Enviar Notificaciones", Toast.LENGTH_SHORT).show()
            // Aquí conectaremos la siguiente activity en el Paso 5
        }
    }

    private fun loadUserInfo(uid: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid)

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString()
                    val role = snapshot.child("role").value.toString()

                    binding.tvWelcome.text = "Hola, $name"
                    binding.tvRole.text = "Rol: $role"

                    // Lógica de roles: Si es admin, mostramos el botón especial
                    if (role == "admin") {
                        binding.btnAdminPanel.visibility = View.VISIBLE
                    } else {
                        binding.btnAdminPanel.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}