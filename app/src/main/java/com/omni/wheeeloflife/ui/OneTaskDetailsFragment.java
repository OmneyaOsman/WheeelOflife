package com.omni.wheeeloflife.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.omni.wheeeloflife.R;
import com.omni.wheeeloflife.model.WheelTask;
import com.omni.wheeeloflife.utils.AppConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;


public class OneTaskDetailsFragment extends Fragment {

    private WheelTask task ;
    @BindView(R.id.task_name)
    TextView taskNameTv ;
    @BindView(R.id.task_location)
    TextView addressTv ;
    @BindView(R.id.reminder_value)
    TextView reminderTv ;
    @BindView(R.id.reminder_title)
    TextView reminderTvTitle ;
    @BindView(R.id.note_value)
    TextView noteTvValue ;
    @BindView(R.id.note_title)
    TextView noteTitle;
    @BindView(R.id.open_direction)
    ImageButton directionBtn;
    @BindView(R.id.task_image)
    ImageView imageView;
    @BindView(R.id.date_tv)
    TextView dateTv;
    @BindView(R.id.delete_task)
    Button deleteBtn;
    @BindView(R.id.complete_task)
    Button completeBtn;
    private double lat=0 , lon =0;
    private String category ;
    private int mColor ;
    private String imageUrl ;

    private FirebaseDatabase mFirebaseDatabase ;
    private FirebaseAuth firebaseAuth ;



    public static OneTaskDetailsFragment newInstance(WheelTask task  , String category) {
        Bundle args = new Bundle();
        args.putParcelable("task" , task);
        args.putString("category" , category);
        OneTaskDetailsFragment fragment = new OneTaskDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            if(getArguments().containsKey("task")){
                task = getArguments().getParcelable("task");
                category = getArguments().getString("category");
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("color" , mColor);
        outState.putParcelable("task" , task);
        outState.putString("cat" , category);
        outState.putString("url" ,imageUrl);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_details, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            updateUI(false);
            mColor = AppConfig.toolbarColor(category);
        } else {
            updateUI(true);
            mColor = savedInstanceState.getInt("color");
            category = savedInstanceState.getString("cat");
            imageUrl = savedInstanceState.getString("url");
            task = savedInstanceState.getParcelable("task");
            loadImage(imageUrl);
        }
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


    }

    private void updateUI(boolean isSavedInstanceState) {

        if (task != null) {
            if (task.getDueDate() != null && task.getDueTime() != null)
                dateTv.setText(getString(R.string.due_date_title).concat(task.getDueDate()));
            else
                dateTv.setText(getString(R.string.created_at).concat(task.getCreatedDate()));

            if (task.getTaskName() != null)
                taskNameTv.setText(task.getTaskName());
            if (task.getAddress() != null) {
                addressTv.setVisibility(View.VISIBLE);
                directionBtn.setVisibility(View.VISIBLE);
                addressTv.setText(task.getAddress());
                lat = task.getAddressLat();
                lon = task.getAddressLon();
            } else {
                addressTv.setVisibility(View.GONE);
                directionBtn.setVisibility(View.GONE);
            }
            if (task.getNote() != null) {
                noteTvValue.setVisibility(View.VISIBLE);
                noteTitle.setVisibility(View.VISIBLE);
                noteTvValue.setText(task.getNote());
            } else {
                noteTvValue.setVisibility(View.GONE);
                noteTitle.setVisibility(View.GONE);
            }

            if (!isSavedInstanceState) {
                if (task.getImageUrl() != null) {
                    imageView.setVisibility(View.VISIBLE);
                    loadImage(task.getImageUrl());
                } else {
                    if (isNetworkConnected()) {
                        new PhotosAsyncTask().execute("beautiful-blur");
                    } else
                        imageView.setBackgroundColor(getResources().getColor(mColor));
                    imageView.setVisibility(View.VISIBLE);
                }
            }
            if (task.getReminderUnite() != null && !task.getReminderUnite().isEmpty()) {
                reminderTv.setVisibility(View.VISIBLE);
                reminderTvTitle.setVisibility(View.VISIBLE);
                reminderTv.setText(String.valueOf(task.getReminderNumber()).concat(" ")
                        .concat(task.getReminderUnite()).concat(getString(R.string.before)).concat(" due date"));
            } else {
                reminderTv.setVisibility(View.GONE);
                reminderTvTitle.setVisibility(View.GONE);
            }

            if (task.isCompleted())
                completeBtn.setVisibility(View.GONE);

        }

    }

    @OnClick(R.id.open_direction)
    void openGoogleMap() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.google.com").appendPath("maps").appendPath("dir").appendPath("").appendQueryParameter("api", "1")
                .appendQueryParameter("destination", lat + "," + lon);
        String url = builder.build().toString();
        Log.d("Directions", url);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        mapIntent.setData(Uri.parse(url));
        startActivity(mapIntent);
    }

    @OnClick(R.id.edit_btn)
    void editTask() {
        Intent  intent = new Intent(getActivity() , NewTaskActivity.class);
        intent.putExtra("task" , task);
        intent.putExtra("category" , category);
        intent.putExtra("color" , mColor);
        intent.putExtra("url" , imageUrl);
        intent.putExtra("isEditable", true);
        if(getActivity()!=null) {
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        }

    }

    @OnClick(R.id.complete_task)
    void completeTask() {
        if (firebaseAuth.getUid() != null) {
            Query query = mFirebaseDatabase.getReference().child(firebaseAuth.getUid())
                    .child(category)
                    .child("tasks").child(task.getKey()).child("completed");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().setValue(true);
                    if (getActivity() != null)
                        getActivity().finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });
        }


    }

    @OnClick(R.id.delete_task)
    void deleteTask() {
        if (firebaseAuth.getUid() != null) {
            Query query = mFirebaseDatabase.getReference().child(firebaseAuth.getUid())
                    .child(category)
                    .child("tasks").orderByChild("key").equalTo(task.getKey());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                        taskSnapshot.getRef().removeValue();
                    }
                    if (getActivity() != null)
                        getActivity().finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });

        }
    }

    // check if Network available
    public boolean isNetworkConnected() {
        boolean isConnected = false;

        try {

            ConnectivityManager connectivityManager = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            isConnected = networkInfo.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    private void loadImage(final String url) {
        imageUrl = url;
        if (getActivity() != null)
            Glide.with(getActivity()).load(url).placeholder(R.drawable.pattern)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop().into(imageView);
    }

    class PhotosAsyncTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            return AppConfig.fetchPhotoURL(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            loadImage(s);

        }
    }


}
