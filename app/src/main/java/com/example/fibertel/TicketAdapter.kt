package com.example.fibertel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fibertel.model.Ticket

class TicketAdapter(
    private val tickets: MutableList<Ticket>,
    private val onResolveClick: (Ticket) -> Unit
) : RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    // Mapa de códigos a nombres de categorías
    private val categoryNames = mapOf(
        "e38a327a-341f-4f17-999b-caf97ef816ee" to "Lentitud",
        "1d294e6f-781a-4d9e-8237-359084263f41" to "Sin conexión"
        // Agrega más códigos y nombres según sea necesario
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.bind(ticket)
    }

    override fun getItemCount(): Int = tickets.size

    // Remover un ticket de la lista
    fun removeTicket(ticket: Ticket) {
        val position = tickets.indexOf(ticket)
        if (position != -1) {
            tickets.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // Obtener la lista de tickets
    fun getTickets(): List<Ticket> = tickets

    // Actualizar la lista de tickets de manera eficiente
    fun updateTickets(newTickets: List<Ticket>) {
        tickets.clear()
        tickets.addAll(newTickets)
        notifyDataSetChanged()
    }

    inner class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.ticketTitle)
        private val categoryTextView: TextView = itemView.findViewById(R.id.ticketCategory)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.ticketDescription)
        private val resolveButton: Button = itemView.findViewById(R.id.btnResuelto)
        private val resolvedMessageTextView: TextView = itemView.findViewById(R.id.tvResolvedMessage)

        fun bind(ticket: Ticket) {
            titleTextView.text = ticket.title ?: "No Title"
            categoryTextView.text = categoryNames[ticket.categoryId] ?: "Unknown Category"
            descriptionTextView.text = ticket.description ?: "No Description"

            if (ticket.state == "finalized") {
                resolveButton.visibility = View.GONE
                resolvedMessageTextView.visibility = View.VISIBLE
                resolvedMessageTextView.text = "Resolved"
            } else {
                resolveButton.visibility = View.VISIBLE
                resolvedMessageTextView.visibility = View.GONE
                resolveButton.setOnClickListener {
                    onResolveClick(ticket)
                    // Cambiar el estado del botón después de hacer clic
                    resolveButton.isEnabled = false
                    resolveButton.text = "Resolved"
                }
            }
        }
    }
}

