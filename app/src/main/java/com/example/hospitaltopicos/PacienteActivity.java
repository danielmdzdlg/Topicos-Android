package com.example.hospitaltopicos;

import android.database.Cursor;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
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

        btnInternar.setOnClickListener(v -> {
            String idPaciente = etIdPaciente.getText().toString().trim();
            if (idPaciente.isEmpty()) {
                tvResultadoInternamiento.setTextColor(0xFFFF0000);
                tvResultadoInternamiento.setText("Escribe el expediente del paciente");
                return;
            }

            java.text.SimpleDateFormat sdfFecha = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.text.SimpleDateFormat sdfHora = new java.text.SimpleDateFormat("HH:mm:ss");
            String fechaActual = sdfFecha.format(new java.util.Date());
            String horaActual = sdfHora.format(new java.util.Date());

            ContentValues valoresInternamiento = new ContentValues();
            valoresInternamiento.put("id_paciente", idPaciente);
            valoresInternamiento.put("fecha_entrada", fechaActual);
            valoresInternamiento.put("hora_entrada", horaActual);
            valoresInternamiento.put("estado", "internado");

            SQLiteDatabase dbInternar = dbHelper.getWritableDatabase();
            long resultado = dbInternar.insert("internamientos", null, valoresInternamiento);

            if (resultado != -1) {
                tvResultadoInternamiento.setTextColor(0xFF008800);
                tvResultadoInternamiento.setText("Paciente internado a las " + horaActual);
            } else {
                tvResultadoInternamiento.setTextColor(0xFFFF0000);
                tvResultadoInternamiento.setText("Error al internar paciente");
            }
        });

        btnDarDeAlta.setOnClickListener(v -> {
            String idPaciente = etIdPaciente.getText().toString().trim();
            if (idPaciente.isEmpty()) {
                tvResultadoInternamiento.setTextColor(0xFFFF0000);
                tvResultadoInternamiento.setText("Escribe el expediente del paciente");
                return;
            }

            SQLiteDatabase dbAlta = dbHelper.getWritableDatabase();

            // Busca el internamiento activo más reciente de ese paciente
            Cursor cursor = dbAlta.rawQuery(
                    "SELECT id_internamiento FROM internamientos WHERE id_paciente = ? AND estado = 'internado' " +
                            "ORDER BY id_internamiento DESC LIMIT 1",
                    new String[]{idPaciente});

            if (cursor.moveToFirst()) {
                int idInternamiento = cursor.getInt(0);
                cursor.close();

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
                    tvResultadoInternamiento.setTextColor(0xFF008800);
                    tvResultadoInternamiento.setText("Paciente dado de alta a las " + horaActual);
                } else {
                    tvResultadoInternamiento.setTextColor(0xFFFF0000);
                    tvResultadoInternamiento.setText("Error al dar de alta");
                }
            } else {
                cursor.close();
                tvResultadoInternamiento.setTextColor(0xFFFF0000);
                tvResultadoInternamiento.setText("Este paciente no tiene internamiento activo");
            }
        });

        // Inserta el fragment de consulta al abrir la pantalla
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentConsultaContainer, new ConsultaFragment())
                .commit();

        btnGuardarPaciente.setOnClickListener(v -> {
            String idPaciente = etIdPaciente.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String apellidoPaterno = etApellidoPaterno.getText().toString().trim();

            if (idPaciente.isEmpty() || nombre.isEmpty() || apellidoPaterno.isEmpty()) {
                tvResultado.setTextColor(0xFFFF0000);
                tvResultado.setText("Faltan datos obligatorios");
                return;
            }

            ContentValues valores = new ContentValues();
            valores.put("id_paciente", idPaciente);
            valores.put("nombre", nombre);
            valores.put("apellido_paterno", apellidoPaterno);
            valores.put("apellido_materno", etApellidoMaterno.getText().toString().trim());
            valores.put("genero", etGenero.getText().toString().trim());
            valores.put("fecha_nacimiento", etFechaNacimiento.getText().toString().trim());
            valores.put("telefono", etTelefono.getText().toString().trim());

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            long resultado = db.insert("pacientes", null, valores);

            if (resultado != -1) {
                tvResultado.setTextColor(0xFF008800);
                tvResultado.setText("Paciente guardado correctamente");
            } else {
                tvResultado.setTextColor(0xFFFF0000);
                tvResultado.setText("Error al guardar (¿ID repetido?)");
            }
        });
    }
}