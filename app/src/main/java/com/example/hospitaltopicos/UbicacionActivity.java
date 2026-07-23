package com.example.hospitaltopicos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class UbicacionActivity extends AppCompatActivity {

    private static final int PERMISO_UBICACION = 200;
    private TextView tvUbicacion;
    private LocationManager locationManager;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            tvUbicacion.setText("Latitud (x): " + location.getLatitude() +
                    "\nLongitud (y): " + location.getLongitude());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        tvUbicacion = findViewById(R.id.tvUbicacion);

        // 1. Manejo de Excepción: Fallo al obtener el servicio del sistema
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        } catch (Exception e) {
            tvUbicacion.setText("Error al inicializar el servicio de ubicación: " + e.getMessage());
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISO_UBICACION);
        } else {
            iniciarUbicacion();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_UBICACION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            iniciarUbicacion();
        } else {
            tvUbicacion.setText("Error de permisos: Se necesita permiso de ubicación.");
        }
    }

    private void iniciarUbicacion() {
        // 2. Manejo de Excepción: SecurityException en lecturas de GPS sin permisos
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 2000, 1, locationListener);

                Location ultima = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (ultima != null) {
                    tvUbicacion.setText("Latitud (x): " + ultima.getLatitude() +
                            "\nLongitud (y): " + ultima.getLongitude());
                } else {
                    tvUbicacion.setText("Obteniendo ubicación en tiempo real...");
                }
            }
        } catch (SecurityException e) {
            tvUbicacion.setText("Excepción de Seguridad: Permisos de ubicación denegados.");
        } catch (Exception e) {
            tvUbicacion.setText("Error al solicitar actualizaciones de ubicación: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 3. Manejo de Excepción al remover listeners
        try {
            if (locationManager != null) {
                locationManager.removeUpdates(locationListener);
            }
        } catch (SecurityException e) {
            // Ignorar o registrar error al cerrar
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}