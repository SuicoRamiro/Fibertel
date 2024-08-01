package com.example.fibertel

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.fibertel.databinding.ActivityInfoFacturaBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class infoFactura : AppCompatActivity() {

    private lateinit var binding: ActivityInfoFacturaBinding
    private val client = OkHttpClient()
    private val apiToken = "2865daf0-f236-46cf-b3c9-5ef541183a31"
    private val PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoFacturaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verifica permisos de almacenamiento
        checkPermissions()

        // Recupera los datos del Intent
        val balance = intent.getStringExtra("balance") ?: "Monto no disponible"
        val issuedAt = intent.getStringExtra("issued_at") ?: "Fecha no disponible"
        val firstDueDate = intent.getStringExtra("first_due_date") ?: "Fecha no disponible"
        val secondDueDate = intent.getStringExtra("second_due_date") ?: "Fecha no disponible"
        val invoiceNumber = intent.getStringExtra("invoice_number") ?: "Número no disponible"
        val id = intent.getStringExtra("id")

        // Mueve el parsing de las fechas a un hilo secundario
        Thread {
            val formattedIssuedAt = formatDate(issuedAt)
            val formattedFirstDueDate = formatDate(firstDueDate)
            val formattedSecondDueDate = formatDate(secondDueDate)

            // Actualiza la UI en el hilo principal
            runOnUiThread {
                binding.tvFacturaBalance.text = "Monto a pagar: $$balance"
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

        // Configura el listener para descargar la factura
        binding.opcionDescargarFactura.setOnClickListener {
            val downloadUrl = "https://www.cloud.wispro.co/api/v1/invoicing/invoices/$id/download_pdf"
            downloadInvoice(downloadUrl)
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

    private fun downloadInvoice(url: String) {
        // Verifica si ya se han concedido los permisos de almacenamiento
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Thread {
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", apiToken)
                    .build()

                try {
                    val response: Response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val inputStream = response.body()?.byteStream()
                        val invoiceNumber = intent.getStringExtra("invoice_number") ?: "Factura"
                        val fileName = "FacturaFibertel#$invoiceNumber.pdf"
                        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                        val outputStream = FileOutputStream(file)

                        inputStream?.use { input ->
                            outputStream.use { output ->
                                input.copyTo(output)
                            }
                        }

                        runOnUiThread {
                            showDownloadNotification(file.absolutePath, fileName)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "Error al descargar la factura. Código: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        } else {
            Toast.makeText(this, "Permiso de almacenamiento no concedido", Toast.LENGTH_LONG).show()
        }
    }


    private fun showDownloadNotification(filePath: String, fileName: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager

        // Crea el canal de notificación (para Android 8.0 y superior)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "download_channel"
            val channelName = "Download Notifications"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val channel = android.app.NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        // Crea un Intent para abrir el archivo PDF
        val file = File(filePath)
        val fileUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.fileprovider", file)

        val openFileIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Crea un PendingIntent para el Intent con FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openFileIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = android.app.Notification.Builder(this)
            .setContentTitle("$fileName Descarga")
            .setContentText("Factura: $fileName")
            .setSmallIcon(R.drawable.ic_desc_factura) // Reemplaza con tu icono de notificación
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // Configura el PendingIntent
            .apply {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    setChannelId("download_channel")
                }
            }

        notificationManager.notify(1, notificationBuilder.build())
    }




    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permiso concedido, puedes continuar con la descarga
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }
}
