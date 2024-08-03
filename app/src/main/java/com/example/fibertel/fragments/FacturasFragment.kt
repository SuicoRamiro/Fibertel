package com.example.fibertel.fragments

import FacturaAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fibertel.R
import com.example.fibertel.model.Factura
import com.example.fibertel.model.UserManager
import com.example.fibertel.ApiClient
import com.example.fibertel.network.ApiEndpoints
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class FacturasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var facturaAdapter: FacturaAdapter
    private val facturas = mutableListOf<Factura>()
    private lateinit var noFacturasTextView: TextView
    private var userManager: UserManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_facturas, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewFacturas)
        noFacturasTextView = view.findViewById(R.id.tv_no_facturas)
        recyclerView.layoutManager = LinearLayoutManager(context)
        facturaAdapter = FacturaAdapter(facturas)
        recyclerView.adapter = facturaAdapter

        userManager = UserManager(requireContext())
        val dni = userManager?.currentUser?.nationalIdentificationNumber ?: ""

        if (dni.isNotEmpty()) {
            fetchInvoices(dni)
        } else {
            showMessage("DNI del usuario no encontrado")
        }

        return view
    }

    private fun fetchInvoices(dni: String) {
        val endpoint = ApiEndpoints.INVOICING_BY_DNI + dni
        val request = ApiClient.createRequest(endpoint)

        ApiClient.getClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                showMessage("Error al obtener las facturas")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    showMessage("Error en la respuesta del servidor")
                    return
                }

                response.body()?.string()?.let { jsonString ->
                    val jsonArray = JSONObject(jsonString).getJSONArray("data")
                    val fetchedFacturas = mutableListOf<Factura>()

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val factura = Factura(
                            id = jsonObject.getString("id"),
                            balance = jsonObject.getString("balance"),
                            amount = jsonObject.getString("amount"),
                            state = jsonObject.getString("state"),
                            invoice_number = jsonObject.getString("invoice_number"),
                            first_due_date = jsonObject.getString("first_due_date"),
                            second_due_date = jsonObject.getString("second_due_date"),
                            issued_at = jsonObject.getString("issued_at")
                        )
                        if (factura.state == "paid" || factura.state == "pending") {
                            fetchedFacturas.add(factura)
                        }
                    }

                    // Ordenar las facturas en orden descendente por la fecha de emisión
                    val sortedFacturas = fetchedFacturas.sortedByDescending { it.issued_at }

                    activity?.runOnUiThread {
                        if (sortedFacturas.isEmpty()) {
                            noFacturasTextView.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            noFacturasTextView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            facturas.clear()
                            facturas.addAll(sortedFacturas)
                            facturaAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }


    private fun showMessage(message: String) {
        // Implementa tu método de mostrar mensajes aquí
        // Por ejemplo, usando un Toast
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

