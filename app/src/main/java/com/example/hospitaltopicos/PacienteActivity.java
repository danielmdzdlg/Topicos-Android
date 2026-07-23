package com.example.hospitaltopicos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PacienteActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);

        dbHelper = new DatabaseHelper(this);

        EditText etIdPaciente = findViewById(R.id.etIdPaciente);
        EditText etNombre = findViewById(R.id.etNombre);
        EditText etApellidoPaterno = findViewById(R.id.etApellidoPaterno);
        EditText etApellidoMaterno = findViewById(R.id.etApellidoMaterno);
        EditText etGenero = findViewById(R.id.etGenero);
        EditText etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        EditText etTelefono = findViewById(R.id.etTelefono);
        Button btnGuardarPaciente = findViewById(R.id.btnGuardarPaciente);
        TextView tvResultado = findViewById(R.id.tvResultado);

        Button btnInternar = findViewById(R.id.btnInternar);
        Button btnDarDeAlta = findViewById(R.id.btnDarDeAlta);
        TextView tvResultadoInternamiento = findViewById(R.id.tvResultadoInternamiento);

        // --- BOTÓN GUARDAR PACIENTE ---
        btnGuardarPaciente.setOnClickListener(v -> {
            String idPaciente = etIdPaciente.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String apellidoPaterno = etApellidoPaterno.getText().toString().trim();

            if (idPaciente.isEmpty() || nombre.isEmpty() || apellidoPaterno.isEmpty()) {
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Error: Faltan datos obligatorios (ID, Nombre, Apellido Paterno).");
                return;
            }

            SQLiteDatabase db = null;
            try {
                db = dbHelper.getWritableDatabase();

                ContentValues valores = new ContentValues();
                valores.put("id_paciente", idPaciente);
                valores.put("nombre", nombre);
                valores.put("apellido_paterno", apellidoPaterno);
                valores.put("apellido_materno", etApellidoMaterno.getText().toString().trim());
                valores.put("genero", etGenero.getText().toString().trim());
                valores.put("fecha_nacimiento", etFechaNacimiento.getText().toString().trim());
                valores.put("telefono", etTelefono.getText().toString().trim());

                // Usa insertOrThrow para forzar la excepción si hay duplicado
                long resultado = db.insertOrThrow("pacientes", null, valores);

                if (resultado != -1) {
                    tvResultado.setTextColor(Color.parseColor("#008800"));
                    tvResultado.setText("Paciente guardado correctamente.");

                    // Limpiar formulario
                    etNombre.setText("");
                    etApellidoPaterno.setText("");
                    etApellidoMaterno.setText("");
                    etGenero.setText("");
                    etFechaNacimiento.setText("");
                    etTelefono.setText("");
                }

            } catch (SQLiteConstraintException e) {
                // Captura específica cuando el ID del paciente ya existe
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Error: El expediente o ID del paciente ya está registrado.");
            } catch (SQLiteException e) {
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Error en la Base de Datos: " + e.getMessage());
            } catch (Exception e) {
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Error inesperado: " + e.getMessage());
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
        });

        // --- BOTÓN INTERNAR PACIENTE ---
        btnInternar.setOnClickListener(v -> {
            String idPaciente = etIdPaciente.getText().toString().trim();
            if (idPaciente.isEmpty()) {
                tvResultadoInternamiento.setTextColor(Color.RED);
                tvResultadoInternamiento.setText("Escribe el expediente del paciente.");
                return;
            }

            SQLiteDatabase dbInternar = null;
            try {
                dbInternar = dbHelper.getWritableDatabase();

                java.text.SimpleDateFormat sdfFecha = new java.text.SimpleDateFormat("yyyy-MM-dd");
                java.text.SimpleDateFormat sdfHora = new java.text.SimpleDateFormat("HH:mm:ss");
                String fechaActual = sdfFecha.format(new java.util.Date());
                String horaActual = sdfHora.format(new java.util.Date());

                ContentValues valoresInternamiento = new ContentValues();
                valoresInternamiento.put("id_paciente", idPaciente);
                valoresInternamiento.put("fecha_entrada", fechaActual);
                valoresInternamiento.put("hora_entrada", horaActual);
                valoresInternamiento.put("estado", "internado");

                long resultado = dbInternar.insert("internamientos", null, valoresInternamiento);

                if (resultado != -1) {
                    tvResultadoInternamiento.setTextColor(Color.parseColor("#008800"));
                    tvResultadoInternamiento.setText("Paciente internado a las " + horaActual);
                } else {
                    tvResultadoInternamiento.setTextColor(Color.RED);
                    tvResultadoInternamiento.setText("Error al internar paciente.");
                }

            } catch (SQLiteException e) {
                tvResultadoInternamiento.setTextColor(Color.RED);
                tvResultadoInternamiento.setText("Error de BD al internar: " + e.getMessage());
            } catch (Exception e) {
                tvResultadoInternamiento.setTextColor(Color.RED);
                tvResultadoInternamiento.setText("Error inesperado: " + e.getMessage());
            } finally {
                if (dbInternar != null && dbInternar.isOpen()) {
                    dbInternar.close();
                }
            }
        });

        // --- BOTÓN DAR DE ALTA ---
        btnDarDeAlta.setOnClickListener(v -> {
            String idPaciente = etIdPaciente.getText().toString().trim();
            if (idPaciente.isEmpty()) {
                tvResultadoInternamiento.setTextColor(Color.RED);
                tvResultadoInternamiento.setText("Escribe el expediente del paciente.");
                return;
            }

            SQLiteDatabase dbAlta = null;
            Cursor cursor = null;

            try {
                dbAlta = dbHelper.getWritableDatabase();

                // Busca el internamiento activo más reciente de ese paciente
                cursor = dbAlta.rawQuery(
                        "SELECT id_internamiento FROM internamientos WHERE id_paciente = ? AND estado = 'internado' " +
                                "ORDER BY id_internamiento DESC LIMIT 1",
                        new String[]{idPaciente});

                if (cursor != null && cursor.moveToFirst()) {
                    int idInternamiento = cursor.getInt(0);

                    java.text.SimpleDateFormat sdfFecha = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    java.text.SimpleDateFormat sdfHora = new java.text.SimpleDateFormat("HH:mm:ss");
                    String fechaActual = sdfFecha.format(new java.util.Date());
                    String horaActual = sdfHora.format(new java.util.Date());

                    ContentValues valoresAlta = new ContentValues();
                    valoresAlta.put("fecha_salida", fechaActual);
                    valoresAlta.put("hora_salida", horaActual);
                    valoresAlta.put("estado", "dado_de_alta");

                    int filas = dbAlta.update("internamientos", valoresAlta,
                            "id_internamiento = ?", new String[]{String.valueOf(idInternamiento)});

                    if (filas > 0) {
                        tvResultadoInternamiento.setTextColor(Color.parseColor("#008800"));
                        tvResultadoInternamiento.setText("Paciente dado de alta a las " + horaActual);
                    } else {
                        tvResultadoInternamiento.setTextColor(Color.RED);
                        tvResultadoInternamiento.setText("Error al actualizar la alta.");
                    }
                } else {
                    tvResultadoInternamiento.setTextColor(Color.RED);
                    tvResultadoInternamiento.setText("Este paciente no tiene internamiento activo.");
                }

            } catch (SQLiteException e) {
                tvResultadoInternamiento.setTextColor(Color.RED);
                tvResultadoInternamiento.setText("Error de BD al dar de alta: " + e.getMessage());
            } catch (Exception e) {
                tvResultadoInternamiento.setTextColor(Color.RED);
                tvResultadoInternamiento.setText("Error inesperado: " + e.getMessage());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (dbAlta != null && dbAlta.isOpen()) {
                    dbAlta.close();
                }
            }
        });

        // Inserta el fragment de consulta al abrir la pantalla
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentConsultaContainer, new ConsultaFragment())
                .commit();
    }
}