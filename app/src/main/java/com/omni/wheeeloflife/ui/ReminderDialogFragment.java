package com.omni.wheeeloflife.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.omni.wheeeloflife.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.omni.wheeeloflife.utils.AppConfig.REMINDER_RQUEST_CODE;


public class ReminderDialogFragment extends DialogFragment {

    @BindView(R.id.number_spinner)
    Spinner numberSpinner ;
    @BindView(R.id.unit_spinner)
    Spinner uniteSpinner ;
    private String unite ;
    private int number;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = getActivity().getLayoutInflater();
        View view = inflator.inflate(R.layout.reminder_dialog, null);
        ButterKnife.bind(this, view);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> uniteAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.unite_array, android.R.layout.simple_spinner_item);
        uniteSpinner.setAdapter(uniteAdapter);
        ArrayAdapter<CharSequence> numberAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.numbers_array, android.R.layout.simple_spinner_item);
        numberSpinner.setAdapter(numberAdapter);
        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                number = Integer.parseInt( adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        uniteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                unite = adapterView.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        builder.setView(view);
        Dialog dialog = builder.create();

        return dialog;
    }

    @OnClick(R.id.save_button)
    void saveReminder(){
        Intent data = new Intent();
        data.putExtra("number" , number);
        data.putExtra("unite" , unite);
        getTargetFragment().onActivityResult(REMINDER_RQUEST_CODE , RESULT_OK ,data);
        getDialog().cancel();

    }
    @OnClick(R.id.cancel_button)
    void cancelReminder(){
        getDialog().cancel();
    }
}
