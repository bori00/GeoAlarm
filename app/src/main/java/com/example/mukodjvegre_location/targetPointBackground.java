package com.example.mukodjvegre_location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import static android.location.Location.distanceBetween;

public class targetPointBackground {
    String targetName;
    LatLng targetCoordinates;
    int circleRadius; //in meter
    int prevCircleRadius = -1;
    String sendEmailToAddress;
    boolean sendEmail;
    boolean statusChanged;
    int volume;
    boolean saved;
    boolean clicked;
    boolean arrived;

    targetPointBackground(String name, double tLatitude, double tLongtitude) {
        circleRadius = 100;
        targetName = name;
        targetCoordinates = new LatLng(tLatitude, tLongtitude);
        circleRadius = 500;
        sendEmail = false;
        sendEmailToAddress = "";
        statusChanged = true;
        //TODO consider adding a default value, that depends on the device settings
        volume = 7;
        saved = true;
        clicked = false;
        arrived = false;
    }

    public void sync_target_readFromFile(targetPointInFile targetInFile)
    {
        circleRadius = targetInFile.circleRadius;
        targetName = targetInFile.targetName;
        targetCoordinates = new LatLng(targetInFile.myLatitude, targetInFile.myLongtitude);
        sendEmail = targetInFile.sendEmail;
        sendEmailToAddress = targetInFile.sendEmailToAddress;
        statusChanged = true;
        volume = targetInFile.volume;
        saved = targetInFile.saved;
        clicked = targetInFile.clicked;
        arrived = targetInFile.arrived;
    }

    public boolean arrived(double startLat, double startLong){
        float result[] = new float[1];
        distanceBetween(startLat, startLong, targetCoordinates.latitude, targetCoordinates.longitude, result);

        // Log.d(TAG, "distance calculated " + result[0]);
        if(result[0]<=circleRadius) return true;
        return false;
    }

    public targetPointInFile getTargetToFile()
    {
        targetPointInFile targetToFile = new targetPointInFile();
        targetToFile.targetName = targetName;
        targetToFile.circleRadius = circleRadius;
        targetToFile.myLatitude = targetCoordinates.latitude;
        targetToFile.myLongtitude = targetCoordinates.longitude;
        targetToFile.sendEmail =sendEmail;
        targetToFile.sendEmailToAddress = sendEmailToAddress;
        targetToFile.volume = volume;
        targetToFile.saved = saved;
        targetToFile.arrived = arrived;
        targetToFile.clicked = clicked;
        return targetToFile;
    }
}
