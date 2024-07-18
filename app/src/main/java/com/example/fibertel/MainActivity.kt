package com.example.fibertel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.fibertel.fragments.ConfiguracionFragment
import com.example.fibertel.fragments.FacturasFragment
import com.example.fibertel.fragments.InicioFragment
import com.example.fibertel.fragments.NotificacionesFragment
import com.example.fibertel.fragments.SoporteFragment
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<CurvedBottomNavigation>(R.id.bottomNavigation)

        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigation) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBarsInsets.bottom)
            insets
        }

        bottomNavigation.add(CurvedBottomNavigation.Model(1, "Inicio", R.drawable.ic_house))
        bottomNavigation.add(CurvedBottomNavigation.Model(2, "Facturas", R.drawable.ic_facturas))
        bottomNavigation.add(CurvedBottomNavigation.Model(3, "Soporte", R.drawable.ic_soporte))
        bottomNavigation.add(CurvedBottomNavigation.Model(4, "Notificaciones", R.drawable.ic_notificaciones))
        bottomNavigation.add(CurvedBottomNavigation.Model(5, "Configuracion", R.drawable.ic_configuracion))

        bottomNavigation.setOnClickMenuListener {
            when (it.id) {
                1 -> replaceFragment(InicioFragment())
                2 -> replaceFragment(FacturasFragment())
                3 -> replaceFragment(SoporteFragment())
                4 -> replaceFragment(NotificacionesFragment())
                5 -> replaceFragment(ConfiguracionFragment())
            }
        }

        // Inicio por defecto
        replaceFragment(InicioFragment())
        bottomNavigation.show(1)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
