package com.seven.zion.StudentAttendance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
                                                     ,AddGeofenceDialog.geofenceDialog
                                                     ,GoogleMap.OnCircleClickListener
                                                     ,GoogleMap.OnMapLongClickListener{

    private GoogleMap mMap;
    private TextView Lattitude,Longtitude;
    FirebaseDatabase database;
    DatabaseReference reference;
    boolean onStartInit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Lattitude = (TextView)findViewById(R.id.Lattitude);
        Longtitude = (TextView)findViewById(R.id.Longtitiude);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Geofences");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (onStartInit) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        geofenceData fences = data.getValue(geofenceData.class);
                        Log.i("Initialize", fences.getRequestId());
                        LatLng latLng = new LatLng(fences.getLattitude(), fences.getLontitude());
                        CreateGeoFence(latLng, fences.getRadius(), 120, fences.getRequestId());
                    }
                   // onStartInit = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng defaultM = new LatLng(9.961742, 78.105299);
        mMap.addMarker(new MarkerOptions().position(defaultM).title("Marker in test"));
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultM));
    }
    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(MapsActivity.this,BackgroundService.class));
    }

    private void CreateGeoFence(LatLng latLng, float Radius,int color, String id) {

         Marker geofenceMarker;
         Circle geofenceCircle;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(id.toUpperCase(Locale.getDefault()));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        markerOptions.snippet("Geofence : "+ id);
        geofenceMarker = mMap.addMarker(markerOptions);

        CircleOptions circleOptions = new CircleOptions().center(latLng)
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor( Color.argb(100, 150,150,color))
                .radius(Radius);
        geofenceCircle = mMap.addCircle(circleOptions);
        geofenceCircle.setClickable(true);
       // reference.push().setValue(newGeofence);
        geofenceCircle.setTag(id);
    }

    @Override
    public void onCircleClick(Circle circle) {
        String h = (String)circle.getTag();
        Toast.makeText(this,"hi"+circle.getId()+h,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("ADD GEO-FENCE");
        if (fragment !=null)
            fragmentManager.beginTransaction().remove(fragment).commit();
        AddGeofenceDialog geofenceDialog = new AddGeofenceDialog();
        Bundle data = new Bundle();
        data.putDouble("Lat",latLng.latitude);
        data.putDouble("Long",latLng.longitude);
        geofenceDialog.setArguments(data);
        geofenceDialog.show(fragmentManager,"ADD GEO-FENCE");


    }

    @Override
    public void addGeofence(LatLng latLng, String id, int radius) {

        Toast.makeText(this,id,Toast.LENGTH_LONG).show();
        geofenceData data = new geofenceData(id,latLng.latitude,latLng.longitude,radius);
        reference.push().setValue(data);
        //CreateGeoFence(latLng,radius,120,id);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("ADD GEO-FENCE");
        if (fragment !=null)
            fragmentManager.beginTransaction().remove(fragment).commit();

    }
}
