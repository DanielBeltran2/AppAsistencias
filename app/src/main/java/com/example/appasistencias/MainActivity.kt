package com.example.appasistencias

import android.content.Intent
import android.os.Bundle
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
        val url = "http://165.232.118.127:8000/loginapp"
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener<String> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getInt("status")
                    val rol = jsonResponse.getString("rol_id")
                    val idUsuario = jsonResponse.getString("maestro_id")
                    val clase = Clase(rol, idUsuario)
                    if (status == 200) {
                        val rolInt = rol.toIntOrNull()
                        if (rolInt == 2) {
                            val ingresar = Intent(this, Menu::class.java)
                            ingresar.putExtra("clase", clase)
                            startActivity(ingresar)
                        }else{
                            Toast.makeText(this, "El Usuario no es un Maestro", Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        val message = jsonResponse.getString("message")
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["nombre"] = usuario.text.toString()
                params["password"] = contraseña.text.toString()
                return params
            }
        }

        requestQueue.add(stringRequest)
    }


}
