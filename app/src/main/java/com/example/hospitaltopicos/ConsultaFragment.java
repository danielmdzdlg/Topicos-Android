package com.example.hospitaltopicos;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ConsultaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_consulta, container, false);

        EditText etIdPaciente = vista.findViewById(R.id.etIdPacienteConsulta);
        Spinner spinnerMedico = vista.findViewById(R.id.spinnerMedico);
        EditText etDiagnostico = vista.findViewById(R.id.etDiagnostico);
        EditText etHoraSalida = vista.findViewById(R.id.etHoraSalida);
        Button btnGuardar = vista.findViewById(R.id.btnGuardarConsulta);
        TextView tvResultado = vista.findViewById(R.id.tvResultadoConsulta);

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        // --- RELOJ (TIMEPICKER) PARA HORA DE SALIDA ---
        etHoraSalida.setFocusable(false);
        etHoraSalida.setClickable(true);
        etHoraSalida.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int horaActual = calendar.get(Calendar.HOUR_OF_DAY);
            int minutoActual = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (view, hourOfDay, minute) -> {
                        String horaFormateada = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        etHoraSalida.setText(horaFormateada);
                    },
                    horaActual,
                    minutoActual,
                    true // true para formato de 24 horas (00:00 - 23:59)
            );
            timePickerDialog.show();
        });

        // --- Cargar médicos existentes en el spinner ---
        List<Integer> idsMedicos = new ArrayList<>();
        List<String> nombresMedicos = new ArrayList<>();

        nombresMedicos.add("Selecciona un médico");
        idsMedicos.add(-1);

        SQLiteDatabase dbCarga = null;
        Cursor cursorMedicos = null;

        try {
            dbCarga = dbHelper.getReadableDatabase();
            cursorMedicos = dbCarga.rawQuery(
                    "SELECT id_medico, nombre, apellido_paterno FROM medicos", null);

            if (cursorMedicos.moveToFirst()) {
                do {
                    idsMedicos.add(cursorMedicos.getInt(0));
                    nombresMedicos.add(cursorMedicos.getInt(0) + " - " + cursorMedicos.getString(1) + " " + cursorMedicos.getString(2));
                } while (cursorMedicos.moveToNext());
            }
        } catch (SQLiteException e) {
            tvResultado.setTextColor(Color.RED);
            tvResultado.setText("Error al cargar lista de médicos: " + e.getMessage());
        } catch (Exception e) {
            tvResultado.setTextColor(Color.RED);
            tvResultado.setText("Error inesperado al leer médicos: " + e.getMessage());
        } finally {
            if (cursorMedicos != null) cursorMedicos.close();
            if (dbCarga != null && dbCarga.isOpen()) dbCarga.close();
        }

        ArrayAdapter<String> adapterMedicos = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, nombresMedicos);
        spinnerMedico.setAdapter(adapterMedicos);

        // --- Guardar consulta ---
        btnGuardar.setOnClickListener(v -> {
            String idPaciente = etIdPaciente.getText().toString().trim();

            // Validación previa de selección en el Spinner
            int pos = spinnerMedico.getSelectedItemPosition();
            if (pos < 0 || pos >= idsMedicos.size()) {
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Error: Selección de médico no válida.");
                return;
            }

            int idMedico = idsMedicos.get(pos);
            String diagnostico = etDiagnostico.getText().toString().trim();
            String horaSalida = etHoraSalida.getText().toString().trim();

            if (idPaciente.isEmpty() || idMedico == -1) {
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Error: Selecciona un paciente válido y un médico.");
                return;
            }

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
                    tvResultado.setTextColor(Color.parseColor("#008800"));
                    tvResultado.setText("Consulta guardada correctamente.");

                    // Limpiar formulario tras guardado exitoso
                    etIdPaciente.setText("");
                    spinnerMedico.setSelection(0);
                    etDiagnostico.setText("");
                    etHoraSalida.setText("");
                } else {
                    tvResultado.setTextColor(Color.RED);
                    tvResultado.setText("Error: No se pudo insertar el registro en la BD.");
                }

            } catch (SQLiteException e) {
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Error en la Base de Datos: " + e.getLocalizedMessage());
            } catch (Exception e) {
                tvResultado.setTextColor(Color.RED);
                tvResultado.setText("Ocurrió un error inesperado: " + e.getMessage());
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
        });

        return vista;
    }
}