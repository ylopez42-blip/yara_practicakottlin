package com.example.yara_practicakottlin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                imageUriStr = uri.toString()
                imgAddFoto.setImageURI(uri)
                // Take persistable URI permission if needed
                try {
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (e: Exception) {
                    // Ignored if not required/supported
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
