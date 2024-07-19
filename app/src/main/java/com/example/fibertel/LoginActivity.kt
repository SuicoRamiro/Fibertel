package com.example.fibertel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var tvMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tvMessage = findViewById(R.id.tvMessage)

        val editTextDNI = findViewById<EditText>(R.id.editTextDNI)
        val buttonContinue = findViewById<Button>(R.id.buttonContinue)

        buttonContinue.setOnClickListener {
            val dni = editTextDNI.text.toString()
            if (dni.isNotEmpty()) {
                fetchUserData(dni)
            } else {
                showMessage("Ingrese su DNI")
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

                val jsonString = response.body()?.string()
                if (jsonString != null) {
                    val dataArray = JSONObject(jsonString).getJSONArray("data")
                    if (dataArray.length() > 0) {
                        val intent = Intent(this@LoginActivity, LoginQRActivity::class.java)
                        intent.putExtra("DNI", dni)
                        startActivity(intent)
                    } else {
                        runOnUiThread {
                            showMessage("DNI no encontrado")
                        }
                    }
                } else {
                    runOnUiThread {
                        showMessage("DNI no encontrado")
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
