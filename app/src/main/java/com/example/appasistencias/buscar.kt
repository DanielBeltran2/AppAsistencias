package com.example.appasistencias

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class buscar(clase: Any?) : Fragment() {

    private var idUsuario: String = ""
    private val alumnos = mutableListOf<Alumnobus>()
    private var fecha: String = ""
    private var grupoId: String = ""

    private lateinit var elementosAdapter: Elementosbus

    init {
        if (clase is Clase) {
            idUsuario = clase.id
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_modificar, container, false)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.constraintLayout)

        val listView = view.findViewById<ListView>(R.id.listView)

        elementosAdapter = Elementosbus(requireContext(), alumnos)
        listView.adapter = elementosAdapter

        listView.setOnItemClickListener { _, _, position, _ ->
            elementosAdapter.toggleAsistencia(position)
            elementosAdapter.notifyDataSetChanged()
        }

        val btnDatePicker = Button(requireContext())
        btnDatePicker.id = View.generateViewId()
        btnDatePicker.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        btnDatePicker.text = "Seleccionar fecha"
        btnDatePicker.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.button_rounded)
        btnDatePicker.setOnClickListener { showDatePicker() }

        constraintLayout.addView(btnDatePicker)

        val paramsListView = listView.layoutParams as ConstraintLayout.LayoutParams
        paramsListView.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        paramsListView.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        paramsListView.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID

        val paramsBtnDatePicker = btnDatePicker.layoutParams as ConstraintLayout.LayoutParams
        paramsBtnDatePicker.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        paramsBtnDatePicker.topToBottom = listView.id
        paramsBtnDatePicker.marginStart = 40
        paramsBtnDatePicker.marginEnd = 20 // Ajusta el valor del margen según sea necesario

        listView.layoutParams = paramsListView
        btnDatePicker.layoutParams = paramsBtnDatePicker

        val seleccionarGrupos = Spinner(requireContext())
        seleccionarGrupos.id = View.generateViewId()
        seleccionarGrupos.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        seleccionarGrupos.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.button_rounded)
        seleccionarGrupos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val grupoSeleccionado = parent.getItemAtPosition(position).toString()
                grupoId = grupoSeleccionado.substringAfter("Grupo ").toInt().toString()

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Lógica que se ejecuta cuando no se ha seleccionado ningún elemento
            }
        }

        constraintLayout.addView(seleccionarGrupos)

        val paramsSeleccionarGrupos = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            topToBottom = btnDatePicker.id
            marginStart = 16
            marginEnd = 16
            topMargin = 16 // Ajusta el valor del margen superior según sea necesario
        }

        seleccionarGrupos.layoutParams = paramsSeleccionarGrupos

        obtenerGrupos(idUsuario) { grupos ->
            requireActivity().runOnUiThread {
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, grupos)
                seleccionarGrupos.adapter = adapter
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                fecha = selectedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                obtenerAlumnos { alumnos, error ->
                    if (error != null) {
                        // Manejar el error
                    } else if (alumnos != null) {
                        this@buscar.alumnos.clear()
                        this@buscar.alumnos.addAll(alumnos)

                        activity?.runOnUiThread {
                            elementosAdapter.notifyDataSetChanged()
                        }
                    }
                }
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun obtenerAlumnos(
        callback: (List<Alumnobus>?, error: String?) -> Unit
    ) {
        val url = "http://165.232.118.127:8000/asistenciafecha"

        val jsonObject = JSONObject()
        jsonObject.put("fecha", fecha)
        jsonObject.put("grupo_id", grupoId)

        val requestBody =
            RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body()?.string()

                if (responseData != null) {
                    val jsonResponse = JSONObject(responseData)
                    val grupoArray = jsonResponse.getJSONArray("grupo")
                    val nuevosAlumnos = mutableListOf<Alumnobus>()

                    for (i in 0 until grupoArray.length()) {
                        val alumnoJson = grupoArray.getJSONObject(i)
                        val id = alumnoJson.getString("id")
                        val nombre = alumnoJson.getString("nombre")
                        val asistenciaString = alumnoJson.getString("asistencia")
                        val asistencia = asistenciaString == "1"

                        val alumno = Alumnobus(nombre, fecha, asistencia, id, grupoId)
                        nuevosAlumnos.add(alumno)
                    }

                    activity?.runOnUiThread {
                        alumnos.clear()
                        alumnos.addAll(nuevosAlumnos)
                        elementosAdapter.notifyDataSetChanged()
                    }

                    callback(nuevosAlumnos, null)
                } else {
                    callback(null, "Error: No se pudo obtener la respuesta del servidor")
                }
            }


            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Error de conexión: ${e.message}")
            }
        })
    }


    private fun obtenerGrupos(idUsuario: String, callback: (List<String>) -> Unit) {
        val url = "http://165.232.118.127:8000/getgruposm"

        val jsonObject = JSONObject()
        jsonObject.put("maestro_id", idUsuario)


        val requestBody =
            RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body()?.string()
                val grupoIds = mutableListOf<String>()

                if (responseData != null) {
                    val jsonResponse = JSONObject(responseData)
                    val gruposjson = jsonResponse.getJSONArray("grupos")

                    for (i in 0 until gruposjson.length()) {
                        val grupoId = "Grupo " + gruposjson.getJSONObject(i).getString("id")
                        grupoIds.add(grupoId)
                    }
                }

                callback(grupoIds)
            }

            override fun onFailure(call: Call, e: IOException) {
                // Manejar el error de conexión o solicitud fallida aquí
            }
        })
    }


    private fun accionBoton(btnGuardar: Button) {
        val mensaje = "¡Se han guardado los datos con éxito!"
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(clase: Any?): buscar = buscar(clase)

    }

    class Alumnobus(
        val nombre: String,
        val fecha: String,
        var asistencia: Boolean,
        val id: String,
        var grupoId: String
    )


}

class Elementosbus(
    private val contexto: Context,
    private val alumnobus: MutableList<buscar.Alumnobus>
) :
    BaseAdapter() {
    private val inflater: LayoutInflater =
        contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val aver: BooleanArray = BooleanArray(alumnobus.size)

    init {
        for (i in aver.indices) {
            aver[i] = false
        }
    }

    override fun getView(i: Int, convertView: View?, parent: ViewGroup): View {
        var vista = convertView
        val holder: ViewHolder

        if (vista == null) {
            vista = inflater.inflate(R.layout.activity_modificar, null)
            holder = ViewHolder()
            holder.checkBox = vista.findViewById(R.id.checkBox)
            holder.tvalumno = vista.findViewById(R.id.tvAlumno)
            vista.tag = holder
        } else {
            holder = vista.tag as ViewHolder
        }

        val alumno = alumnobus[i]
        holder.checkBox.visibility = View.VISIBLE
        holder.tvalumno.visibility = View.VISIBLE
        holder.checkBox.isChecked = alumno.asistencia
        holder.tvalumno.text = alumno.nombre

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            alumno.asistencia = isChecked
            //cositas
        }

        return vista!!
    }

    override fun getCount(): Int {
        return alumnobus.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    fun toggleAsistencia(position: Int) {
        aver[position] = !aver[position]
        alumnobus[position].asistencia = aver[position]
    }

    private class ViewHolder {
        lateinit var checkBox: CheckBox
        lateinit var tvalumno: TextView
    }

}
