package com.seven.zion.StudentAttendance;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Stephen V on 27-Feb-18.
 */

public class AddGeofenceDialog extends DialogFragment {

    public interface geofenceDialog{

        public void addGeofence (LatLng latLng,String id,int radius);
    }
    public AddGeofenceDialog(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_geofence_dialog,container);

        return view ;
    }
}
