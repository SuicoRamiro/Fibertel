package com.example.fibertel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class LoginQRActivity : AppCompatActivity() {
    private lateinit var qrScanLauncher: ActivityResultLauncher<Intent>
    private var linkMobileLogin: String? = null
    private lateinit var tvMessage: TextView

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
        val url = "https://www.cloud.wispro.co/api/v1/clients?national_identification_number_eq=$dni"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "2865daf0-f236-46cf-b3c9-5ef541183a31")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    showMessage("Error al obtener los datos del usuario")
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        showMessage("Error en la respuesta del servidor")
                    }
                    return
                }

                response.body()?.string()?.let { jsonString ->
                    val jsonObject = JSONObject(jsonString).getJSONArray("data").getJSONObject(0)
                    linkMobileLogin = jsonObject.getString("link_mobile_login")
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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
