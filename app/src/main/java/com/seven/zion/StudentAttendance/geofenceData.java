package com.seven.zion.StudentAttendance;

/**
 * Created by Stephen on 11-Mar-18.
 */

public class geofenceData {

    String requestId;
    Double lattitude,lontitude;
    int radius;

    public geofenceData()
    {

    }
    public geofenceData(String requestId, Double lattitude, Double lontitude, int radius) {
        this.requestId = requestId;
        this.lattitude = lattitude;
        this.lontitude = lontitude;
        this.radius = radius;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Double getLattitude() {
        return lattitude;
    }

    public void setLattitude(Double lattitude) {
        this.lattitude = lattitude;
    }

    public Double getLontitude() {
        return lontitude;
    }

    public void setLontitude(Double lontitude) {
        this.lontitude = lontitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
