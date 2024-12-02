package com.example.ejemplografico2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ejemplografico2.databinding.UsuarioItemBinding
import com.example.ejemplografico2.Usuario


class UsuariosAdapter(
    private val usuarios: List<Usuario>,
    private val onModificar: (Usuario) -> Unit,
    private val onEliminar: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(val binding: UsuarioItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val binding = UsuarioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsuarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.binding.nombreUsuario.text = usuario.nombre

        holder.binding.btnModificar.setOnClickListener { onModificar(usuario) }
        holder.binding.btnEliminar.setOnClickListener { onEliminar(usuario) }
    }

    override fun getItemCount() = usuarios.size
}
