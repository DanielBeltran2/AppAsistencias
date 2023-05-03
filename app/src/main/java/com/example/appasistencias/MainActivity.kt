package com.example.appasistencias

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val buttonIngresar: Button =
            findViewById(R.id.buttonIngresar)


        buttonIngresar.setOnClickListener(View.OnClickListener {

            val ingresar = Intent(this, Menu::class.java)
            startActivity(ingresar)

        })
    }


}