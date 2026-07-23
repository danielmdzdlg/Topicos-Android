package com.example.hospitaltopicos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class WifiActivity extends AppCompatActivity {

    private static final int PERMISO_UBICACION = 100;
    private TextView tvWifiInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        tvWifiInfo = findViewById(R.id.tvWifiInfo);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISO_UBICACION);
        } else {
            mostrarInfoWifi();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_UBICACION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mostrarInfoWifi();
        } else {
            tvWifiInfo.setText("Error de permisos: Se necesita permiso de ubicación para ver datos del WiFi.");
        }
    }

    private void mostrarInfoWifi() {
        // Manejo de Excepción: SecurityException y NullPointerException al leer datos de red
        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext()
                    .getSystemService(WIFI_SERVICE);

            if (wifiManager == null) {
                tvWifiInfo.setText("Error: Servicio de WiFi no disponible en este dispositivo.");
                return;
            }

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            if (wifiInfo != null && wifiInfo.getNetworkId() != -1) {
                String info = "SSID: " + wifiInfo.getSSID() + "\n" +
                        "BSSID: " + wifiInfo.getBSSID() + "\n" +
                        "Velocidad de enlace: " + wifiInfo.getLinkSpeed() + " Mbps\n" +
                        "Fuerza de señal (RSSI): " + wifiInfo.getRssi() + " dBm\n" +
                        "Dirección IP: " + Integer.toHexString(wifiInfo.getIpAddress());
                tvWifiInfo.setText(info);
            } else {
                tvWifiInfo.setText("No hay conexión WiFi activa.");
            }

        } catch (SecurityException e) {
            tvWifiInfo.setText("Excepción de Seguridad: Permisos denegados al acceder a la red WiFi.");
        } catch (NullPointerException e) {
            tvWifiInfo.setText("Error de Referencia Nula: No se pudieron leer los datos del adapter WiFi.");
        } catch (Exception e) {
            tvWifiInfo.setText("Error inesperado al obtener información de WiFi: " + e.getMessage());
        }
    }
}