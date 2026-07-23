package com.example.hospitaltopicos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TablasActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvContenidoTabla;
    private TextView tvPacientesDoctor;
    private View layoutTablas;
    private View layoutPacientes;

    private final String[] tablas = {"medicos", "pacientes", "consultas", "usuarios", "internamientos"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablas);

        dbHelper = new DatabaseHelper(this);
        tvContenidoTabla = findViewById(R.id.tvContenidoTabla);
        tvPacientesDoctor = findViewById(R.id.tvPacientesDoctor);
        layoutTablas = findViewById(R.id.layoutTablas);
        layoutPacientes = findViewById(R.id.layoutPacientes);
        Spinner spinnerTablas = findViewById(R.id.spinnerTablas);
        Button btnTabTablas = findViewById(R.id.btnTabTablas);
        Button btnTabPacientes = findViewById(R.id.btnTabPacientes);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, tablas);
        spinnerTablas.setAdapter(adapter);

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
        });

        btnTabPacientes.setOnClickListener(v -> {
            layoutTablas.setVisibility(View.GONE);
            layoutPacientes.setVisibility(View.VISIBLE);
            mostrarPacientesConDoctor();
        });
    }

    private void mostrarContenidoTabla(String nombreTabla) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + nombreTabla, null);

        StringBuilder sb = new StringBuilder();

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

        cursor.close();
        tvContenidoTabla.setText(sb.toString());
    }

    private void mostrarPacientesConDoctor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT p.nombre AS nombre_paciente, p.apellido_paterno, " +
                "m.nombre AS nombre_medico, m.apellido_paterno AS apellido_medico, c.diagnostico " +
                "FROM consultas c " +
                "JOIN pacientes p ON c.id_paciente = p.id_paciente " +
                "JOIN medicos m ON c.id_medico = m.id_medico";

        Cursor cursor = db.rawQuery(query, null);
        StringBuilder sb = new StringBuilder();

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

        cursor.close();
        tvPacientesDoctor.setText(sb.toString());
    }
}