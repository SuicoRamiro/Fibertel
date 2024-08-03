package com.example.fibertel.activities

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fibertel.ApiClient
import com.example.fibertel.R
import com.example.fibertel.adapter.TicketAdapter
import com.example.fibertel.model.Ticket
import com.example.fibertel.model.UserManager
import com.example.fibertel.network.ApiEndpoints
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MisTicketsActivity : AppCompatActivity() {

    private lateinit var ticketAdapter: TicketAdapter
    private lateinit var userId: String
    private lateinit var tvNoTickets: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_tickets)

        // Inicializar UserManager y obtener el ID del usuario
        val userManager = UserManager(this)
        val user = userManager.currentUser

        userId = user?.id ?: run {
            Log.e("MisTicketsActivity", "User ID is null")
            return
        }

        // Inicializar el botón de retroceso
        val btnRetroceder: ImageButton = findViewById(R.id.btn_retroceder)
        btnRetroceder.setOnClickListener {
            finish() // Cierra la actividad y vuelve al fragmento anterior
        }

        // Inicializar el TextView de "No tickets"
        tvNoTickets = findViewById(R.id.tv_no_tickets)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTickets)
        recyclerView.layoutManager = LinearLayoutManager(this)
        ticketAdapter = TicketAdapter(mutableListOf()) { ticket ->
            // Lógica para el botón "Resuelto"
            markTicketAsResolved(ticket)
        }
        recyclerView.adapter = ticketAdapter

        fetchTickets()
    }

    private fun fetchTickets() {
        val request = ApiClient.createRequest(ApiEndpoints.TICKETS)

        ApiClient.getClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MisTicketsActivity", "Error fetching tickets", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("MisTicketsActivity", "Unexpected code $response")
                    return
                }

                response.body()?.let { responseBody ->
                    val responseString = responseBody.string()
                    Log.d("MisTicketsActivity", "Response: $responseString")

                    try {
                        val jsonObject = JSONObject(responseString)
                        val jsonArray = jsonObject.getJSONArray("data")
                        runOnUiThread {
                            parseTickets(jsonArray)
                        }
                    } catch (e: Exception) {
                        Log.e("MisTicketsActivity", "Error parsing tickets", e)
                    }
                } ?: run {
                    Log.e("MisTicketsActivity", "Response body is null")
                }
            }
        })
    }

    private fun parseTickets(jsonArray: JSONArray) {
        val tickets = mutableListOf<Ticket>()
        for (i in 0 until jsonArray.length()) {
            try {
                val ticketObject = jsonArray.getJSONObject(i)
                val ticket = Ticket(
                    id = ticketObject.getString("id"),
                    publicId = ticketObject.getInt("public_id"),
                    clientId = ticketObject.optString("client_id"),
                    contractId = ticketObject.optString("contract_id"),
                    categoryId = ticketObject.getString("category_id"),
                    assignableId = ticketObject.optString("assignable_id"),
                    title = ticketObject.getString("title"),
                    description = ticketObject.getString("description"),
                    assignedAt = ticketObject.optString("assigned_at"),
                    finalizedAt = ticketObject.optString("finalized_at"),
                    closedAt = ticketObject.optString("closed_at"),
                    state = ticketObject.getString("state"),
                    createdAt = ticketObject.getString("created_at"),
                    updatedAt = ticketObject.getString("updated_at"),
                    priority = ticketObject.getString("priority"),
                    complexity = ticketObject.getString("complexity"),
                    expiresAt = ticketObject.optString("expires_at")
                )
                // Filtra por ID de cliente
                if (ticket.clientId == userId) {
                    tickets.add(ticket)
                }
            } catch (e: Exception) {
                Log.e("MisTicketsActivity", "Error parsing ticket item", e)
            }
        }

        runOnUiThread {
            if (tickets.isEmpty()) {
                tvNoTickets.visibility = TextView.VISIBLE
            } else {
                tvNoTickets.visibility = TextView.GONE
                ticketAdapter.updateTickets(tickets)
            }
        }
    }

    private fun markTicketAsResolved(ticket: Ticket) {
        val requestBody = FormBody.Builder()
            .add("state", "finalized")
            .build()
        val request =
            ApiClient.createPatchRequest("${ApiEndpoints.TICKET_DETAILS}${ticket.id}", requestBody)

        ApiClient.getClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MisTicketsActivity", "Error marking ticket as resolved", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        ticketAdapter.removeTicket(ticket)
                        if (ticketAdapter.itemCount == 0) {
                            tvNoTickets.visibility = TextView.VISIBLE
                        }
                    }
                } else {
                    Log.e("MisTicketsActivity", "Failed to mark ticket as resolved: ${response.message()}")
                }
            }
        })
    }
}
