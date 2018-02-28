package com.seven.zion.StudentAttendance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    public TextView sig;
    private String LocPerm[] = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sig = (TextView)findViewById(R.id.signLev);
      //  WifiManager manager =  (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,LocPerm[0])!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,LocPerm[1])!= PackageManager.PERMISSION_GRANTED)
        {
            RequestPermissions();
        }
        else
            startActivity(new Intent(this,MapsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        LocationManager manager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                sig.setText("Lat: " + String.format(Locale.getDefault(),"%f",location.getLatitude()) + "Long: " +
                String.format(Locale.getDefault(),"%f",location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
    }
    public void RequestPermissions()
    {
        ActivityCompat.requestPermissions(this,LocPerm,REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE)
        {
            if (grantResults.length >0)
            {
                for (int results : grantResults){
                    if (results != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "App Needs Location Permission", Toast.LENGTH_LONG).show();
                        LoginActivity.this.finish();
                    }
                }
            }
        }
    }
}
