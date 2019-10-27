package com.example.mukodjvegre_location;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class targetPointInFile implements java.io.Serializable {
    String targetName;
    double myLatitude, myLongtitude;
    int circleRadius; //in meter
    String sendEmailToAddress;
    boolean sendEmail;
    int volume;
    boolean saved;
    boolean clicked;
    boolean arrived;

    targetPointInFile(){
        targetName = "";
        myLatitude =0;
        myLongtitude = 0;
        circleRadius =0;
        sendEmail =false;
        sendEmailToAddress = "";
        volume = 15;
        saved = true;
        clicked = false;
        arrived = false;
    }
}
