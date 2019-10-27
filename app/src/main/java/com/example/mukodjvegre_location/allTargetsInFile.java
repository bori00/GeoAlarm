package com.example.mukodjvegre_location;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class allTargetsInFile implements java.io.Serializable {

    targetPointInFile targets[] = new targetPointInFile[51];
    int targetsNr;

    // Constant with a file name
    public static String fileName = "allTargetsFile.txt";

    allTargetsInFile()
    {
        targetsNr = 0;
        for(int i=0; i<=50; i++)
        {
            targets[i] = new targetPointInFile();
        }
    }


    // Serializes an object and saves it to a file
    public void saveToFile(Context context) {
        try{
            File path = context.getFilesDir();
            File file = new File(path, fileName);
            //Log.e("allTargetsInFile", "writing to file 1");
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
            fileOutputStream.close();
            //Log.e("allTargetsInFile", "writing to file 2");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Creates an object by reading it from a file
    public static allTargetsInFile readFromFile(Context context) {
        allTargetsInFile targetsInFile = null;
        try {
            //Log.e("allTargetsInFile", "reading from file 1");
            File path = context.getFilesDir();
            File file = new File(path, fileName);
            FileInputStream fileInputStream = context.openFileInput(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            targetsInFile = (allTargetsInFile) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            //Log.e("allTargetsInFile", "reading from  file 2");
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(targetsInFile == null) targetsInFile = new allTargetsInFile();
        return targetsInFile;
    }
}
