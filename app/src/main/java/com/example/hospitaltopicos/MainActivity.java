package com.example.hospitaltopicos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        EditText etUsuario = findViewById(R.id.etUsuario);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvError = findViewById(R.id.tvError);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = etUsuario.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // 1. Validación de campos vacíos
                if (usuario.isEmpty() || password.isEmpty()) {
                    tvError.setText("Error: Por favor ingresa usuario y contraseña.");
                    return;
                }

                SQLiteDatabase db = null;
                Cursor cursor = null;

                // 2. Manejo de Excepciones para Base de Datos y Valores Nulos
                try {
                    db = dbHelper.getReadableDatabase();
                    cursor = db.rawQuery(
                            "SELECT * FROM usuarios WHERE usuario = ? AND password = ?",
                            new String[]{usuario, password});

                    if (cursor != null && cursor.moveToFirst()) {
                        startActivity(new Intent(MainActivity.this, MenuActivity.class));
                        finish();
                    } else {
                        tvError.setText("Usuario o contraseña incorrectos.");
                    }

                } catch (SQLiteException e) {
                    // Excepción si la base de datos falla al abrirse o consultarse
                    tvError.setText("Error en la Base de Datos: " + e.getMessage());
                } catch (NullPointerException e) {
                    // Excepción en caso de que alguna referencia a la interfaz o BD sea nula
                    tvError.setText("Error de referencia nula al iniciar sesión.");
                } catch (Exception e) {
                    // Captura general para cualquier otro fallo
                    tvError.setText("Error inesperado: " + e.getMessage());
                } finally {
                    // 3. Garantizar el cierre de recursos
                    if (cursor != null) {
                        cursor.close();
                    }
                    if (db != null && db.isOpen()) {
                        db.close();
                    }
                }
            }
        });
    }
}