package com.example.hospitaltopicos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hospital.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE medicos (" +
                "id_medico INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "apellido_paterno TEXT NOT NULL," +
                "apellido_materno TEXT," +
                "cedula TEXT NOT NULL UNIQUE," +
                "telefono TEXT," +
                "especialidad TEXT)");

        db.execSQL("CREATE TABLE pacientes (" +
                "id_paciente TEXT PRIMARY KEY," +
                "nombre TEXT NOT NULL," +
                "apellido_paterno TEXT NOT NULL," +
                "apellido_materno TEXT," +
                "genero TEXT," +
                "fecha_nacimiento TEXT NOT NULL," +
                "telefono TEXT)");

        db.execSQL("CREATE TABLE consultas (" +
                "id_consulta INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_paciente TEXT NOT NULL," +
                "id_medico INTEGER NOT NULL," +
                "diagnostico TEXT," +
                "hora_salida TEXT," +
                "fecha_consulta TEXT DEFAULT (datetime('now'))," +
                "FOREIGN KEY(id_paciente) REFERENCES pacientes(id_paciente)," +
                "FOREIGN KEY(id_medico) REFERENCES medicos(id_medico))");

        db.execSQL("CREATE TABLE usuarios (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL)");

        db.execSQL("CREATE TABLE internamientos (" +
                "id_internamiento INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_paciente TEXT NOT NULL," +
                "fecha_entrada TEXT NOT NULL," +
                "hora_entrada TEXT NOT NULL," +
                "fecha_salida TEXT," +
                "hora_salida TEXT," +
                "estado TEXT DEFAULT 'internado'," +
                "FOREIGN KEY(id_paciente) REFERENCES pacientes(id_paciente))");

        // Usuario de prueba para el login
        db.execSQL("INSERT INTO usuarios (usuario, password) VALUES ('admin', '1234')");
        db.execSQL("INSERT INTO medicos (nombre, apellido_paterno, cedula, especialidad) VALUES ('Juan', 'Pérez', '12345', 'General')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS medicos");
        db.execSQL("DROP TABLE IF EXISTS pacientes");
        db.execSQL("DROP TABLE IF EXISTS consultas");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS internamientos");
        onCreate(db);
    }
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Habilita las Ilaves foráneas (Foreign Keys) para que se respeten las relaciones
        db.setForeignKeyConstraintsEnabled(true);
    }
}