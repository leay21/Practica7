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
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // 1. Validar sesión
        val user = auth.currentUser
        if (user != null) {
            loadUserInfo(user.uid)
            saveFcmToken(user.uid)
        }


        // 3. Botón de Cerrar Sesión
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            goToLogin()
        }

        // 4. Botón Panel Admin (Aún no hace nada, es placeholder)
        binding.btnAdminPanel.setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
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
    private fun saveFcmToken(uid: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                // Guardamos el token dentro del nodo del usuario
                FirebaseDatabase.getInstance().getReference("users")
                    .child(uid)
                    .child("fcmToken")
                    .setValue(token)
            }
        }
    }
    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}