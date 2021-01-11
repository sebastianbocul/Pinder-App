package com.pinder.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.pinder.app.util.Constants;
import com.pinder.app.util.PaintText;

public class RequestLocationPermissionActivity extends AppCompatActivity {
    Button requestPermission;
    TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_location_permission);
        setObjectsById();
        setButtons();
        PaintText.paintLogo(locationTextView, "Location");
    }

    private void setObjectsById() {
        requestPermission = findViewById(R.id.button_request_permission);
        locationTextView = findViewById(R.id.text_view_location);
    }

    private void setButtons() {
        requestPermission.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(RequestLocationPermissionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(RequestLocationPermissionActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            ActivityCompat.requestPermissions(RequestLocationPermissionActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.requestLocationPermission);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            switch (requestCode) {
                case Constants.requestLocationPermission:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(RequestLocationPermissionActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RequestLocationPermissionActivity.this, ":(", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Opps something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}