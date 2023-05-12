package com.example.appasistencias

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.time.LocalDate
import java.util.*


class buscar : Fragment() {
    private val alumnosBus = arrayOf(
        Alumnobus("primero", LocalDate.now(), true),
        Alumnobus("Pedro", LocalDate.now(), false),
        Alumnobus("María", LocalDate.now(), true),
        Alumnobus("Luisa", LocalDate.now(), false),
        Alumnobus("Roberto", LocalDate.now(), true),
        Alumnobus("Juan", LocalDate.now(), false),
        Alumnobus("Pedro", LocalDate.now(), true),
        Alumnobus("María", LocalDate.now(), false),
        Alumnobus("Luisa", LocalDate.now(), false),
        Alumnobus("Roberto", LocalDate.now(), false),
        Alumnobus("Juan", LocalDate.now(), false),
        Alumnobus("Pedro", LocalDate.now(), false),
        Alumnobus("María", LocalDate.now(), false),
        Alumnobus("Luisa", LocalDate.now(), false),
        Alumnobus("Roberto", LocalDate.now(), false),
        Alumnobus("Juan", LocalDate.now(), false),
        Alumnobus("Pedro", LocalDate.now(), false),
        Alumnobus("María", LocalDate.now(), false),
        Alumnobus("Luisa", LocalDate.now(), false),
        Alumnobus("ultimo", LocalDate.now(), false)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_modificar, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.constraintLayout)

        val listView = view.findViewById<ListView>(R.id.listView)

        val elementosAdapter = Elementosbus(requireContext(), alumnosBus)
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

        listView.layoutParams = paramsListView
        btnDatePicker.layoutParams = paramsBtnDatePicker
    }


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
        fun newInstance(): buscar = buscar()
    }

    class Alumnobus(
        val nombre: String, val fecha: LocalDate, var asistencia: Boolean
    )
}
class Elementosbus(
    private val contexto: Context,
    private val alumnobus: Array<buscar.Alumnobus>
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
            val mensaje = if (isChecked) "¡El alumno asistió!" else "El alumno no asistió."
            Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
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

    fun imprimir() {
        for (i in 0 until alumnobus.size) {
            val alumno = alumnobus[i]
            val estadoAsistencia = if (alumno.asistencia) "Asistió" else "No asistió"
            println("Alumno ${i + 1}: ${alumno.nombre} - $estadoAsistencia")
        }
    }
}
