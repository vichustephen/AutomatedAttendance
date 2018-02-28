package com.seven.zion.StudentAttendance;

import android.app.IntentService;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.common.GooglePlayServicesUtil.getErrorString;

public class GeoFenceTransitionService extends IntentService {

    public GeoFenceTransitionService() {
        super("GeoFenceTransitionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if ( geofencingEvent.hasError() ) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode());
            Log.e("TransisitonService", errorMsg);
            return;
        }
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == GeofencingRequest.INITIAL_TRIGGER_ENTER ||
                geofenceTransition == GeofencingRequest.INITIAL_TRIGGER_EXIT)
        {
            List<Geofence> triggerTransitions = geofencingEvent.getTriggeringGeofences();
            final String geofenceDetails = getGeofenceTransitionDetails(geofenceTransition,triggerTransitions);
            //Toast.makeText(this,geofenceDetails,Toast.LENGTH_LONG).show();
            Log.i("Transition occured",geofenceDetails);

            android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(getApplicationContext(),geofenceDetails,Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private String getGeofenceTransitionDetails(int geofenceTransition, List<Geofence> triggerTransitions) {

        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for ( Geofence geofence : triggerTransitions ) {
            triggeringGeofencesList.add( geofence.getRequestId() );
        }

        String status = null;
        if ( geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
            status = "Entering ";
        else if ( geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
            status = "Exiting ";
        return status + TextUtils.join( ", ", triggeringGeofencesList);
    }
}
