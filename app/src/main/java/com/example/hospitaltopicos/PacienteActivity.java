package com.example.hospitaltopicos;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
        Spinner spinnerGenero = findViewById(R.id.spinnerGenero);
        EditText etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        EditText etTelefono = findViewById(R.id.etTelefono);
        Button btnGuardarPaciente = findViewById(R.id.btnGuardarPaciente);
        TextView tvResultado = findViewById(R.id.tvResultado);

        Button btnInternar = findViewById(R.id.btnInternar);
        Button btnDarDeAlta = findViewById(R.id.btnDarDeAlta);
        TextView tvResultadoInternamiento = findViewById(R.id.tvResultadoInternamiento);

        // --- RESTRICCIÓN DE TELÉFONO: SOLO NÚMEROS Y MÁXIMO 10 DÍGITOS ---
        etTelefono.setInputType(InputType.TYPE_CLASS_NUMBER);
        etTelefono.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(10) });

        // --- FILTRO: BLOQUEAR NÚMEROS Y SÍMBOLOS EN NOMBRES ---
        InputFilter filtroSoloLetras = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Character.isLetter(source.charAt(i)) && !Character.isWhitespace(source.charAt(i))) {
                    return ""; // Bloquea si no es letra o espacio
                }
            }
            return null;
        };

        etNombre.setFilters(new InputFilter[]{filtroSoloLetras});
        etApellidoPaterno.setFilters(new InputFilter[]{filtroSoloLetras});
        etApellidoMaterno.setFilters(new InputFilter[]{filtroSoloLetras});

        // --- CALENDARIO (DATEPICKER) PARA FECHA DE NACIMIENTO ---
        etFechaNacimiento.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int anio = calendar.get(Calendar.YEAR);
            int mes = calendar.get(Calendar.MONTH);
            int dia = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    PacienteActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        String fechaSeleccionada = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                        etFechaNacimiento.setText(fechaSeleccionada);
                    }, anio, mes, dia);

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        // --- BOTÓN GUARDAR PACIENTE ---
        btnGuardarPaciente.setOnClickListener(v -> {
            String idPaciente = etIdPaciente.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String apellidoPaterno = etApellidoPaterno.getText().toString().trim();
            String fechaNacimiento = etFechaNacimiento.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();

            String apellidoMaterno = etApellidoMaterno.getText().toString().trim();
            String genero = (spinnerGenero.getSelectedItemPosition() > 0 && spinnerGenero.getSelectedItem() != null)
                    ? spinnerGenero.getSelectedItem().toString() : "";

            if (idPaciente.isEmpty() || nombre.isEmpty() || apellidoPaterno.isEmpty() ||
                    apellidoMaterno.isEmpty() || genero.isEmpty() || fechaNacimiento.isEmpty() || telefono.isEmpty()) {
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Error: Todos los campos son obligatorios.");
                return;
            }

            // Validar teléfono exactamente de 10 dígitos si no está vacío
            if (!telefono.isEmpty() && telefono.length() != 10) {
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Error: El teléfono debe tener exactamente 10 dígitos.");
                return;
            }

            // Validar formato de fecha si no está vacía (Excepción ParseException)
            if (!fechaNacimiento.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    sdf.setLenient(false);
                    sdf.parse(fechaNacimiento);
                } catch (ParseException e) {
                    tvResultado.setTextColor(Color.RED);
                    tvResultado.setText("Error: Formato de fecha inválido.");
                    return;
                }
            }

            SQLiteDatabase db = null;
            try {
                db = dbHelper.getWritableDatabase();


                ContentValues valores = new ContentValues();
                valores.put("id_paciente", idPaciente);
                valores.put("nombre", nombre);
                valores.put("apellido_paterno", apellidoPaterno);
                valores.put("apellido_materno", etApellidoMaterno.getText().toString().trim());
                valores.put("genero", genero);
                valores.put("fecha_nacimiento", fechaNacimiento);
                valores.put("telefono", telefono);

                long resultado = db.insertOrThrow("pacientes", null, valores);

                if (resultado != -1) {
                    tvResultado.setTextColor(Color.parseColor("#008800"));
                    tvResultado.setText("Paciente guardado correctamente.");

                    // Limpiar campos
                    etIdPaciente.setText("");
                    etNombre.setText("");
                    etApellidoPaterno.setText("");
                    etApellidoMaterno.setText("");
                    etFechaNacimiento.setText("");
                    etTelefono.setText("");
                }

            } catch (SQLiteConstraintException e) {
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Error: El expediente (ID) ya está registrado.");
            } catch (NullPointerException e) {
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Error: Referencia nula al guardar campos.");
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

                SimpleDateFormat sdfFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
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
                tvResultadoInternamiento.setText("Error de BD: " + e.getMessage());
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

                cursor = dbAlta.rawQuery(
                        "SELECT id_internamiento FROM internamientos WHERE id_paciente = ? AND estado = 'internado' " +
                                "ORDER BY id_internamiento DESC LIMIT 1",
                        new String[]{idPaciente});

                if (cursor != null && cursor.moveToFirst()) {
                    int idInternamiento = cursor.getInt(0);

                    SimpleDateFormat sdfFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
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
                        tvResultadoInternamiento.setText("Error al dar de alta.");
                    }
                } else {
                    tvResultadoInternamiento.setTextColor(Color.RED);
                    tvResultadoInternamiento.setText("Este paciente no tiene internamiento activo.");
                }

            } catch (SQLiteException e) {
                tvResultadoInternamiento.setTextColor(Color.RED);
                tvResultadoInternamiento.setText("Error de BD: " + e.getMessage());
            } finally {
                if (cursor != null) cursor.close();
                if (dbAlta != null && dbAlta.isOpen()) dbAlta.close();
            }
        });

        // Inserta el fragment de consulta
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentConsultaContainer, new ConsultaFragment())
                .commit();
    }
}