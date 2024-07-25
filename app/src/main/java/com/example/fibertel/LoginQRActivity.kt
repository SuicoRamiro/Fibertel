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
    private var userManager: UserManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_qractivity)

        userManager = UserManager(this)
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

                    val user = User(
                        id = jsonObject.getString("id"),
                        name = jsonObject.getString("name"),
                        email = jsonObject.getString("email"),
                        phone = jsonObject.getString("phone_mobile"),
                        address = jsonObject.getString("address"),
                        nationalIdentificationNumber = jsonObject.getString("national_identification_number"),
                        linkMobileLogin = linkMobileLogin!!
                    )

                    userManager?.currentUser = user
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
            val user = userManager?.currentUser
            if (user != null) {
                runOnUiThread {
                    showMessage("Bienvenido ${user.name}")
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        } else {
            showMessage("URL inválida, intente nuevamente.")
        }
    }

    private fun showMessage(message: String) {
        runOnUiThread {
            tvMessage.text = message
            tvMessage.visibility = TextView.VISIBLE
        }
    }
}
