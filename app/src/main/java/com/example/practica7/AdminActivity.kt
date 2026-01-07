package com.example.practica7

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practica7.databinding.ActivityAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var database: DatabaseReference
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar RecyclerView
        userList = ArrayList()
        adapter = UsersAdapter(userList)
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter

        // Cargar usuarios
        loadUsers()

        binding.btnSend.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val message = binding.etMessage.text.toString()
            val selectedUsers = adapter.getSelectedUsers()

            if (title.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Escribe un título y mensaje", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedUsers.isEmpty()) {
                Toast.makeText(this, "Selecciona al menos un usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Aquí irá la lógica de envío en el siguiente paso
            Toast.makeText(this, "Enviando a ${selectedUsers.size} usuarios (Simulado)", Toast.LENGTH_SHORT).show()

            // TODO: Llamar función de envío en el Paso 5
        }
    }

    private fun loadUsers() {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        database = FirebaseDatabase.getInstance().getReference("users")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (data in snapshot.children) {
                    val user = data.getValue(User::class.java)
                    if (user != null && user.uid != currentUid) {
                        // Opcional: No mostrarse a sí mismo en la lista
                        userList.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
            }
        })
    }
}