package com.example.appasistencias

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var usuario: EditText
    private lateinit var contraseña: EditText
    private lateinit var buttonIngresar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonIngresar = findViewById(R.id.buttonIngresar)
        usuario = findViewById(R.id.editTextTextPersonName)
        contraseña = findViewById(R.id.editTextTextPassword)

        buttonIngresar.setOnClickListener {
            leer(usuario, contraseña)
        }
    }

    private fun leer(usuario: EditText, contraseña: EditText) {
        val url = "http://25.64.102.162/apitec/public/Login"

        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener<String> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getInt("status")
                    if (status == 200) {
                        val ingresar = Intent(this@MainActivity, Menu::class.java)
                        startActivity(ingresar)
                    } else {
                        val message = jsonResponse.getString("message")
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this@MainActivity, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                // Manejar errores de la solicitud
                Toast.makeText(this@MainActivity, error.toString(), Toast.LENGTH_SHORT).show()
            }

        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["nombre"] = usuario.text.toString()
                params["password"] = contraseña.text.toString()
                return params
            }
        }

        // Agregar la solicitud a una cola de solicitudes (por ejemplo, usando Volley)
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}
