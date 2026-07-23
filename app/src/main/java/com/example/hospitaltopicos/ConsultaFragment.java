package com.example.hospitaltopicos;

import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ConsultaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_consulta, container, false);

        EditText etIdPaciente = vista.findViewById(R.id.etIdPacienteConsulta);
        EditText etIdMedico = vista.findViewById(R.id.etIdMedico);
        EditText etDiagnostico = vista.findViewById(R.id.etDiagnostico);
        EditText etHoraSalida = vista.findViewById(R.id.etHoraSalida);
        Button btnGuardar = vista.findViewById(R.id.btnGuardarConsulta);
        TextView tvResultado = vista.findViewById(R.id.tvResultadoConsulta);

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        btnGuardar.setOnClickListener(v -> {
            String idPaciente = etIdPaciente.getText().toString().trim();
            String idMedicoStr = etIdMedico.getText().toString().trim();
            String diagnostico = etDiagnostico.getText().toString().trim();
            String horaSalida = etHoraSalida.getText().toString().trim();

            // 1. Excepción / Validación de campos obligatorios vacíos
            if (idPaciente.isEmpty() || idMedicoStr.isEmpty()) {
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Error: Los campos ID Paciente e ID Médico son obligatorios.");
                return;
            }

            int idMedico;

            // 2. Manejo de Excepción: NumberFormatException (Si el usuario escribe letras en el ID)
            try {
                idMedico = Integer.parseInt(idMedicoStr);
            } catch (NumberFormatException e) {
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Error: El ID del médico debe ser un número entero.");
                return;
            }

            // 3. Manejo de Excepción: SQLiteException (Apertura e inserción en Base de Datos)
            SQLiteDatabase db = null;
            try {
                db = dbHelper.getWritableDatabase();

                ContentValues valores = new ContentValues();
                valores.put("id_paciente", idPaciente);
                valores.put("id_medico", idMedico);
                valores.put("diagnostico", diagnostico);
                valores.put("hora_salida", horaSalida);

                long resultado = db.insert("consultas", null, valores);

                if (resultado != -1) {
                    tvResultado.setTextColor(Color.parseColor("#008800")); // Verde
                    tvResultado.setText("Consulta guardada correctamente.");

                    // Limpiar formulario tras guardado exitoso
                    etIdPaciente.setText("");
                    etIdMedico.setText("");
                    etDiagnostico.setText("");
                    etHoraSalida.setText("");
                } else {
                    tvResultado.setTextColor(Color.RED);
                    tvResultado.setText("Error: No se pudo insertar el registro en la BD.");
                }

            } catch (SQLiteException e) {
                // Captura errores específicos de base de datos
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Error en la Base de Datos: " + e.getLocalizedMessage());
            } catch (Exception e) {
                // Captura cualquier otro error no contemplado
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Ocurrió un error inesperado: " + e.getMessage());
            } finally {
                // Cerramos la base de datos siempre al terminar para evitar fugas de memoria
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
        });

        return vista;
    }
}