package com.example.fibertel.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fibertel.ApiClient
import com.example.fibertel.ApiEndpoints
import com.example.fibertel.R
import com.example.fibertel.model.UserManager
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class InicioFragment : Fragment() {

    private lateinit var tvFacturaNoPagadas: TextView
    private lateinit var tvCreditoDisponible: TextView
    private lateinit var tvBalance: TextView
    private var clientId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        val btnVerMas = view.findViewById<ImageButton>(R.id.icon_info_balance)
        tvFacturaNoPagadas = view.findViewById(R.id.tv_FacturaNoPagadas)
        tvCreditoDisponible = view.findViewById(R.id.tv_CreditoDisponible)
        tvBalance = view.findViewById(R.id.tv_Balance)

        btnVerMas.setOnClickListener {
            mostrarDialogoBalanceCC()
        }

        val userManager = UserManager(requireContext())
        val user = userManager.currentUser

        if (user != null) {
            clientId = user.id
            Log.d("InicioFragment", "ID del usuario: $clientId")
            fetchAccountData(clientId!!)
        } else {
            showMessage("ID del usuario no encontrado")
        }

        return view
    }

    private fun fetchAccountData(clientId: String) {
        val url = "${ApiEndpoints.BASE_URL}/clients/$clientId/current_account"

        Log.d("InicioFragment", "URL final para obtener datos de cuenta: $url")

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "2865daf0-f236-46cf-b3c9-5ef541183a31")
            .build()

        ApiClient.getClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                showMessage("Error al obtener los datos de la cuenta")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    activity?.runOnUiThread {
                        showMessage("Error en la respuesta del servidor")
                    }
                    return
                }

                response.body()?.string()?.let { responseString ->
                    Log.d("InicioFragment", "Respuesta completa: $responseString")
                    try {
                        val jsonObject = JSONObject(responseString)
                        val dataObject = jsonObject.getJSONObject("data")
                        val balanceAmount = dataObject.getString("balance_amount")
                        val creditAmount = dataObject.getString("credit_amount")
                        val invoiceBalanceAmount = dataObject.getString("balance_amount")

                        activity?.runOnUiThread {
                            tvFacturaNoPagadas.text = "$$invoiceBalanceAmount"
                            tvCreditoDisponible.text = "$$creditAmount"
                            tvBalance.text = "$$balanceAmount"
                        }
                    } catch (e: Exception) {
                        Log.e("InicioFragment", "Error al procesar la respuesta JSON: ${e.message}")
                        e.printStackTrace()
                        activity?.runOnUiThread {
                            showMessage("Error al procesar los datos")
                        }
                    }
                } ?: run {
                    activity?.runOnUiThread {
                        showMessage("Respuesta vacía del servidor")
                    }
                }
            }

        })
    }

    private fun mostrarDialogoBalanceCC() {
        val dialog = Dialog(requireContext())
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.info_balance_cc)

        val btnCerrar = dialog.findViewById<Button>(R.id.btnCerrar)
        btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showMessage(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

}
