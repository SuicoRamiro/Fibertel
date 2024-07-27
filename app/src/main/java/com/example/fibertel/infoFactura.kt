package com.example.fibertel

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.fibertel.databinding.ActivityInfoFacturaBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class infoFactura : AppCompatActivity() {

    private lateinit var binding: ActivityInfoFacturaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoFacturaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recupera los datos del Intent
        val balance = intent.getStringExtra("balance") ?: "Monto no disponible"
        val issuedAt = intent.getStringExtra("issued_at") ?: "Fecha no disponible"
        val firstDueDate = intent.getStringExtra("first_due_date") ?: "Fecha no disponible"
        val secondDueDate = intent.getStringExtra("second_due_date") ?: "Fecha no disponible"
        val invoiceNumber = intent.getStringExtra("invoice_number") ?: "Número no disponible"

        // Mueve el parsing de las fechas a un hilo secundario
        Thread {
            val formattedIssuedAt = formatDate(issuedAt)
            val formattedFirstDueDate = formatDate(firstDueDate)
            val formattedSecondDueDate = formatDate(secondDueDate)

            // Actualiza la UI en el hilo principal
            Handler(Looper.getMainLooper()).post {
                binding.tvFacturaBalance.text = "Monto a pagar: $balance"
                binding.tvFacturaNumber.text = "Factura # $invoiceNumber"
                binding.tvFechaEmitida.text = "Emitida el: $formattedIssuedAt"
                binding.tvFechaVencimiento1.text = "Primera Fecha de Vencimiento: $formattedFirstDueDate"
                binding.tvFechaVencimiento2.text = "Segunda Fecha de Vencimiento: $formattedSecondDueDate"
            }
        }.start()

        // Configura el botón de retroceso
        binding.btnRetroceder.setOnClickListener {
            finish()  // Cierra la actividad y regresa a la anterior
        }
    }

    private fun formatDate(inputDate: String): String {
        val inputFormatDateTime = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" // Para fechas con tiempo
        val inputFormatDate = "yyyy-MM-dd" // Para fechas sin tiempo
        val outputFormat = "dd/MM/yyyy" // Formato deseado

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

