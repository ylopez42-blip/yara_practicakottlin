# Guía de Desarrollo: Aplicación Android de Alumnos con SQLite y RecyclerView en Kotlin

Aplicación nativa desarrollada en **Android Studio** utilizando **Kotlin**, **RecyclerView**, **CardView** y almacenamiento local mediante **SQLite** (`SQLiteOpenHelper`).

---

## 📋 Requisitos del Proyecto
1. **Clase Modelo (`Alumno`)**: Atributos `nombre`, `cuenta`, `image` y `correo`.
2. **Base de Datos SQLite**: Tabla `"Alumnos"` con operaciones CRUD (Inserción y Consulta).
3. **Pantalla Principal (`MainActivity`)**: Listado de alumnos registrados utilizando un `RecyclerView` y un botón flotante (`FloatingActionButton`) para agregar nuevos registros.
4. **Pantalla de Registro (`AddAlumnoActivity`)**: Formulario para capturar y almacenar un nuevo alumno en SQLite.

---

## 🛠️ Arquitectura y Código Fuente

### 1. Modelo de Datos (`Alumno.kt`)
Define la estructura de los datos del alumno:
```kotlin
package com.example.yara_practicakottlin

data class Alumno(
    val id: Long = 0,
    val nombre: String,
    val cuenta: String,
    val image: String,
    val correo: String
)
```

### 2. Gestor de Base de Datos SQLite (`DatabaseHelper.kt`)
Extiende de `SQLiteOpenHelper` para administrar la creación de la base de datos `alumnos.db`, la tabla `Alumnos` y los métodos para insertar y consultar registros.
```kotlin
package com.example.yara_practicakottlin

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "alumnos.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_ALUMNOS = "Alumnos"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_CUENTA = "cuenta"
        const val COLUMN_IMAGE = "image"
        const val COLUMN_CORREO = "correo"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE " + TABLE_ALUMNOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NOMBRE + " TEXT, "
                + COLUMN_CUENTA + " TEXT, "
                + COLUMN_IMAGE + " TEXT, "
                + COLUMN_CORREO + " TEXT)")
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ALUMNOS")
        onCreate(db)
    }

    fun insertAlumno(alumno: Alumno): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_IMAGE, alumno.image)
            put(COLUMN_CORREO, alumno.correo)
        }
        val id = db.insert(TABLE_ALUMNOS, null, values)
        db.close()
        return id
    }

    fun getAllAlumnos(): List<Alumno> {
        val alumnosList = mutableListOf<Alumno>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_ALUMNOS", null)

        cursor.use {
            val idIndex = it.getColumnIndex(COLUMN_ID)
            val nombreIndex = it.getColumnIndex(COLUMN_NOMBRE)
            val cuentaIndex = it.getColumnIndex(COLUMN_CUENTA)
            val imageIndex = it.getColumnIndex(COLUMN_IMAGE)
            val correoIndex = it.getColumnIndex(COLUMN_CORREO)

            while (it.moveToNext()) {
                val id = if (idIndex != -1) it.getLong(idIndex) else 0L
                val nombre = if (nombreIndex != -1) it.getString(nombreIndex) ?: "" else ""
                val cuenta = if (cuentaIndex != -1) it.getString(cuentaIndex) ?: "" else ""
                val image = if (imageIndex != -1) it.getString(imageIndex) ?: "" else ""
                val correo = if (correoIndex != -1) it.getString(correoIndex) ?: "" else ""

                alumnosList.add(Alumno(id, nombre, cuenta, image, correo))
            }
        }
        db.close()
        return alumnosList
    }
}
```

### 3. Adaptador del RecyclerView (`AlumnoAdapter.kt`)
Conecta la lista de objetos `Alumno` con las vistas de la interfaz de usuario (`item_alumno.xml`).
```kotlin
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
            } catch (exception: Exception) {
                holder.imgAlumno.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } else {
            holder.imgAlumno.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }

    override fun getItemCount(): Int = alumnosList.size
}
```

### 4. Pantalla Principal (`MainActivity.kt`)
Carga los datos desde SQLite al iniciar (incluyendo registros de prueba si está vacía) y gestiona el RecyclerView y el botón flotante para agregar nuevos alumnos.
```kotlin
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
```

### 5. Pantalla de Agregar Alumno (`AddAlumnoActivity.kt`)
Permite registrar los datos de un nuevo alumno y almacenarlos en la base de datos SQLite.
```kotlin
package com.example.yara_practicakottlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class AddAlumnoActivity : AppCompatActivity() {

    private lateinit var imgAddFoto: ImageView
    private lateinit var btnSeleccionarFoto: Button
    private lateinit var etNombre: TextInputEditText
    private lateinit var etCuenta: TextInputEditText
    private lateinit var etCorreo: TextInputEditText
    private lateinit var btnGuardar: Button

    private var imageUriStr: String = ""

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                imageUriStr = uri.toString()
                imgAddFoto.setImageURI(uri)
                try {
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (exception: Exception) {
                    // Ignorado si no es necesario
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alumno)

        imgAddFoto = findViewById(R.id.imgAddFoto)
        btnSeleccionarFoto = findViewById(R.id.btnSeleccionarFoto)
        etNombre = findViewById(R.id.etNombre)
        etCuenta = findViewById(R.id.etCuenta)
        etCorreo = findViewById(R.id.etCorreo)
        btnGuardar = findViewById(R.id.btnGuardar)

        btnSeleccionarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            selectImageLauncher.launch(intent)
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val cuenta = etCuenta.text.toString().trim()
            val correo = etCorreo.text.toString().trim()

            if (nombre.isEmpty() || cuenta.isEmpty() || correo.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val alumno = Alumno(
                nombre = nombre,
                cuenta = cuenta,
                image = imageUriStr,
                correo = correo
            )

            val dbHelper = DatabaseHelper(this)
            val id = dbHelper.insertAlumno(alumno)

            if (id != -1L) {
                Toast.makeText(this, "Alumno guardado exitosamente", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Error al guardar el alumno", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```

---

## 📱 Instrucciones para Evidencia y Capturas de Pantalla

1. **Subir Repositorio a GitHub**:
   - Inicializa Git en tu proyecto (`git init`).
   - Añade los archivos (`git add .`), realiza el commit (`git commit -m "Implementación de Alumnos con RecyclerView y SQLite"`) y conéctalo con tu repositorio remoto de GitHub (`git remote add origin <URL-del-repositorio>`).
   - Sube los cambios con `git push -u origin main`.

2. **Capturas de Pantalla requeridas**:
   - **Pantalla Principal (`MainActivity`)**: Muestra el listado de alumnos en el `RecyclerView` cargados desde la base de datos SQLite y el botón flotante `+` para agregar.
   - **Pantalla de Registro (`AddAlumnoActivity`)**: Muestra el formulario con los campos `nombre`, `cuenta`, `correo` y el selector de imagen.
