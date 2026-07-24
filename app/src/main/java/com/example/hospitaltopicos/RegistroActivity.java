package com.example.hospitaltopicos;

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;

public class RegistroActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        dbHelper = new DatabaseHelper(this);

        View layoutMedico = findViewById(R.id.layoutMedico);
        View layoutUsuario = findViewById(R.id.layoutUsuario);
        Button btnTabMedico = findViewById(R.id.btnTabMedico);
        Button btnTabUsuario = findViewById(R.id.btnTabUsuario);

        btnTabMedico.setOnClickListener(v -> {
            layoutMedico.setVisibility(View.VISIBLE);
            layoutUsuario.setVisibility(View.GONE);
        });

        btnTabUsuario.setOnClickListener(v -> {
            layoutMedico.setVisibility(View.GONE);
            layoutUsuario.setVisibility(View.VISIBLE);
        });

        // ---------- FORMULARIO MÉDICO ----------
        EditText etMedNombre = findViewById(R.id.etMedNombre);
        EditText etMedApellidoPaterno = findViewById(R.id.etMedApellidoPaterno);
        EditText etMedApellidoMaterno = findViewById(R.id.etMedApellidoMaterno);
        EditText etMedCedula = findViewById(R.id.etMedCedula);
        EditText etMedTelefono = findViewById(R.id.etMedTelefono);
        EditText etMedEspecialidad = findViewById(R.id.etMedEspecialidad);
        Button btnGuardarMedico = findViewById(R.id.btnGuardarMedico);
        TextView tvResultadoMedico = findViewById(R.id.tvResultadoMedico);

        // --- Filtro: solo letras y espacios en nombre y apellidos ---
        InputFilter filtroSoloLetras = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Character.isLetter(source.charAt(i)) && !Character.isWhitespace(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        };

        etMedNombre.setFilters(new InputFilter[]{filtroSoloLetras});
        etMedApellidoPaterno.setFilters(new InputFilter[]{filtroSoloLetras});
        etMedApellidoMaterno.setFilters(new InputFilter[]{filtroSoloLetras});

// --- Restricción: teléfono solo números, máximo 10 dígitos ---
        etMedTelefono.setInputType(InputType.TYPE_CLASS_NUMBER);
        etMedTelefono.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(10) });

        btnGuardarMedico.setOnClickListener(v -> {
            String nombre = etMedNombre.getText().toString().trim();
            String apellidoPaterno = etMedApellidoPaterno.getText().toString().trim();
            String apellidoMaterno = etMedApellidoMaterno.getText().toString().trim();
            String cedula = etMedCedula.getText().toString().trim();
            String telefono = etMedTelefono.getText().toString().trim();
            String especialidad = etMedEspecialidad.getText().toString().trim();

            if (nombre.isEmpty() || apellidoPaterno.isEmpty() || apellidoMaterno.isEmpty()
                    || cedula.isEmpty() || telefono.isEmpty() || especialidad.isEmpty()) {
                tvResultadoMedico.setTextColor(Color.RED);
                tvResultadoMedico.setText("Error: Todos los campos son obligatorios.");
                return;
            }

            SQLiteDatabase db = null;
            try {
                db = dbHelper.getWritableDatabase();

                ContentValues valores = new ContentValues();
                valores.put("nombre", nombre);
                valores.put("apellido_paterno", apellidoPaterno);
                valores.put("apellido_materno", apellidoMaterno);
                valores.put("cedula", cedula);
                valores.put("telefono", telefono);
                valores.put("especialidad", especialidad);

                long resultado = db.insertOrThrow("medicos", null, valores);

                if (resultado != -1) {
                    tvResultadoMedico.setTextColor(Color.parseColor("#008800"));
                    tvResultadoMedico.setText("Médico registrado correctamente.");

                    etMedNombre.setText("");
                    etMedApellidoPaterno.setText("");
                    etMedApellidoMaterno.setText("");
                    etMedCedula.setText("");
                    etMedTelefono.setText("");
                    etMedEspecialidad.setText("");
                }

            } catch (SQLiteConstraintException e) {
                tvResultadoMedico.setTextColor(Color.RED);
                tvResultadoMedico.setText("Error: La cédula ya está registrada.");
            } catch (SQLiteException e) {
                tvResultadoMedico.setTextColor(Color.RED);
                tvResultadoMedico.setText("Error de base de datos: " + e.getMessage());
            } finally {
                if (db != null && db.isOpen()) db.close();
            }
        });

        // ---------- FORMULARIO USUARIO ----------
        EditText etUsrUsuario = findViewById(R.id.etUsrUsuario);
        EditText etUsrPassword = findViewById(R.id.etUsrPassword);
        Button btnGuardarUsuario = findViewById(R.id.btnGuardarUsuario);
        TextView tvResultadoUsuario = findViewById(R.id.tvResultadoUsuario);

        btnGuardarUsuario.setOnClickListener(v -> {
            String usuario = etUsrUsuario.getText().toString().trim();
            String password = etUsrPassword.getText().toString().trim();

            if (usuario.isEmpty() || password.isEmpty()) {
                tvResultadoUsuario.setTextColor(Color.RED);
                tvResultadoUsuario.setText("Error: Todos los campos son obligatorios.");
                return;
            }

            SQLiteDatabase db = null;
            try {
                db = dbHelper.getWritableDatabase();

                ContentValues valores = new ContentValues();
                valores.put("usuario", usuario);
                valores.put("password", password);

                long resultado = db.insertOrThrow("usuarios", null, valores);

                if (resultado != -1) {
                    tvResultadoUsuario.setTextColor(Color.parseColor("#008800"));
                    tvResultadoUsuario.setText("Usuario registrado correctamente.");

                    etUsrUsuario.setText("");
                    etUsrPassword.setText("");
                }

            } catch (SQLiteConstraintException e) {
                tvResultadoUsuario.setTextColor(Color.RED);
                tvResultadoUsuario.setText("Error: Ese nombre de usuario ya existe.");
            } catch (SQLiteException e) {
                tvResultadoUsuario.setTextColor(Color.RED);
                tvResultadoUsuario.setText("Error de base de datos: " + e.getMessage());
            } finally {
                if (db != null && db.isOpen()) db.close();
            }
        });
    }
}