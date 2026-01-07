package com.example.practica7

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practica7.databinding.ItemUserBinding

class UsersAdapter(private val users: List<User>) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.binding.tvName.text = user.name
        holder.binding.tvEmail.text = user.email

        // Manejo del checkbox
        // Quitamos el listener anterior para evitar bugs al reciclar vistas
        holder.binding.cbSelect.setOnCheckedChangeListener(null)
        holder.binding.cbSelect.isChecked = user.isSelected

        holder.binding.cbSelect.setOnCheckedChangeListener { _, isChecked ->
            user.isSelected = isChecked
        }

        // Mostrar etiqueta si es Admin
        if (user.role == "admin") {
            holder.binding.tvRoleTag.visibility = View.VISIBLE
        } else {
            holder.binding.tvRoleTag.visibility = View.GONE
        }
    }

    override fun getItemCount() = users.size

    // Función auxiliar para obtener los seleccionados
    fun getSelectedUsers(): List<User> {
        return users.filter { it.isSelected }
    }
}