package com.seven.zion.StudentAttendance;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
                                                     ,GoogleApiClient.ConnectionCallbacks
                                                     ,GoogleApiClient.OnConnectionFailedListener
                                                     ,ResultCallback<Status>
                                                     ,GoogleMap.OnCircleClickListener
                                                     ,GoogleMap.OnMapLongClickListener{

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private PendingIntent geofenceIntent;
    private final int GEOFENCE_REQ_CODE = 101;
    private TextView Lattitude,Longtitude;
    private List<Geofence> geofenceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Lattitude = (TextView)findViewById(R.id.Lattitude);
        Longtitude = (TextView)findViewById(R.id.Longtitiude);
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
               // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                String accuracy = String.format(Locale.getDefault(),"%f",location.getAccuracy());
                //Toast.makeText(getApplicationContext(),accuracy,Toast.LENGTH_SHORT).show();
                String Lat = String.format(Locale.getDefault(),"%f",latLng.latitude);
                String Long = String.format(Locale.getDefault(),"%f",latLng.longitude);
                Lattitude.setText("Lattitude: "+ Lat);
                Longtitude.setText("Longtitude: "+Long);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnCircleClickListener(this);
        mMap.setOnMapLongClickListener(this);
        GoogleMapOptions mapOptions = new GoogleMapOptions();
        mapOptions.tiltGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    private Geofence CreateGeoFence(LatLng latLng, float Radius,int color, String id) {

         Marker geofenceMarker;
         Circle geofenceCircle;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Test Marker");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        markerOptions.snippet("Test Marker : MyHouse");
        geofenceMarker = mMap.addMarker(markerOptions);

        CircleOptions circleOptions = new CircleOptions().center(latLng)
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor( Color.argb(100, 150,150,color))
                .radius(Radius);
        geofenceCircle = mMap.addCircle(circleOptions);
        geofenceCircle.setClickable(true);
        Geofence newGeofence = new Geofence.Builder().setRequestId("Test Demo " + id)
                .setCircularRegion(latLng.latitude, latLng.longitude, Radius).setExpirationDuration(5 * 60 * 1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT).build();
        geofenceCircle.setTag(newGeofence);
        return newGeofence;
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

    private void addGeofence(GeofencingRequest request) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.GeofencingApi.addGeofences(googleApiClient, request, createGeofenceIntent()).setResultCallback(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        LatLng latLng = new LatLng(9.962709, 78.107144);
        Geofence myGeofence = CreateGeoFence(latLng, 70,150," 1");
        Geofence geofence = CreateGeoFence(latLng,30,100,"2");
        geofenceList.add(myGeofence);
        geofenceList.add(geofence);
        GeofencingRequest request = GeoFenceRequest();
        addGeofence(request);
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
    protected void onDestroy() {
        super.onDestroy();
        LocationServices.GeofencingApi.removeGeofences(googleApiClient,geofenceIntent);
    }

    @Override
    public void onCircleClick(Circle circle) {
        Geofence h = (Geofence)circle.getTag();
        Toast.makeText(this,"hi"+circle.getId()+h.getRequestId(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("ADD GEO-FENCE");
        if (fragment !=null)
            fragmentManager.beginTransaction().remove(fragment).commit();
        AddGeofenceDialog geofenceDialog = new AddGeofenceDialog();
        geofenceDialog.show(fragmentManager,"ADD GEO-FENCE");

    }
}
