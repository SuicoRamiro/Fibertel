package com.example.fibertel.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fibertel.ApiClient
import com.example.fibertel.network.ApiEndpoints
import com.example.fibertel.R
import com.example.fibertel.model.Factura
import com.example.fibertel.model.UserManager
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class InicioFragment : Fragment() {

    private lateinit var tvFacturaNoPagadas: TextView
    private lateinit var tvCreditoDisponible: TextView
    private lateinit var tvBalance: TextView
    private lateinit var tvFacturaBalance: TextView
    private lateinit var tvFechaVencimiento1: TextView
    private lateinit var tvFechaVencimiento2: TextView
    private var clientId: String? = null
    private var clientDNI: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        val btnVerMas = view.findViewById<ImageButton>(R.id.icon_info_balance)
        tvFacturaNoPagadas = view.findViewById(R.id.tv_FacturaNoPagadas)
        tvCreditoDisponible = view.findViewById(R.id.tv_CreditoDisponible)
        tvBalance = view.findViewById(R.id.tv_Balance)
        tvFacturaBalance = view.findViewById(R.id.tvFacturaBalance)
        tvFechaVencimiento1 = view.findViewById(R.id.tvFechaVencimiento1)
        tvFechaVencimiento2 = view.findViewById(R.id.tvFechaVencimiento2)

        btnVerMas.setOnClickListener {
            mostrarDialogoBalanceCC()
        }

        val userManager = UserManager(requireContext())
        val user = userManager.currentUser

        if (user != null) {
            clientId = user.id
            clientDNI = user.nationalIdentificationNumber
            Log.d("InicioFragment", "ID del usuario: $clientId")
            clientId?.let { fetchAccountData(it) }
        } else {
            showMessage("ID del usuario no encontrado")
        }

        return view
    }

    private fun fetchAccountData(clientId: String) {
        val accountUrl = "${ApiEndpoints.BASE_URL}/clients/$clientId/current_account"
        val invoicesUrl = "${ApiEndpoints.BASE_URL}/invoicing/invoices?client_national_identification_number_eq=$clientDNI"

        Log.d("InicioFragment", "URL final para obtener datos de cuenta: $accountUrl")
        Log.d("InicioFragment", "URL final para obtener facturas: $invoicesUrl")

        val accountRequest = Request.Builder()
            .url(accountUrl)
            .header("Authorization", "2865daf0-f236-46cf-b3c9-5ef541183a31")
            .build()

        ApiClient.getClient().newCall(accountRequest).enqueue(object : Callback {
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

                        activity?.runOnUiThread {
                            tvFacturaNoPagadas.text = "S/.$balanceAmount"
                            tvCreditoDisponible.text = "S/.$creditAmount"
                            tvBalance.text = "S/.$balanceAmount"
                        }

                        fetchInvoices(clientId)
                    } catch (e: Exception) {
                        Log.e("InicioFragment", "Error al procesar la respuesta JSON: ${e.message}")
                        e.printStackTrace()
                        activity?.runOnUiThread {
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

    private fun fetchInvoices(clientId: String) {
        val invoicesUrl = "${ApiEndpoints.BASE_URL}/invoicing/invoices?client_national_identification_number_eq=$clientDNI"
        Log.d("InicioFragment", "URL final para obtener facturas: $invoicesUrl")

        val invoiceRequest = Request.Builder()
            .url(invoicesUrl)
            .header("Authorization", "2865daf0-f236-46cf-b3c9-5ef541183a31")
            .build()

        ApiClient.getClient().newCall(invoiceRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.e("InicioFragment", "Error al obtener las facturas: ${e.message}")
                activity?.runOnUiThread {
                    showMessage("Error al obtener las facturas")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("InicioFragment", "Error en la respuesta del servidor: ${response.message()}")
                    activity?.runOnUiThread {
                        showMessage("Error en la respuesta del servidor")
                    }
                    return
                }

                val responseString = response.body()?.string()
                Log.d("InicioFragment", "Respuesta completa de facturas: $responseString")

                if (responseString.isNullOrEmpty()) {
                    Log.e("InicioFragment", "Respuesta vacía del servidor")
                    activity?.runOnUiThread {
                        showMessage("Respuesta vacía del servidor")
                    }
                    return
                }

                try {
                    val jsonObject = JSONObject(responseString)
                    Log.d("InicioFragment", "JSONObject obtenido: $jsonObject")

                    val jsonArray = jsonObject.getJSONArray("data")
                    Log.d("InicioFragment", "JSONArray de facturas: $jsonArray")

                    if (jsonArray.length() == 0) {
                        Log.d("InicioFragment", "No se encontraron facturas")
                        activity?.runOnUiThread {
                            tvFacturaBalance.text = "No hay facturas pendientes"
                            tvFechaVencimiento1.text = ""
                            tvFechaVencimiento2.text = ""
                            view?.findViewById<LinearLayout>(R.id.layout_factura_pendiente)?.visibility = View.GONE
                        }
                        return
                    }

                    val firstPendingInvoice = findFirstPendingInvoice(jsonArray)
                    Log.d("InicioFragment", "Primera factura pendiente: $firstPendingInvoice")

                    activity?.runOnUiThread {
                        if (firstPendingInvoice != null) {
                            Log.d("InicioFragment", "Actualizando UI con factura pendiente: ${firstPendingInvoice.balance}, ${firstPendingInvoice.first_due_date}, ${firstPendingInvoice.second_due_date}")
                            // Monto a pagar con negrita en la segunda línea
                            val balanceText = """
                            Monto a pagar:<br>
                            <b>S/ ${firstPendingInvoice.balance}</b>
                        """.trimIndent()
                            tvFacturaBalance.text = Html.fromHtml(balanceText, Html.FROM_HTML_MODE_LEGACY)

                            // Fechas de vencimiento
                            tvFechaVencimiento1.text = "Primera Fecha de Vencimiento: ${firstPendingInvoice.first_due_date}"
                            tvFechaVencimiento2.text = "Segunda Fecha de Vencimiento: ${firstPendingInvoice.second_due_date}"

                            // Muestra el layout de facturas pendientes
                            view?.findViewById<LinearLayout>(R.id.layout_factura_pendiente)?.visibility = View.VISIBLE
                        } else {
                            Log.d("InicioFragment", "No hay facturas pendientes")
                            tvFacturaBalance.text = "No hay facturas pendientes"
                            tvFechaVencimiento1.text = ""
                            tvFechaVencimiento2.text = ""
                            view?.findViewById<LinearLayout>(R.id.layout_factura_pendiente)?.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {
                    Log.e("InicioFragment", "Error al procesar la respuesta JSON de facturas: ${e.message}")
                    e.printStackTrace()
                    activity?.runOnUiThread {
                        showMessage("Error al procesar los datos de las facturas")
                    }
                }
            }
        })
    }





    private fun findFirstPendingInvoice(jsonArray: JSONArray): Factura? {
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val state = jsonObject.optString("state")
            Log.d("InicioFragment", "Estado de la factura: $state")
            if (state == "pending") {
                return Factura(
                    id = jsonObject.optString("id"),
                    balance = jsonObject.optString("balance"),
                    amount = jsonObject.optString("amount"),
                    state = jsonObject.optString("state"),
                    invoice_number = jsonObject.optString("invoice_number"),
                    first_due_date = jsonObject.optString("first_due_date"),
                    second_due_date = jsonObject.optString("second_due_date"),
                    issued_at = jsonObject.optString("issued_at")
                )
            }
        }
        return null
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
