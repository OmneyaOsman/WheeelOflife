package com.omni.wheeeloflife.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.omni.wheeeloflife.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class NoteFragment extends Fragment {


    @BindView(R.id.note_ed)
    EditText noteEd;
    private String noteString;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("note" , noteString);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                if (intent.hasExtra("notePre"))
                    noteString = intent.getStringExtra("notePre");

            }
        } else {
            noteString = savedInstanceState.getString("note");
        }

        noteEd.setText(noteString);
        noteEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                noteEd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if(!b)
                            noteString = editable.toString();
                    }
                });

            }
        });
    }

    @OnClick(R.id.done_adding_note)
    void addNote(){
         noteString = noteEd.getText().toString();
        Intent data = new Intent();
        data.putExtra("note" , noteString);
        getActivity().setResult(RESULT_OK , data);
        getActivity().finish();
    }


}
