package com.omni.wheeeloflife.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.omni.wheeeloflife.utils.AppConfig;

import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class DateDialogFragment extends DialogFragment implements  DatePickerDialog.OnDateSetListener {


    private  Date currentSavedDate ;

    public static DateDialogFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putSerializable(AppConfig.DATE_KEY ,date);
        DateDialogFragment fragment = new DateDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(getArguments()!=null){
            if(getArguments().containsKey(AppConfig.DATE_KEY)){
               currentSavedDate = (Date) getArguments().getSerializable(AppConfig.DATE_KEY);
            }
        }

        final Calendar calendar = Calendar.getInstance();
        long minMilis =calendar.getTimeInMillis();
        if(currentSavedDate!=null )
            calendar.setTime(currentSavedDate);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog =new DatePickerDialog(getActivity(), this, year, month, day);
        // set minimum is today
        datePickerDialog.getDatePicker().setMinDate(minMilis);

        return datePickerDialog;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        month++;
        String chosenDate =dayOfMonth+"/"+month+"/"+year;
        Intent data = new Intent();
        data.putExtra("dueDate" ,chosenDate);
        getTargetFragment().onActivityResult(AppConfig.DATE_REQUEST_CODE ,RESULT_OK ,data);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        onCreate(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


}
