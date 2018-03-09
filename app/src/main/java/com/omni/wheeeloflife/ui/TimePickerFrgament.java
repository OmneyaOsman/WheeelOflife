package com.omni.wheeeloflife.ui;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.omni.wheeeloflife.utils.AppConfig;

import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class TimePickerFrgament extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


    private  Date chosenTime ;

    public static TimePickerFrgament newInstance(Date time) {

        Bundle args = new Bundle();
        args.putSerializable(AppConfig.TIME_KEY ,time);
        TimePickerFrgament fragment = new TimePickerFrgament();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(getArguments()!=null){
            if(getArguments().containsKey(AppConfig.TIME_KEY)){
                chosenTime = (Date) getArguments().getSerializable(AppConfig.TIME_KEY);
            }
        }
        final Calendar c = Calendar.getInstance();
        if(chosenTime!=null )
            c.setTime(chosenTime);

        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity()
                , this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        String aTime = String.valueOf(hourOfDay).concat(":").concat(String.valueOf(minute));

        Intent data = new Intent();
        data.putExtra("dueTime" ,aTime);
        getTargetFragment().onActivityResult(AppConfig.Time_REQUEST_CODE ,RESULT_OK ,data);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        onCreate(outState);
    }

}
