package com.example.mukodjvegre_location;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class dialogSetVolume<dialogSetVolumeListener> extends AppCompatDialogFragment {

    int volume;
    Ringtone r;

    private void seekBarCheck(final View view){
        SeekBar volumeSeekBar = (SeekBar) view.findViewById(R.id.volumeSeekBar); //a seekbar segitsegevel kivalaszthato a sugar nagysaga
        final TextView volumeTextView = (TextView) view.findViewById(R.id.volumeTextView); //a kivalasztott sugar megjelenitesere
        volumeTextView.setText("Volume = " + volume);

        final AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        volumeSeekBar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_ALARM));
        volumeSeekBar.setProgress(volume);

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if(alert == null){
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if(alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        r = RingtoneManager.getRingtone(getContext(), alert);
        r.setStreamType(AudioManager.STREAM_ALARM);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);



        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volumeTextView.setText("Volume = " + progress );
                volume = progress;
                //checkButton(view);
                if(r.isPlaying()) r.stop();
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                        volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                r.play();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
    }

    public void playAlarmSoundForExample(){
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if(alert == null){
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if(alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        final Ringtone r = RingtoneManager.getRingtone(getContext(), alert);


        final AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        r.play();
    }


    void setPreviousValues(int prevVolume, View view){
        SeekBar volumeSeekBar = (SeekBar) view.findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setProgress(prevVolume);
        volume = prevVolume;

        final TextView volumeTextView = (TextView) view.findViewById(R.id.volumeTextView);
        volumeTextView.setText("Volume = " + volume);
    }

    private dialogSetVolume.dialogSetVolumeListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Log.e("dialogSetVolume", "dialogCreate started");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_dialog_setvolume, null);

        builder.setView(view);

        volume = getArguments().getInt("prevVolume");
        setPreviousValues(volume, view);

        seekBarCheck(view);


        builder.setTitle("Set the volume of your alarm!").setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //checkButton(view); //megnezem, hogy meterben vagy kilometerben adta meg a felhasznalo a sugarat
                listener.applyValueVolume(volume);
                r.stop();
            }
        });

        return builder.create();
        //public void onClick(DialogInterface dialogInterface, int i)
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (dialogSetVolume.dialogSetVolumeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement dialogSetRadiusListener");
        }

    }

    public interface dialogSetVolumeListener{
        void applyValueVolume(int volume);
    }
}
