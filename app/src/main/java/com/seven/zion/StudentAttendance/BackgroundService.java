package com.seven.zion.StudentAttendance;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BackgroundService extends Service implements GoogleApiClient.ConnectionCallbacks
        ,GoogleApiClient.OnConnectionFailedListener
        ,ResultCallback<Status> {

    private List<Geofence> geofenceList = new ArrayList<>();
    DatabaseReference reference;
    private GoogleApiClient googleApiClient;
    private PendingIntent geofenceIntent;
    private final int GEOFENCE_REQ_CODE = 101;
    boolean onStartInit = true;

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return  null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                String accuracy = String.format(Locale.getDefault(),"%f",location.getAccuracy());
                //Toast.makeText(getApplicationContext(),accuracy,Toast.LENGTH_SHORT).show();
                String Lat = String.format(Locale.getDefault(),"%f",latLng.latitude);
                String Long = String.format(Locale.getDefault(),"%f",latLng.longitude);
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
    private GeofencingRequest GeoFenceRequest() {
        return new GeofencingRequest.Builder().setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofenceList)
                .build();
    }
    private PendingIntent createGeofenceIntent() {
        if (geofenceIntent != null)
            return geofenceIntent;
        Intent intent = new Intent(this, GeoFenceTransitionService.class);
        geofenceIntent = PendingIntent.getService(this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofenceIntent;
    }
    private Geofence CreateGeoFence(LatLng latLng, float Radius, int color, String id) {

        return new Geofence.Builder().setRequestId(id)
                .setCircularRegion(latLng.latitude, latLng.longitude, Radius).setExpirationDuration(5 * 60 * 1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT).build();
    }
    private void addGeofence(GeofencingRequest request) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.GeofencingApi.addGeofences(googleApiClient, request, createGeofenceIntent()).setResultCallback(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //LatLng latLng = new LatLng(9.962709, 78.107144);
        // Geofence myGeofence = CreateGeoFence(latLng, 70,150," 1");
       // Geofence geofence = CreateGeoFence(latLng,30,100,"2");
        // geofenceList.add(myGeofence);
        //geofenceList.add(geofence);
        Initialize();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return  START_STICKY;
    }

    private void Initialize() {

        reference = FirebaseDatabase.getInstance().getReference("Geofences");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(BackgroundService.this,"initing",Toast.LENGTH_LONG).show();
                if (onStartInit) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        geofenceData fences = data.getValue(geofenceData.class);
                        Log.i("InitService", fences.getRequestId());
                        LatLng latLng = new LatLng(fences.getLattitude(), fences.getLontitude());
                        Geofence geofence = CreateGeoFence(latLng, fences.getRadius(), 120, fences.requestId);
                        geofenceList.add(geofence);
                    }
                    onStartInit = false;
                    GeofencingRequest request = GeoFenceRequest();
                    addGeofence(request);
                }
                else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Stopped",Toast.LENGTH_SHORT).show();
        LocationServices.GeofencingApi.removeGeofences(googleApiClient,geofenceIntent);
    }
}
