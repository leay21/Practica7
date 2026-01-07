package com.example.practica7

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practica7.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    // --- CONFIGURACIÓN ---
    // Contraseña maestra definida por el desarrollador
    private val MASTER_KEY = "admin1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupListeners()
    }

    private fun setupListeners() {
        // 1. Mostrar/Ocultar campo de contraseña maestra
        binding.switchAdmin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.layoutMasterPass.visibility = View.VISIBLE
            } else {
                binding.layoutMasterPass.visibility = View.GONE
                binding.etMasterPassword.text?.clear()
            }
        }

        // 2. Botón Registrar
        binding.btnRegister.setOnClickListener {
            validateAndRegister()
        }

        // 3. Ir a Login
        binding.tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateAndRegister() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val isAdmin = binding.switchAdmin.isChecked
        val masterPassInput = binding.etMasterPassword.text.toString().trim()

        // Validaciones básicas
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        // Validación de Rol y Contraseña Maestra
        var role = "user"
        if (isAdmin) {
            if (masterPassInput == MASTER_KEY) {
                role = "admin"
            } else {
                binding.etMasterPassword.error = "Contraseña maestra incorrecta"
                return
            }
        }

        registerUserInFirebase(name, email, password, role)
    }

    private fun registerUserInFirebase(name: String, email: String, pass: String, role: String) {
        // Mostrar carga (opcional, aquí lo haremos simple)
        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "Registrando..."

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        saveUserToDatabase(uid, name, email, role)
                    }
                } else {
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "Registrarse"
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserToDatabase(uid: String, name: String, email: String, role: String) {
        // Crear objeto de usuario
        val userMap = mapOf(
            "uid" to uid,
            "name" to name,
            "email" to email,
            "role" to role
        )

        // Guardar en nodo "users"
        database.child("users").child(uid).setValue(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                // Por ahora, cerramos o vamos a la MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                binding.btnRegister.isEnabled = true
                binding.btnRegister.text = "Registrarse"
                Toast.makeText(this, "Error al guardar datos: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}