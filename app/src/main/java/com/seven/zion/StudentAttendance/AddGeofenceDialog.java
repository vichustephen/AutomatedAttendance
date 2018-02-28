package com.seven.zion.StudentAttendance;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

/**
 * Created by Stephen V on 27-Feb-18.
 */

public class AddGeofenceDialog extends DialogFragment {

    Button okButton;
    EditText latText,longText,radius,name;
    Double lat;
    Double Long;

    public interface geofenceDialog{

        public void addGeofence (LatLng latLng,String id,int radius);
    }
    public AddGeofenceDialog(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_geofence_dialog,container);
        name = (EditText)view.findViewById(R.id.gname);
        okButton = (Button)view.findViewById(R.id.okbutton);
        latText = (EditText)view.findViewById(R.id.lat);
        longText = (EditText)view.findViewById(R.id.lang);
        radius = (EditText)view.findViewById(R.id.radius);
        lat = getArguments().getDouble("Lat");
        Long = getArguments().getDouble("Long");
        longText.setText(String.format(Locale.getDefault(),"%f",Long));
        latText.setText(String.format(Locale.getDefault(),"%f",lat));
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(radius.getText())&&!TextUtils.isEmpty(latText.getText())&&
                        !TextUtils.isEmpty(longText.getText())) {

                    if (Integer.parseInt(radius.getText().toString())<8)
                        Toast.makeText(getActivity(),"Radius must be minimum 8 !",Toast.LENGTH_LONG).show();
                    else {
                        geofenceDialog dialog = (geofenceDialog) getActivity();
                        LatLng latLng = new LatLng(Double.parseDouble(latText.getText().toString()),
                                Double.parseDouble(longText.getText().toString()));
                        dialog.addGeofence(latLng, name.getText().toString(), Integer.parseInt(radius.getText().toString()));
                        dismiss();
                    }
                }
                else
                    Toast.makeText(getActivity(),"Fields cannot be empty !",Toast.LENGTH_LONG).show();
            }
        });
        return view ;
    }
}
