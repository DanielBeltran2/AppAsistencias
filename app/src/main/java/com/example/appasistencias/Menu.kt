package com.example.appasistencias

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Menu : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var clase: Clase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        clase = intent.getParcelableExtra("clase") ?: Clase("", "")

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        // Mostrar el fragmento "Buscar" por defecto
        val buscarFragment = buscar.newInstance(clase)
        openFragment(buscarFragment)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragment: Fragment = when (item.itemId) {
            R.id.buscar -> {
                buscar.newInstance(clase)
            }
            R.id.tomarlista -> {
                Capturar.newInstance(clase)
            }
            R.id.modificar -> {
                modificar.newInstance(clase)
            }
            else -> return false
        }

        openFragment(fragment)
        return true
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
