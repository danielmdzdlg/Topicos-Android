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

        insertarDatosDePrueba(db);
    }

    private void insertarDatosDePrueba(SQLiteDatabase db) {

        // ---------- 10 MÉDICOS ----------
        String[][] medicos = {
                {"Juan", "Pérez", "González", "CED001", "5512345671", "General"},
                {"María", "López", "Ramírez", "CED002", "5512345672", "Pediatría"},
                {"Carlos", "Hernández", "Cruz", "CED003", "5512345673", "Cardiología"},
                {"Ana", "Martínez", "Torres", "CED004", "5512345674", "Dermatología"},
                {"Luis", "García", "Vázquez", "CED005", "5512345675", "Neurología"},
                {"Laura", "Sánchez", "Díaz", "CED006", "5512345676", "Ginecología"},
                {"Jorge", "Ramírez", "Flores", "CED007", "5512345677", "Traumatología"},
                {"Patricia", "Torres", "Morales", "CED008", "5512345678", "Oncología"},
                {"Miguel", "Flores", "Reyes", "CED009", "5512345679", "Urología"},
                {"Sofía", "Cruz", "Jiménez", "CED010", "5512345680", "Psiquiatría"}
        };

        for (String[] m : medicos) {
            db.execSQL("INSERT INTO medicos (nombre, apellido_paterno, apellido_materno, cedula, telefono, especialidad) " +
                    "VALUES (?, ?, ?, ?, ?, ?)", new Object[]{m[0], m[1], m[2], m[3], m[4], m[5]});
        }

        // ---------- 10 PACIENTES ----------
        String[][] pacientes = {
                {"P001", "Fernando", "Gómez", "Ruiz", "Masculino", "1990-03-15", "5511111111"},
                {"P002", "Daniela", "Ortiz", "Castro", "Femenino", "1985-07-22", "5511111112"},
                {"P003", "Ricardo", "Mendoza", "Silva", "Masculino", "2000-01-10", "5511111113"},
                {"P004", "Valeria", "Rojas", "Núñez", "Femenino", "1995-11-30", "5511111114"},
                {"P005", "Alejandro", "Vargas", "Peña", "Masculino", "1978-06-05", "5511111115"},
                {"P006", "Camila", "Delgado", "Aguilar", "Femenino", "2002-09-18", "5511111116"},
                {"P007", "Diego", "Castillo", "Medina", "Masculino", "1988-12-25", "5511111117"},
                {"P008", "Renata", "Guerrero", "Ibarra", "Femenino", "1993-04-08", "5511111118"},
                {"P009", "Emilio", "Domínguez", "Salazar", "Masculino", "1999-08-14", "5511111119"},
                {"P010", "Ximena", "Cabrera", "Espinoza", "Femenino", "1982-02-27", "5511111120"}
        };

        for (String[] p : pacientes) {
            db.execSQL("INSERT INTO pacientes (id_paciente, nombre, apellido_paterno, apellido_materno, genero, fecha_nacimiento, telefono) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)", new Object[]{p[0], p[1], p[2], p[3], p[4], p[5], p[6]});
        }

        // ---------- 10 USUARIOS ----------
        String[][] usuarios = {
                {"admin", "1234"},
                {"usuario1", "pass1234"},
                {"usuario2", "pass1234"},
                {"usuario3", "pass1234"},
                {"usuario4", "pass1234"},
                {"usuario5", "pass1234"},
                {"usuario6", "pass1234"},
                {"usuario7", "pass1234"},
                {"usuario8", "pass1234"},
                {"usuario9", "pass1234"}
        };

        for (String[] u : usuarios) {
            db.execSQL("INSERT INTO usuarios (usuario, password) VALUES (?, ?)",
                    new Object[]{u[0], u[1]});
        }

        // ---------- 10 CONSULTAS (id_paciente P001-P010, id_medico 1-10) ----------
        String[][] consultas = {
                {"P001", "1", "Gripe común", "10:30"},
                {"P002", "2", "Revisión pediátrica", "11:00"},
                {"P003", "3", "Dolor en el pecho", "09:15"},
                {"P004", "4", "Alergia cutánea", "12:45"},
                {"P005", "5", "Migraña recurrente", "13:20"},
                {"P006", "6", "Control ginecológico", "14:00"},
                {"P007", "7", "Fractura de brazo", "08:30"},
                {"P008", "8", "Chequeo oncológico", "15:10"},
                {"P009", "9", "Infección urinaria", "16:00"},
                {"P010", "10", "Consulta de ansiedad", "17:30"}
        };

        for (String[] c : consultas) {
            db.execSQL("INSERT INTO consultas (id_paciente, id_medico, diagnostico, hora_salida) VALUES (?, ?, ?, ?)",
                    new Object[]{c[0], c[1], c[2], c[3]});
        }

        // ---------- 10 INTERNAMIENTOS ----------
        String[][] internamientos = {
                {"P001", "2026-07-01", "08:00:00", "internado"},
                {"P002", "2026-07-02", "09:15:00", "internado"},
                {"P003", "2026-07-03", "10:30:00", "dado_de_alta"},
                {"P004", "2026-07-04", "11:45:00", "internado"},
                {"P005", "2026-07-05", "12:00:00", "dado_de_alta"},
                {"P006", "2026-07-06", "13:30:00", "internado"},
                {"P007", "2026-07-07", "14:15:00", "internado"},
                {"P008", "2026-07-08", "15:00:00", "dado_de_alta"},
                {"P009", "2026-07-09", "16:45:00", "internado"},
                {"P010", "2026-07-10", "17:20:00", "internado"}
        };

        for (String[] i : internamientos) {
            db.execSQL("INSERT INTO internamientos (id_paciente, fecha_entrada, hora_entrada, estado) VALUES (?, ?, ?, ?)",
                    new Object[]{i[0], i[1], i[2], i[3]});
        }
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
        // Habilita las llaves foráneas (Foreign Keys) para que se respeten las relaciones
        db.setForeignKeyConstraintsEnabled(true);
    }
}