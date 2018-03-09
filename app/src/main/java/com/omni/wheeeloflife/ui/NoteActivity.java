package com.omni.wheeeloflife.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.omni.wheeeloflife.R;
import com.omni.wheeeloflife.model.WheelTask;

public class NoteActivity extends AppCompatActivity {


    private OneTaskDetailsFragment detailsFragment;
    private NoteFragment noteFragment;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(detailsFragment!=null) {
            outState.putString("detailFragment", detailsFragment.getTag());
        }else{
            outState.putString("noteFragment", noteFragment.getTag());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Intent intent = getIntent();
        if (savedInstanceState == null) {
            if (intent.hasExtra("taskDetails")) {
                WheelTask task = intent.getParcelableExtra("task");
                String cat = intent.getStringExtra("category");
                detailsFragment = OneTaskDetailsFragment.newInstance(task, cat);

                getSupportFragmentManager().beginTransaction().replace(R.id.note_container, detailsFragment, "details").commit();
            } else {
                noteFragment = new NoteFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.note_container, noteFragment, "note").commit();
            }
        } else {
            if (savedInstanceState.containsKey("detailFragment"))
                detailsFragment = (OneTaskDetailsFragment) getSupportFragmentManager().findFragmentByTag(
                        savedInstanceState.getString("detailFragment"));

            else if (savedInstanceState.containsKey("noteFragment"))
                noteFragment = (NoteFragment) getSupportFragmentManager()
                        .findFragmentByTag(savedInstanceState.getString("noteFragment"));

        }
    }


}
