package com.example.mukodjvegre_location;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class dialogSetRadius<dialogSetRadiusListener> extends AppCompatDialogFragment {

    private int radius;
    boolean meters; //meter=true, kilometer=false;
    private void checkButton(final View view) //meter vagy kilometer?
    {
        final RadioGroup radiusRadioGroup = (RadioGroup) view.findViewById(R.id.radiusRadioGroup); //a radiogombok segitsegevel kivalaszthato, hogy meterben/kilometerben akarjuk megadni a sugarat
        //TO DO: add listener
        radiusRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton radiusRadioBtnChecked = view.findViewById(checkedId);

                if(radiusRadioBtnChecked == view.findViewById(R.id.radioBtnMeters)) meters = true;
                else meters = false;
                listener.applyValueRadius(radius, meters, false);
            }
        });
    }

    private void seekBarCheck(final View view){
        SeekBar radiusSeekBar = (SeekBar) view.findViewById(R.id.radiusSeekBar); //a seekbar segitsegevel kivalaszthato a sugar nagysaga
        final TextView radiusTextView = (TextView) view.findViewById(R.id.radiusTextView); //a kivalasztott sugar megjelenitesere

        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiusTextView.setText("Radius = " + progress );
                radius = progress;
                //checkButton(view);
                listener.applyValueRadius(radius, meters, false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    void setPreviousValues(int radiusSize, boolean isMeters, View view){
        SeekBar radiusSeekBar = (SeekBar) view.findViewById(R.id.radiusSeekBar);
        radiusSeekBar.setProgress(radiusSize);

        RadioGroup radiusRadioGroup = (RadioGroup) view.findViewById(R.id.radiusRadioGroup);
        RadioButton radiusBtnMeters = (RadioButton) view.findViewById(R.id.radioBtnMeters);
        RadioButton radiusBtnKilometers = (RadioButton) view.findViewById(R.id.radioBtnKilometers);
        if(isMeters) radiusBtnMeters.setChecked(true);
        else radiusBtnKilometers.setChecked(true);

        final TextView radiusTextView = (TextView) view.findViewById(R.id.radiusTextView);
        radiusTextView.setText("Radius = " + radiusSize);



    }




    private dialogSetRadius.dialogSetRadiusListener listener;

    //ebben a dialogboxban be lehet allitani a kereses korenek a sugarat
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_dilaog_setradius, null);

        builder.setView(view);

        meters = getArguments().getBoolean("isMeters");
        radius = getArguments().getInt("radius");
        setPreviousValues(radius, meters, view);

        seekBarCheck(view);
        checkButton(view);


        builder.setTitle("Set the radius of your destination!").setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //checkButton(view); //megnezem, hogy meterben vagy kilometerben adta meg a felhasznalo a sugarat
                listener.applyValueRadius(radius, meters, true);
            }
        });

        return builder.create();
        //public void onClick(DialogInterface dialogInterface, int i)
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (dialogSetRadius.dialogSetRadiusListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement dialogSetRadiusListener");
        }

    }

    public interface dialogSetRadiusListener{
        void applyValueRadius(int setRadius, boolean meters, boolean last);
    }
}

