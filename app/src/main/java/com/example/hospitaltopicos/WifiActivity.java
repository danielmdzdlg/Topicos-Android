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
            tvWifiInfo.setText("Se necesita permiso de ubicación para ver los datos del WiFi.");
        }
    }

    private void mostrarInfoWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext()
                .getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo != null) {
            String info = "SSID: " + wifiInfo.getSSID() + "\n" +
                    "BSSID: " + wifiInfo.getBSSID() + "\n" +
                    "Velocidad de enlace: " + wifiInfo.getLinkSpeed() + " Mbps\n" +
                    "Fuerza de señal (RSSI): " + wifiInfo.getRssi() + " dBm\n" +
                    "Dirección IP: " + Integer.toHexString(wifiInfo.getIpAddress());
            tvWifiInfo.setText(info);
        } else {
            tvWifiInfo.setText("No hay conexión WiFi activa.");
        }
    }
}