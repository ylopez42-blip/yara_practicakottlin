package com.example.yara_practicakottlin

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class AlumnoAdapter(private val alumnosList: List<Alumno>) :
    RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder>() {

    class AlumnoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAlumno: ImageView = itemView.findViewById(R.id.imgAlumno)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvCuenta: TextView = itemView.findViewById(R.id.tvCuenta)
        val tvCorreo: TextView = itemView.findViewById(R.id.tvCorreo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumno, parent, false)
        return AlumnoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = alumnosList[position]
        holder.tvNombre.text = alumno.nombre
        holder.tvCuenta.text = "Cuenta: ${alumno.cuenta}"
        holder.tvCorreo.text = alumno.correo

        if (alumno.image.isNotBlank()) {
            try {
                if (alumno.image.startsWith("content://") || alumno.image.startsWith("file://")) {
                    holder.imgAlumno.setImageURI(Uri.parse(alumno.image))
                } else {
                    val file = File(alumno.image)
                    if (file.exists()) {
                        holder.imgAlumno.setImageURI(Uri.parse(file.absolutePath))
                    } else {
                        holder.imgAlumno.setImageResource(R.drawable.ic_launcher_foreground)
                    }
                }
            } catch (e: Exception) {
                holder.imgAlumno.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } else {
            holder.imgAlumno.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }

    override fun getItemCount(): Int = alumnosList.size
}
