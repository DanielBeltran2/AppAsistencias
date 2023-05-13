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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.time.LocalDate
import java.util.*


class Capturar : Fragment() {
    @RequiresApi(Build.VERSION_CODES.O)
    private val alumnos = arrayOf(
        Alumno("primero", LocalDate.now(), false),
        Alumno("Pedro", LocalDate.now(), false),
        Alumno("María", LocalDate.now(), false),
        Alumno("Luisa", LocalDate.now(), false),
        Alumno("Roberto", LocalDate.now(), false),
        Alumno("Juan", LocalDate.now(), false),
        Alumno("Pedro", LocalDate.now(), false),
        Alumno("María", LocalDate.now(), false),
        Alumno("Luisa", LocalDate.now(), false),
        Alumno("Roberto", LocalDate.now(), false),
        Alumno("Juan", LocalDate.now(), false),
        Alumno("Pedro", LocalDate.now(), false),
        Alumno("María", LocalDate.now(), false),
        Alumno("Luisa", LocalDate.now(), false),
        Alumno("Roberto", LocalDate.now(), false),
        Alumno("Juan", LocalDate.now(), false),
        Alumno("Pedro", LocalDate.now(), false),
        Alumno("María", LocalDate.now(), false),
        Alumno("Luisa", LocalDate.now(), false),
        Alumno("ultimo", LocalDate.now(), false)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_capturar, container, false)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.listView)

        val elementosAdapter = Elementos(requireContext(), alumnos)
        listView.adapter = elementosAdapter

        listView.setOnItemClickListener { _, _, position, _ ->
            elementosAdapter.toggleAsistencia(position)
            elementosAdapter.notifyDataSetChanged()
        }

        val btnGuardar = Button(requireContext())
        btnGuardar.id = View.generateViewId()
        btnGuardar.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        btnGuardar.text = "Guardar"
        btnGuardar.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.button_rounded)
        btnGuardar.setOnClickListener { accionBoton(btnGuardar) }

        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.constraintLayout)
        constraintLayout.addView(btnGuardar)

        val paramsBtnGuardar = btnGuardar.layoutParams as ConstraintLayout.LayoutParams
        paramsBtnGuardar.endToEnd = R.id.listView
        paramsBtnGuardar.horizontalBias = 0.85f
        paramsBtnGuardar.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        paramsBtnGuardar.topToBottom = R.id.listView

        btnGuardar.layoutParams = paramsBtnGuardar

        val seleccionarGrupos = Spinner(requireContext())
        seleccionarGrupos.id = View.generateViewId()
        seleccionarGrupos.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        seleccionarGrupos.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.button_rounded)

        // Agregar elementos al Spinner
        val grupos = arrayOf("Grupo 1", "Grupo 2", "Grupo 3", "Grupo 4")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, grupos)
        seleccionarGrupos.adapter = adapter

        val paramsSeleccionarGrupos = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            topToBottom = btnGuardar.id
            marginStart = 16
            marginEnd = 16
        }

        constraintLayout.addView(seleccionarGrupos, paramsSeleccionarGrupos)
    }


    private fun accionBoton(btnGuardar: Button) {
        val mensaje = "¡Se han guardado los datos con exito!"
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(): Capturar = Capturar()
    }

    class Alumno(
        val nombre: String, val fecha: LocalDate, var asistencia: Boolean
    )
}

class Elementos(private val contexto: Context, private val alumnos: Array<Capturar.Alumno>) :
    BaseAdapter() {
    private val inflater: LayoutInflater =
        contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val aver: BooleanArray = BooleanArray(alumnos.size)

    init {
        for (i in aver.indices) {
            aver[i] = false
        }
    }

    override fun getView(i: Int, convertView: View?, parent: ViewGroup): View {
        var vista = convertView
        val holder: ViewHolder

        if (vista == null) {
            vista = inflater.inflate(R.layout.activity_capturar, null)
            holder = ViewHolder()
            holder.checkBox = vista.findViewById(R.id.checkBox)
            holder.tvalumno = vista.findViewById(R.id.tvAlumno)
            vista.tag = holder
        } else {
            holder = vista.tag as ViewHolder
        }

        val alumno = alumnos[i]
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
        return alumnos.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    fun toggleAsistencia(position: Int) {
        aver[position] = !aver[position]
        alumnos[position].asistencia = aver[position]
    }

    private class ViewHolder {
        lateinit var checkBox: CheckBox
        lateinit var tvalumno: TextView
    }
}