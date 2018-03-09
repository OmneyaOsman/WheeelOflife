package com.omni.wheeeloflife.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.omni.wheeeloflife.R;
import com.omni.wheeeloflife.utils.AppConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class HomeFragment extends Fragment {


    @BindView(R.id.scroll_view)
    ScrollView mScrollView ;




    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("ARTICLE_SCROLL_POSITION",
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
            if (position != null)
                mScrollView.post(new Runnable() {
                    public void run() {
                        mScrollView.scrollTo(position[0], position[1]);
                    }
                });
        }


    }


    @OnClick(R.id.family_cat_btn)
    void openFamilyScreen(View view){
      openCategoryListActivity(AppConfig.FAMILY_CAT , view);
    }

    @OnClick(R.id.fun_cat_btn)
    void openFunScreen(View view){
       openCategoryListActivity(AppConfig.FUN_CAT,view);
    }

    @OnClick(R.id.finance_cat_btn)
    void openFinanceScreen(View view){
      openCategoryListActivity(AppConfig.FINANCIAL_CAT, view);
    }

    @OnClick(R.id.religion_cat_btn)
    void openReligionScreen(View view){
       openCategoryListActivity(AppConfig.RELIGION_CAT,view);
    }
    @OnClick(R.id.health_cat_btn)
    void openHealthScreen(View view){
      openCategoryListActivity(AppConfig.HEALTH_CAT,view);
    }

    @OnClick(R.id.love_cat_btn)
    void openLoveScreen(View view){
       openCategoryListActivity(AppConfig.LOVE_CAT,view);
    }

    @OnClick(R.id.education_cat_btn)
    void openEducationScreen(View view){
      openCategoryListActivity(AppConfig.EDUCATION_CAT,view);
    }

    @OnClick(R.id.career_cat_btn)
    void openCreerScreen(View view){
       openCategoryListActivity(AppConfig.CAREER_CAT,view);
    }

    @OnClick(R.id.travel_cat_btn)
    void openTravelScreen(View view){
      openCategoryListActivity(AppConfig.TRAVEL_CAT,view);
    }

    @OnClick(R.id.social_life_cat_btn)
    void openSocialLifeScreen(View view) {
        openCategoryListActivity(AppConfig.SOCIAL_LIFE_CAT ,view);
    }

    private void openCategoryListActivity(String category , View view) {
        if(getActivity()!=null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), view, "transition");
            int revealX = (int) (view.getX() + view.getWidth() / 2);
            int revealY = (int) (view.getY() + view.getHeight() / 2);
            int width = mScrollView.getWidth();
            int height = mScrollView.getHeight();

            Intent intent = new Intent(getActivity(), CategoryActivity.class);
            intent.putExtra("category", category);
            intent.putExtra(AppConfig.EXTRA_CIRCULAR_REVEAL_X, revealX);
            intent.putExtra(AppConfig.EXTRA_CIRCULAR_REVEAL_Y, revealY);
            intent.putExtra("w", width);
            intent.putExtra("h", height);
            if (getActivity() != null)
                if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                    ActivityCompat.startActivity(getActivity(), intent, options.toBundle());

        }
    }
}
