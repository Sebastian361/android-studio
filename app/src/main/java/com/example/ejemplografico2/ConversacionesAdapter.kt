package com.example.ejemplografico2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ConversacionesAdapter(
    private var conversaciones: List<Conversacion>,
    private val onConversationClick: (Conversacion) -> Unit
) : RecyclerView.Adapter<ConversacionesAdapter.ViewHolder>() {

    private var selectedConversation: Conversacion? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewId: TextView = itemView.findViewById(R.id.textViewConversationId)
        val textViewStatus: TextView = itemView.findViewById(R.id.textViewConversationStatus)

        fun bind(conversacion: Conversacion) {
            textViewId.text = "ID: ${conversacion.id}"
            textViewStatus.text = "Estado: ${conversacion.status}"

            // Indicar si está seleccionada
            itemView.isSelected = conversacion == selectedConversation

            itemView.setOnClickListener {
                selectedConversation = conversacion
                notifyDataSetChanged()
                onConversationClick(conversacion)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversacion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(conversaciones[position])
    }

    override fun getItemCount(): Int = conversaciones.size

    fun setConversaciones(nuevasConversaciones: List<Conversacion>) {
        this.conversaciones = nuevasConversaciones
        notifyDataSetChanged()
    }

    fun getSelectedConversation(): Conversacion? {
        return selectedConversation
    }
}
