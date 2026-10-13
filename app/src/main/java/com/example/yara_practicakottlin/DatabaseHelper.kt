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
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_ALUMNOS", null)

        cursor?.use {
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
