package com.example.mukodjvegre_location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class myPos {
    boolean markerExists;
    Marker myPositionMarker;
    private LatLng myCoordinates;
    private LatLng prevCoordinates;

    myPos(){
        //Toast.makeText(MapsActivity.this, "I got your location, NETWORK_PROVIDER: " + myLatitude + ", " + myLongtitude, Toast.LENGTH_SHORT).show();
        markerExists = false;
        prevCoordinates = new LatLng(-1, -1);
        myCoordinates = new LatLng(0, 0);
    }

    void newMyPos(double mLatitude, double mLongtitude, GoogleMap mMap, Context context) {
            prevCoordinates = new LatLng(myCoordinates.latitude, myCoordinates.longitude);
            myCoordinates = new LatLng(mLatitude, mLongtitude);
            showOnMap(mMap, context);
    }


    void showOnMap(GoogleMap mMap, Context context){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,  Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // ActivityCompat.requestPermissions(getActivity(),   new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 500);
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    double getLatitude(){
        return myCoordinates.latitude;
    }

    double getLongtitude(){
        return myCoordinates.longitude;
    }


}
