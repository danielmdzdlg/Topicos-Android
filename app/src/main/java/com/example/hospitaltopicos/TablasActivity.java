package com.example.hospitaltopicos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TablasActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvContenidoTabla;
    private TextView tvPacientesDoctor;
    private View layoutTablas;
    private View layoutPacientes;
    private View layoutGestor;

    private final String[] tablas = {"medicos", "pacientes", "consultas", "usuarios", "internamientos"};

    // Nombre de la columna llave primaria de cada tabla
    private final Map<String, String> columnasPK = new HashMap<String, String>() {{
        put("medicos", "id_medico");
        put("pacientes", "id_paciente");
        put("consultas", "id_consulta");
        put("usuarios", "id_usuario");
        put("internamientos", "id_internamiento");
    }};

    private LinearLayout contenedorCamposGestor;
    private Button btnGestorActualizar;
    private Button btnGestorEliminar;
    private TextView tvResultadoGestor;
    private Spinner spinnerGestorTabla;
    private EditText etGestorId;

    // Guarda los EditText dinámicos junto con su columna
    private final List<EditText> camposGenerados = new ArrayList<>();
    private final List<String> columnasGeneradas = new ArrayList<>();
    private String tablaActualGestor;
    private String valorPkActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablas);

        dbHelper = new DatabaseHelper(this);
        tvContenidoTabla = findViewById(R.id.tvContenidoTabla);
        tvPacientesDoctor = findViewById(R.id.tvPacientesDoctor);
        layoutTablas = findViewById(R.id.layoutTablas);
        layoutPacientes = findViewById(R.id.layoutPacientes);
        layoutGestor = findViewById(R.id.layoutGestor);

        Spinner spinnerTablas = findViewById(R.id.spinnerTablas);
        Button btnTabTablas = findViewById(R.id.btnTabTablas);
        Button btnTabPacientes = findViewById(R.id.btnTabPacientes);
        Button btnTabGestor = findViewById(R.id.btnTabGestor);

        spinnerGestorTabla = findViewById(R.id.spinnerGestorTabla);
        etGestorId = findViewById(R.id.etGestorId);
        Button btnGestorBuscar = findViewById(R.id.btnGestorBuscar);
        contenedorCamposGestor = findViewById(R.id.contenedorCamposGestor);
        btnGestorActualizar = findViewById(R.id.btnGestorActualizar);
        btnGestorEliminar = findViewById(R.id.btnGestorEliminar);
        tvResultadoGestor = findViewById(R.id.tvResultadoGestor);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, tablas);
        spinnerTablas.setAdapter(adapter);
        spinnerGestorTabla.setAdapter(adapter);

        spinnerTablas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mostrarContenidoTabla(tablas[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnTabTablas.setOnClickListener(v -> {
            layoutTablas.setVisibility(View.VISIBLE);
            layoutPacientes.setVisibility(View.GONE);
            layoutGestor.setVisibility(View.GONE);
        });

        btnTabPacientes.setOnClickListener(v -> {
            layoutTablas.setVisibility(View.GONE);
            layoutPacientes.setVisibility(View.VISIBLE);
            layoutGestor.setVisibility(View.GONE);
            mostrarPacientesConDoctor();
        });

        btnTabGestor.setOnClickListener(v -> {
            layoutTablas.setVisibility(View.GONE);
            layoutPacientes.setVisibility(View.GONE);
            layoutGestor.setVisibility(View.VISIBLE);
        });

        btnGestorBuscar.setOnClickListener(v -> buscarRegistro());
        btnGestorActualizar.setOnClickListener(v -> actualizarRegistro());
        btnGestorEliminar.setOnClickListener(v -> eliminarRegistro());
    }

    private void mostrarContenidoTabla(String nombreTabla) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        StringBuilder sb = new StringBuilder();

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + nombreTabla, null);

            if (cursor.moveToFirst()) {
                do {
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        sb.append(cursor.getColumnName(i)).append(": ")
                                .append(cursor.getString(i)).append("\n");
                    }
                    sb.append("--------------------\n");
                } while (cursor.moveToNext());
            } else {
                sb.append("(Sin registros)");
            }
        } catch (SQLiteException e) {
            sb.append("Error al consultar la tabla: ").append(e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }

        tvContenidoTabla.setText(sb.toString());
    }

    private void mostrarPacientesConDoctor() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        StringBuilder sb = new StringBuilder();

        try {
            db = dbHelper.getReadableDatabase();

            String query = "SELECT p.nombre AS nombre_paciente, p.apellido_paterno, " +
                    "m.nombre AS nombre_medico, m.apellido_paterno AS apellido_medico, c.diagnostico " +
                    "FROM consultas c " +
                    "JOIN pacientes p ON c.id_paciente = p.id_paciente " +
                    "JOIN medicos m ON c.id_medico = m.id_medico";

            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    sb.append("Paciente: ").append(cursor.getString(0)).append(" ")
                            .append(cursor.getString(1)).append("\n");
                    sb.append("Atendido por: Dr. ").append(cursor.getString(2)).append(" ")
                            .append(cursor.getString(3)).append("\n");
                    sb.append("Diagnóstico: ").append(cursor.getString(4)).append("\n");
                    sb.append("--------------------\n");
                } while (cursor.moveToNext());
            } else {
                sb.append("(No hay consultas registradas)");
            }
        } catch (SQLiteException e) {
            sb.append("Error en la consulta JOIN: ").append(e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }

        tvPacientesDoctor.setText(sb.toString());
    }

    // ===================== GESTOR (buscar / actualizar / eliminar) =====================

    private void buscarRegistro() {
        if (spinnerGestorTabla.getSelectedItem() == null) return;
        String tabla = spinnerGestorTabla.getSelectedItem().toString();
        String idBuscado = etGestorId.getText().toString().trim();

        if (idBuscado.isEmpty()) {
            tvResultadoGestor.setTextColor(Color.RED);
            tvResultadoGestor.setText("Escribe el ID del registro a buscar.");
            return;
        }

        String columnaPk = columnasPK.get(tabla);
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + tabla + " WHERE " + columnaPk + " = ?",
                    new String[]{idBuscado});

            contenedorCamposGestor.removeAllViews();
            camposGenerados.clear();
            columnasGeneradas.clear();

            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String nombreColumna = cursor.getColumnName(i);
                    String valor = cursor.getString(i);

                    EditText campo = new EditText(this);
                    campo.setHint(nombreColumna);
                    campo.setText(valor);
                    campo.setPadding(28, 28, 28, 28);
                    campo.setBackgroundResource(R.drawable.bg_input);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.bottomMargin = 16;
                    campo.setLayoutParams(params);

                    // La llave primaria no se puede editar
                    if (nombreColumna.equals(columnaPk)) {
                        campo.setEnabled(false);
                    }

                    contenedorCamposGestor.addView(campo);
                    camposGenerados.add(campo);
                    columnasGeneradas.add(nombreColumna);
                }

                tablaActualGestor = tabla;
                valorPkActual = idBuscado;
                btnGestorActualizar.setVisibility(View.VISIBLE);
                btnGestorEliminar.setVisibility(View.VISIBLE);
                tvResultadoGestor.setTextColor(Color.parseColor("#008800"));
                tvResultadoGestor.setText("Registro encontrado.");
            } else {
                btnGestorActualizar.setVisibility(View.GONE);
                btnGestorEliminar.setVisibility(View.GONE);
                tvResultadoGestor.setTextColor(Color.RED);
                tvResultadoGestor.setText("No existe ningún registro con ese ID.");
            }

        } catch (SQLiteException e) {
            tvResultadoGestor.setTextColor(Color.RED);
            tvResultadoGestor.setText("Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            tvResultadoGestor.setTextColor(Color.RED);
            tvResultadoGestor.setText("Error inesperado: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
    }

    private void actualizarRegistro() {
        if (tablaActualGestor == null) return;

        String columnaPk = columnasPK.get(tablaActualGestor);
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();

            ContentValues valores = new ContentValues();
            for (int i = 0; i < columnasGeneradas.size(); i++) {
                String columna = columnasGeneradas.get(i);
                if (!columna.equals(columnaPk)) {
                    valores.put(columna, camposGenerados.get(i).getText().toString().trim());
                }
            }

            int filas = db.update(tablaActualGestor, valores, columnaPk + " = ?", new String[]{valorPkActual});

            if (filas > 0) {
                tvResultadoGestor.setTextColor(Color.parseColor("#008800"));
                tvResultadoGestor.setText("Registro actualizado correctamente.");
            } else {
                tvResultadoGestor.setTextColor(Color.RED);
                tvResultadoGestor.setText("No se pudo actualizar el registro.");
            }

        } catch (SQLiteException e) {
            tvResultadoGestor.setTextColor(Color.RED);
            tvResultadoGestor.setText("Error de base de datos: " + e.getMessage());
        } catch (NullPointerException e) {
            tvResultadoGestor.setTextColor(Color.RED);
            tvResultadoGestor.setText("Error: Referencia nula al leer campos del formulario.");
        } finally {
            if (db != null && db.isOpen()) db.close();
        }
    }

    private void eliminarRegistro() {
        if (tablaActualGestor == null) return;

        String columnaPk = columnasPK.get(tablaActualGestor);
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            int filas = db.delete(tablaActualGestor, columnaPk + " = ?", new String[]{valorPkActual});

            if (filas > 0) {
                tvResultadoGestor.setTextColor(Color.parseColor("#008800"));
                tvResultadoGestor.setText("Registro eliminado correctamente.");
                contenedorCamposGestor.removeAllViews();
                btnGestorActualizar.setVisibility(View.GONE);
                btnGestorEliminar.setVisibility(View.GONE);
                etGestorId.setText("");
                tablaActualGestor = null;
            } else {
                tvResultadoGestor.setTextColor(Color.RED);
                tvResultadoGestor.setText("No se pudo eliminar el registro.");
            }

        } catch (SQLiteException e) {
            tvResultadoGestor.setTextColor(Color.RED);
            tvResultadoGestor.setText("Error de base de datos: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) db.close();
        }
    }
}