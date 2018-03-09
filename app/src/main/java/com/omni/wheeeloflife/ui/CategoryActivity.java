package com.omni.wheeeloflife.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.omni.wheeeloflife.R;

public class CategoryActivity extends AppCompatActivity {

    private OneCategoryFragment categoryFragment;

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (categoryFragment != null)
            outState.putString("currentFragment", "category");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Intent intent = getIntent();
        String category = intent.getStringExtra("category");


        if (savedInstanceState == null) {
            categoryFragment = OneCategoryFragment.newInstance(category);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.category_activity_container,
                            categoryFragment, "category")
                    .commit();


        } else {
            if (savedInstanceState.containsKey("currentFragment"))
                categoryFragment = (OneCategoryFragment) getSupportFragmentManager().findFragmentByTag(savedInstanceState.getString("currentFragment"));


        }


    }
}
