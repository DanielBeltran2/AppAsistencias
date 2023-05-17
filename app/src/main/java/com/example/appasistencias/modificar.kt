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
import java.util.*


class modificar(clase: Any?) : Fragment() {
    private var idUsuario: String = ""

    init {
        if (clase is Clase) {
            idUsuario = clase.id
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val alumnosMod = arrayOf(
        AlumnoMdd("primero", LocalDate.now(), true),
        AlumnoMdd("Pedro", LocalDate.now(), false),
        AlumnoMdd("María", LocalDate.now(), true),
        AlumnoMdd("Luisa", LocalDate.now(), false),
        AlumnoMdd("Roberto", LocalDate.now(), true),
        AlumnoMdd("Juan", LocalDate.now(), false),
        AlumnoMdd("Pedro", LocalDate.now(), true),
        AlumnoMdd("María", LocalDate.now(), false),
        AlumnoMdd("Luisa", LocalDate.now(), false),
        AlumnoMdd("Roberto", LocalDate.now(), false),
        AlumnoMdd("Juan", LocalDate.now(), false),
        AlumnoMdd("Pedro", LocalDate.now(), false),
        AlumnoMdd("María", LocalDate.now(), false),
        AlumnoMdd("Luisa", LocalDate.now(), false),
        AlumnoMdd("Roberto", LocalDate.now(), false),
        AlumnoMdd("Juan", LocalDate.now(), false),
        AlumnoMdd("Pedro", LocalDate.now(), false),
        AlumnoMdd("María", LocalDate.now(), false),
        AlumnoMdd("Luisa", LocalDate.now(), false),
        AlumnoMdd("ultimo", LocalDate.now(), false)
    )

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

        val elementosAdapter = ElementosMod(requireContext(), alumnosMod)
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

        val btnGuardar = Button(requireContext())
        btnGuardar.id = View.generateViewId()
        btnGuardar.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        btnGuardar.text = "Guardar"
        btnGuardar.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.button_rounded)
        btnGuardar.setOnClickListener { accionBoton(btnGuardar) }

        constraintLayout.addView(btnGuardar)

        val paramsListView = listView.layoutParams as ConstraintLayout.LayoutParams
        paramsListView.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        paramsListView.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        paramsListView.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID

        val paramsBtnDatePicker = btnDatePicker.layoutParams as ConstraintLayout.LayoutParams
        paramsBtnDatePicker.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        paramsBtnDatePicker.endToStart = btnGuardar.id
        paramsBtnDatePicker.topToBottom = listView.id

        val paramsBtnGuardar = btnGuardar.layoutParams as ConstraintLayout.LayoutParams
        paramsBtnGuardar.startToEnd = btnDatePicker.id
        paramsBtnGuardar.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        paramsBtnGuardar.topToBottom = listView.id
        paramsBtnGuardar.marginStart = 10

        listView.layoutParams = paramsListView
        btnDatePicker.layoutParams = paramsBtnDatePicker
        btnGuardar.layoutParams = paramsBtnGuardar

        val seleccionarGrupos = Spinner(requireContext())
        seleccionarGrupos.id = View.generateViewId()
        seleccionarGrupos.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        seleccionarGrupos.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.button_rounded)

        // Agregar elementos al Spinner
        obtenerGrupos(idUsuario) { grupos ->
            requireActivity().runOnUiThread {
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, grupos)
                seleccionarGrupos.adapter = adapter
            }
        }

        val paramsSeleccionarGrupos = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            topToBottom = btnDatePicker.id
            marginStart = 16
            marginEnd = 16
            topMargin = 16  // Ajustar el valor del margen superior según sea necesario
        }

        constraintLayout.addView(seleccionarGrupos, paramsSeleccionarGrupos)
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
                // ... Hacer algo con la fecha seleccionada ...
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }


    private fun accionBoton(btnGuardar: Button) {
        val mensaje = "¡Se han guardado los datos con éxito!"
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(clase: Any?): modificar = modificar(clase)
    }

    class AlumnoMdd(
        val nombre: String, val fecha: LocalDate, var asistencia: Boolean
    )
}

class ElementosMod(
    private val contexto: Context,
    private val alumnosmod: Array<modificar.AlumnoMdd>
) :
    BaseAdapter() {
    private val inflater: LayoutInflater =
        contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val aver: BooleanArray = BooleanArray(alumnosmod.size)

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

        val alumno = alumnosmod[i]
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
        return alumnosmod.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    fun toggleAsistencia(position: Int) {
        aver[position] = !aver[position]
        alumnosmod[position].asistencia = aver[position]
    }

    private class ViewHolder {
        lateinit var checkBox: CheckBox
        lateinit var tvalumno: TextView
    }


    fun obtenerGrupos(idMaestro: String) {
        val url = "http://165.232.118.127:8000/getgruposm"

        val jsonObject = JSONObject()
        jsonObject.put("maestro_id", idMaestro)

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
                // Procesar la respuesta JSON aquí
                if (responseData != null) {
                    val jsonResponse = JSONObject(responseData)
                    // Extraer los grupos del JSON y realizar las operaciones necesarias
                    val grupos = jsonResponse.getJSONArray("id")
                    // ...
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Manejar el error de conexión o solicitud fallida aquí
            }
        })
    }
}
