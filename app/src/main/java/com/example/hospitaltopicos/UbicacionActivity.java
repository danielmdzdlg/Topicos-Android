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
            tvUbicacion.setText("Latitud: " + location.getLatitude() +
                    "\nLongitud: " + location.getLongitude());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        tvUbicacion = findViewById(R.id.tvUbicacion);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
            tvUbicacion.setText("Se necesita permiso de ubicación.");
        }
    }

    private void iniciarUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 2000, 1, locationListener);

            Location ultima = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (ultima != null) {
                tvUbicacion.setText("Latitud: " + ultima.getLatitude() +
                        "\nLongitud: " + ultima.getLongitude());
            } else {
                tvUbicacion.setText("Obteniendo ubicación...");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }
}