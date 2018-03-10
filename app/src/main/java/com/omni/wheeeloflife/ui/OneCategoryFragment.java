package com.omni.wheeeloflife.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.omni.wheeeloflife.R;
import com.omni.wheeeloflife.model.WheelTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.omni.wheeeloflife.utils.AppConfig.EXTRA_CIRCULAR_REVEAL_X;
import static com.omni.wheeeloflife.utils.AppConfig.EXTRA_CIRCULAR_REVEAL_Y;
import static com.omni.wheeeloflife.utils.AppConfig.toolbarColor;


public class OneCategoryFragment extends Fragment {

    private static final String BUNDLE_RECYCLER_LAYOUT = "manager" ;
    @BindView(R.id.total_completed)
    TextView totalCompletedTv ;
    @BindView(R.id.total_due_date)
    TextView totalDueDateTv ;
    @BindView(R.id.toolbar)
    Toolbar toolbar ;
    @BindView(R.id.category_recycler_view)
    RecyclerView  recyclerView ;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout ;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout ;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton ;

    private int mColor ;
    private int revealX ,revealY , width , height;
    private int totalCompleted =0, totalDueDate=0;

    private TasksAdapter tasksAdapter ;
    private FirebaseDatabase mFirebaseDatabase ;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private ChildEventListener mChildEventListener ;

    private String  category ;
    private LinearLayoutManager linearLayoutManager;
    @BindView(R.id.root_view)
    CoordinatorLayout rootLayout ;
    private Parcelable layoutManagerSavedState;



    public static OneCategoryFragment newInstance(String title) {

        Bundle args = new Bundle();
        args.putString("title" , title);

        OneCategoryFragment fragment = new OneCategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if(getArguments()!=null)
        {
            if(getArguments().containsKey("title")){
                category = getArguments().getString("title");


            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("color", mColor);
        outState.putInt("complete", totalCompleted);
        outState.putInt("due", totalDueDate);
        outState.putString("title", category);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());


    }

    private void changeSttausBarColor(){
        Window window = getActivity().getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(),mColor));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one_category, container, false);
        ButterKnife.bind(this, rootView);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getUid() != null)
            mDatabaseReference = mFirebaseDatabase.getReference().child(firebaseAuth.getUid()).child(category);

        Query query = mDatabaseReference.child("tasks").orderByChild("createdDate");
        FirebaseRecyclerOptions<WheelTask> options =
                new FirebaseRecyclerOptions.Builder<WheelTask>()
                        .setQuery(query, WheelTask.class)
                        .build();

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);


        tasksAdapter = new TasksAdapter(options, getActivity(), category);
        recyclerView.setAdapter(tasksAdapter);

        if (getActivity() != null) {
            final Intent intent = getActivity().getIntent();
            if (savedInstanceState == null) {

                mColor = toolbarColor(category);
                if (intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
                    rootLayout.setVisibility(View.INVISIBLE);

                    revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
                    revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);
                    height = intent.getIntExtra("h", 0);
                    width = intent.getIntExtra("w", 0);


                    ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
                    if (viewTreeObserver.isAlive()) {
                        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                revealActivity(revealX, revealY, width, height);
                                rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });
                    }
                }


            } else {

                rootLayout.setVisibility(View.VISIBLE);
                mColor = savedInstanceState.getInt("color", 0);
                category = savedInstanceState.getString("title");
                totalCompleted = savedInstanceState.getInt("complete", 0);
                totalDueDate = savedInstanceState.getInt("due", 0);
                layoutManagerSavedState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
                if (layoutManagerSavedState != null) {
                    recyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
                }
            }

        }







        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

            if(((AppCompatActivity) getActivity()).getSupportActionBar() !=null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            toolbar.setTitle(category);
            appBarLayout.setBackgroundColor(getResources().getColor(mColor));
            toolbar.setBackgroundColor(getResources().getColor(mColor));
            collapsingToolbarLayout.setBackgroundColor(getResources().getColor(mColor));
            changeSttausBarColor();
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(mColor)));

        }
        return rootView;
    }


    protected void revealActivity(int x, int y, int width, int height) {
        float
                finalRadius = (float) Math.sqrt(width * width + height * height);
        int duration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
        circularReveal.setDuration(duration);
        circularReveal.setInterpolator(new AccelerateInterpolator());
//            circularReveal.setInterpolator(new FastOutSlowInInterpolator());
        startColorAnimation(rootLayout, getResources().getColor(mColor), getResources().getColor(android.R.color.white), duration);
        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();

    }

    protected void unRevealActivity() {

        float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                rootLayout, revealX, revealY, finalRadius, 0);
        int duration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        circularReveal.setDuration(400);
        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rootLayout.setVisibility(View.INVISIBLE);
            }
        });

        startColorAnimation(rootLayout, getResources().getColor(android.R.color.white), getResources().getColor(mColor), duration);

        circularReveal.start();
    }

    static void startColorAnimation(final View view, final int startColor, final int endColor, int duration) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(startColor, endColor);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        anim.setDuration(duration);
        anim.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unRevealActivity();
                if (getActivity() != null)
                    getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        tasksAdapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        attachChildListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        tasksAdapter.stopListening();
        if(mChildEventListener!=null)
            mDatabaseReference.removeEventListener(mChildEventListener);
    }


    @OnClick(R.id.fab)
    void openNewTaskActivity() {
        Intent intent = new Intent(getActivity(), NewTaskActivity.class);
        intent.putExtra("category", category);
        intent.putExtra("color", mColor);
        intent.putExtra("isEditable", false);
        startActivity(intent);
    }




    private void attachChildListener(){
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                totalCompleted =0;
                totalDueDate=0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    WheelTask task = postSnapshot.getValue(WheelTask.class);
                    assert task != null;
                    if(task.isCompleted())
                        totalCompleted++;
                    if(task.getDueDate()!=null && !task.getDueDate().isEmpty())
                        totalDueDate++;
                }
                totalCompletedTv.setText(String.valueOf(totalCompleted).concat(" ").concat(getString(R.string.completed)));
                totalDueDateTv.setText(String.valueOf(totalDueDate).concat(" ").concat(getString(R.string.due_to_date)));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);

    }


}
