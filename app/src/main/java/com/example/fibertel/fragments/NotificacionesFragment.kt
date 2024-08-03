package com.example.fibertel.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fibertel.ApiClient
import com.example.fibertel.network.ApiEndpoints
import com.example.fibertel.adapter.NotificationAdapter
import com.example.fibertel.R
import com.example.fibertel.model.Ticket
import com.example.fibertel.model.UserManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class NotificacionesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noNotificationsTextView: TextView
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var clientId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notificaciones, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewNotifications)
        noNotificationsTextView = view.findViewById(R.id.tv_no_notifications)

        recyclerView.layoutManager = LinearLayoutManager(context)
        notificationAdapter = NotificationAdapter(emptyList())
        recyclerView.adapter = notificationAdapter

        val userManager = UserManager(requireContext())
        val user = userManager.currentUser
        clientId = user?.id ?: run {
            noNotificationsTextView.text = "Client ID is not available"
            noNotificationsTextView.visibility = View.VISIBLE
            return view
        }

        fetchNotifications()

        return view
    }

    private fun fetchNotifications() {
        val request = ApiClient.createRequest(ApiEndpoints.TICKETS)

        ApiClient.getClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    return
                }

                response.body()?.let { responseBody ->
                    val responseString = responseBody.string()
                    try {
                        val jsonObject = JSONObject(responseString)
                        val jsonArray = jsonObject.getJSONArray("data")
                        val tickets = mutableListOf<Ticket>()
                        for (i in 0 until jsonArray.length()) {
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
                                assignedAt = formatDate(ticketObject.optString("assigned_at")),
                                finalizedAt = formatDate(ticketObject.optString("finalized_at")),
                                closedAt = formatDate(ticketObject.optString("closed_at")),
                                state = ticketObject.getString("state"),
                                createdAt = formatDate(ticketObject.getString("created_at")),
                                updatedAt = formatDate(ticketObject.getString("updated_at")),
                                priority = ticketObject.getString("priority"),
                                complexity = ticketObject.getString("complexity"),
                                expiresAt = formatDate(ticketObject.optString("expires_at"))
                            )
                            if (ticket.clientId == clientId && ticket.state == "finalized") {
                                tickets.add(ticket)
                            }
                        }

                        activity?.runOnUiThread {
                            if (tickets.isEmpty()) {
                                noNotificationsTextView.visibility = View.VISIBLE
                            } else {
                                noNotificationsTextView.visibility = View.GONE
                                notificationAdapter = NotificationAdapter(tickets)
                                recyclerView.adapter = notificationAdapter
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    private fun formatDate(inputDate: String): String {
        val inputFormatDateTime = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
        val inputFormatDate = "yyyy-MM-dd"
        val outputFormat = "dd/MM/yyyy HH:mm:ss"

        return try {
            val inputFormat = if (inputDate.contains("T")) inputFormatDateTime else inputFormatDate
            val date = SimpleDateFormat(inputFormat, Locale.getDefault()).parse(inputDate)
            val outputFormatter = SimpleDateFormat(outputFormat, Locale.getDefault())
            date?.let { outputFormatter.format(it) } ?: "Fecha no válida"
        } catch (e: ParseException) {
            "Fecha no válida"
        }
    }
}
