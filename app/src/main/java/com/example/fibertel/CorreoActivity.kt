package com.example.fibertel

// Importing necessary libraries
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * CorreoActivity is an activity class that extends AppCompatActivity.
 * This activity is responsible for handling the user interface and interactions within the Correo screen.
 */
class CorreoActivity : AppCompatActivity() {

    /**
     * This function is called when the activity is starting.
     * This is where most initialization should go.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display for the activity
        enableEdgeToEdge()

        // Set the user interface layout for this activity
        setContentView(R.layout.activity_correo)

        // Set an OnApplyWindowInsetsListener to the view identified by R.id.main
        // This listener is used to apply window insets to the view's padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            // Get the system window insets
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply the insets as padding to the view
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)


            insets
        }
    }
}