package com.example.fibertel.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.fibertel.ApiClient
import com.example.fibertel.R
import com.example.fibertel.network.ApiEndpoints
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
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
