package com.example.hospitaltopicos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button btnWifi = findViewById(R.id.btnWifi);
        Button btnUbicacion = findViewById(R.id.btnUbicacion);
        Button btnPaciente = findViewById(R.id.btnPaciente);
        Button btnTablas = findViewById(R.id.btnTablas);

        btnWifi.setOnClickListener(v ->
                startActivity(new Intent(MenuActivity.this, WifiActivity.class)));

        btnUbicacion.setOnClickListener(v ->
                startActivity(new Intent(MenuActivity.this, UbicacionActivity.class)));

        btnPaciente.setOnClickListener(v ->
                startActivity(new Intent(MenuActivity.this, PacienteActivity.class)));

        btnTablas.setOnClickListener(v ->
                startActivity(new Intent(MenuActivity.this, TablasActivity.class)));
    }
}