package com.example.fibertel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.example.fibertel.model.User
import com.example.fibertel.model.UserManager
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class LoginQRActivity : AppCompatActivity() {
    private lateinit var qrScanLauncher: ActivityResultLauncher<Intent>
    private var linkMobileLogin: String? = null
    private lateinit var tvMessage: TextView
    private var userId: String? = null
    private var nombre: String? = null
    private var correo: String? = null
    private var direccion: String? = null
    private var telefono: String? = null
    private var identificacionNacional: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_qractivity)

        tvMessage = findViewById(R.id.tvMessage)

        val dni = intent.getStringExtra("DNI") ?: ""
        fetchUserData(dni)

        val buttonScanQR = findViewById<Button>(R.id.bt_QR)
        buttonScanQR.setOnClickListener {
            scanQRCode()
        }

        qrScanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data
            val scanResult = IntentIntegrator.parseActivityResult(resultCode, data)?.contents
            scanResult?.let { url ->
                compareUrls(url)
            } ?: run {
                showMessage("Escaneo cancelado")
            }
        }
    }

    private fun fetchUserData(dni: String) {
        val endpoint = ApiEndpoints.CLIENTS_BY_DNI + dni
        val request = ApiClient.createRequest(endpoint)

        ApiClient.getClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    showMessage("Error al obtener los datos del usuario")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        showMessage("Error en la respuesta del servidor")
                    }
                    return
                }

                response.body()?.string()?.let { jsonString ->
                    val jsonObject = JSONObject(jsonString).getJSONArray("data").getJSONObject(0)
                    linkMobileLogin = jsonObject.getString("link_mobile_login")
                    userId = jsonObject.getString("id")
                    UserManager.nombre = jsonObject.getString("name")
                    UserManager.email = jsonObject.getString("email")
                    UserManager.id = jsonObject.getString("id")
                    UserManager.telefono = jsonObject.getString("phone_mobile")
                    UserManager.direccion = jsonObject.getString("address")
                    UserManager.identificacionNacional = jsonObject.getString("national_identification_number")
                }
            }
        })
    }

    private fun scanQRCode() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Escanee el QR")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        qrScanLauncher.launch(integrator.createScanIntent())
    }

    private fun compareUrls(scannedUrl: String) {
        if (scannedUrl == linkMobileLogin) {
            userId?.let {
                fetchUserDetails(it)
            }
        } else {
            showMessage("URL inválida, intente nuevamente.")
        }
    }

    private fun fetchUserDetails(userId: String) {
        val endpoint = ApiEndpoints.CLIENT_DETAILS + userId
        val request = ApiClient.createRequest(endpoint)

        ApiClient.getClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    showMessage("Error al obtener los detalles del usuario")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        showMessage("Error en la respuesta del servidor")
                    }
                    return
                }

                val jsonString = response.body()?.string()
                jsonString?.let {
                    val user = Gson().fromJson(it, User::class.java)
                    UserManager.currentUser = user
                    runOnUiThread {
                        val intent = Intent(this@LoginQRActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                } ?: run {
                    runOnUiThread {
                        showMessage("Error al obtener los datos del usuario")
                    }
                }
            }
        })
    }

    private fun showMessage(message: String) {
        runOnUiThread {
            tvMessage.text = message
            tvMessage.visibility = TextView.VISIBLE
        }
    }
}
