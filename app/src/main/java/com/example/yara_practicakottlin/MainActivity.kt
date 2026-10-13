package com.example.yara_practicakottlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewAlumnos: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var alumnoAdapter: AlumnoAdapter

    private val addAlumnoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            loadAlumnos()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerViewAlumnos = findViewById(R.id.recyclerViewAlumnos)
        tvEmpty = findViewById(R.id.tvEmpty)
        fabAgregar = findViewById(R.id.fabAgregar)

        recyclerViewAlumnos.layoutManager = LinearLayoutManager(this)
        dbHelper = DatabaseHelper(this)

        // Insert sample data if empty for demonstration
        insertSampleDataIfNeeded()

        loadAlumnos()

        fabAgregar.setOnClickListener {
            val intent = Intent(this, AddAlumnoActivity::class.java)
            addAlumnoLauncher.launch(intent)
        }
    }

    private fun insertSampleDataIfNeeded() {
        val existing = dbHelper.getAllAlumnos()
        if (existing.isEmpty()) {
            dbHelper.insertAlumno(Alumno(nombre = "Ana Karen Pérez", cuenta = "20210001", image = "", correo = "ana.perez@example.com"))
            dbHelper.insertAlumno(Alumno(nombre = "Carlos Gómez Ruiz", cuenta = "20210002", image = "", correo = "carlos.gomez@example.com"))
            dbHelper.insertAlumno(Alumno(nombre = "María Fernanda López", cuenta = "20210003", image = "", correo = "maria.lopez@example.com"))
        }
    }

    private fun loadAlumnos() {
        val alumnosList = dbHelper.getAllAlumnos()
        if (alumnosList.isEmpty()) {
            recyclerViewAlumnos.visibility = View.GONE
            tvEmpty.visibility = View.VISIBLE
        } else {
            recyclerViewAlumnos.visibility = View.VISIBLE
            tvEmpty.visibility = View.GONE
            alumnoAdapter = AlumnoAdapter(alumnosList)
            recyclerViewAlumnos.adapter = alumnoAdapter
        }
    }
}
