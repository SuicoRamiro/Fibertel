package com.example.fibertel

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.fibertel.model.UserManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class ReportarProblemaActivity : AppCompatActivity() {

    private lateinit var tituloProblema: EditText
    private lateinit var descripcionProblema: EditText
    private lateinit var spinnerProblema: Spinner

    private var tituloProblemaTexto: String = ""
    private var descripcionProblemaTexto: String = ""
    private var categoriaProblema: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reportar_problema)

        val spinnerHint: TextView = findViewById(R.id.spinner_hint)
        spinnerProblema = findViewById(R.id.spinner_problema)

        // Crear un ArrayAdapter usando un array de strings y un layout por defecto para el spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.problema_array, // Array definido en recursos
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Especificar el layout a usar cuando se despliegan las opciones
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Aplicar el adaptador al spinner
            spinnerProblema.adapter = adapter
        }

        // Configurar el comportamiento del Spinner
        spinnerHint.setOnClickListener {
            spinnerHint.visibility = View.GONE
            spinnerProblema.visibility = View.VISIBLE
            spinnerProblema.performClick()
        }


        spinnerProblema.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != 2) {
                    spinnerHint.visibility = View.GONE
                    categoriaProblema = when (position) {
                        0 -> "e38a327a-341f-4f17-999b-caf97ef816ee" // ID para "Lentitud"
                        1 -> "1d294e6f-781a-4d9e-8237-359084263f41" // ID para "Sin Conexión"
                        else -> ""
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }

        // Inicializar los EditTexts
        tituloProblema = findViewById(R.id.tituloProblema)
        descripcionProblema = findViewById(R.id.descripcionProblema)

        // Inicializar el botón de retroceso
        val btnRetroceder: ImageButton = findViewById(R.id.btn_retroceder)
        btnRetroceder.setOnClickListener {
            finish() // Cierra la actividad y vuelve al fragmento anterior
        }

        // Inicializar el botón de enviar
        val btnEnviar: Button = findViewById(R.id.botonEnviarReporte)
        btnEnviar.setOnClickListener {
            // Obtener el texto del EditText y almacenarlo en variables de instancia
            tituloProblemaTexto = tituloProblema.text.toString()
            descripcionProblemaTexto = descripcionProblema.text.toString()

            // Verificar que todos los campos estén completos antes de enviar
            if (tituloProblemaTexto.isNotEmpty() && descripcionProblemaTexto.isNotEmpty() && categoriaProblema.isNotEmpty()) {
                // Enviar los datos del ticket al servidor
                sendTicketToServer()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendTicketToServer() {
        // Recupera el usuario almacenado en UserManager
        val userManager = UserManager(this)
        val user = userManager.currentUser


        val clientId = user?.id // Obtén el ID
        val contractId = "your_contract_id" // Obtén el ID del contrato de donde sea necesario

        val json = """
            {
                "client_id": "$clientId",
                "contract_id": "$contractId",
                "category_id": "$categoriaProblema",
                "title": "$tituloProblemaTexto",
                "description": "$descripcionProblemaTexto",
                "assignable_id": "" // Si hay algún ID asignable, colócalo aquí
            }
        """.trimIndent()

        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        val request = Request.Builder()
            .url("https://www.cloud.wispro.co/api/v1/help_desk/issues")
            .header("Authorization", "2865daf0-f236-46cf-b3c9-5ef541183a31")
            .post(requestBody)
            .build()

        ApiClient.getClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ReportarProblemaActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ReportarProblemaActivity, "Ticket Created Successfully", Toast.LENGTH_SHORT).show()
                        finish() // Cierra la actividad después de crear el ticket
                    } else {
                        val errorMessage = response.body()?.string() ?: "Unknown Error"
                        Toast.makeText(this@ReportarProblemaActivity, "Failed: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}
