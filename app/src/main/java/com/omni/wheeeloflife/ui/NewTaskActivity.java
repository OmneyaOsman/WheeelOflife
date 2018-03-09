package com.omni.wheeeloflife.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.omni.wheeeloflife.R;

public class NewTaskActivity extends AppCompatActivity {

    private NewTaskFragment newTaskFragment ;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("newTask" ,newTaskFragment.getTag());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        if(savedInstanceState==null) {
            newTaskFragment = NewTaskFragment.newInstance(getIntent().getStringExtra("category")
                    ,getIntent().getIntExtra("color" , R.color.seashell) , getIntent().getBooleanExtra("isEditable" , false));

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.new_task_activity_container,
                            newTaskFragment, "newTask")
                    .commit();
        }else{
            newTaskFragment = (NewTaskFragment) getSupportFragmentManager()
                    .findFragmentByTag(savedInstanceState.getString("newTask"));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
