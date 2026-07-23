package com.example.hospitaltopicos;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
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
            String idMedico = etIdMedico.getText().toString().trim();
            String diagnostico = etDiagnostico.getText().toString().trim();
            String horaSalida = etHoraSalida.getText().toString().trim();

            if (idPaciente.isEmpty() || idMedico.isEmpty()) {
                tvResultado.setTextColor(0xFFFF0000);
                tvResultado.setText("Faltan datos obligatorios");
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put("id_paciente", idPaciente);
            valores.put("id_medico", Integer.parseInt(idMedico));
            valores.put("diagnostico", diagnostico);
            valores.put("hora_salida", horaSalida);

            long resultado = db.insert("consultas", null, valores);

            if (resultado != -1) {
                tvResultado.setTextColor(0xFF008800);
                tvResultado.setText("Consulta guardada correctamente");
            } else {
                tvResultado.setTextColor(0xFFFF0000);
                tvResultado.setText("Error al guardar la consulta");
            }
        });

        return vista;
    }
}
