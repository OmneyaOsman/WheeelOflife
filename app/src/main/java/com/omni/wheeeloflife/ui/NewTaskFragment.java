package com.omni.wheeeloflife.ui;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.omni.wheeeloflife.R;
import com.omni.wheeeloflife.ReminderAlarmReciever;
import com.omni.wheeeloflife.model.WheelTask;
import com.omni.wheeeloflife.utils.AppConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;
import static com.omni.wheeeloflife.ui.ImageOptionsDialog.IMAGE_CODE;
import static com.omni.wheeeloflife.utils.AppConfig.REMINDER_RQUEST_CODE;
import static com.omni.wheeeloflife.utils.AppConfig.REQUEST_CODE_ASK_PERMISSIONS_LOCATION;

public class NewTaskFragment extends Fragment {

    private static final int PLACE_PICKER_REQUEST = 123;
    private static final int NOTE_ID = 2;
    private static final int RC_PHOTO_PICKER =  2;

    @BindView(R.id.toolbar)
    Toolbar toolbar ;
    private String title;
    private String noteString;
    private String address , category , unite="";
    private String dueDate = null;
    private String dueTime = null;
    private String downloadUriString = null ;
    private Uri selectedImageUri;
    private double lat;
    private double lon;
    private int number=0 ;
    @BindView(R.id.note_tv)
    TextView noteTV ;
    @BindView(R.id.reminder_tv)
    TextView reminderTv ;
    @BindView(R.id.address_tv)
    TextView addressTV ;
    @BindView(R.id.due_date_tv)
    TextView dueDateTV ;
    @BindView(R.id.date_btn)
    Button dateButton ;
    @BindView(R.id.time_btn)
    Button timeButton ;
    @BindView(R.id.new_task_name_ed)
    EditText titleED ;
    @BindView(R.id.text_required_tv)
    TextView errorText ;
    @BindView(R.id.image_layout)
    FrameLayout imageLayout ;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout ;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout ;
    @BindView(R.id.image)
    ImageView imageChoosenView ;
    @BindView(R.id.new_task_scrolling)
    ScrollView mScrollView ;

    private boolean isDueDate = false;
    private FirebaseDatabase mFirebaseDatabase ;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mSingleTaskStorageReference;
    private long reminderBeforeInMiliSeconds=0;

    private int mColor ;
    private WheelTask task ;
    private String imageUrl , reminder ;
    private boolean isEditableMode = false ;


    public static NewTaskFragment newInstance(String category , int color , boolean isEditableMode) {
        Bundle args = new Bundle();
        args.putString("category" , category);
        args.putInt("color" , color);
        args.putBoolean("isEditableMode" , isEditableMode);
        NewTaskFragment fragment = new NewTaskFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getArguments()!=null)
        {
            category = getArguments().getString("category");
            isEditableMode = getArguments().getBoolean("isEditableMode");
            mColor = getArguments().getInt("color" , R.color.seashell);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("ARTICLE_SCROLL_POSITION",
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});

        outState.putString("category", category);
        outState.putString("title", title);
        outState.putString("date", dueDate);
        outState.putInt("color", mColor);
        outState.putBoolean("isDueDate", isDueDate);
        if(dueDate!=null)
        outState.putString("date", dueDate);
        if(dueTime!=null)
            outState.putString("time", dueTime);
        if (address != null&& !address.trim().isEmpty()) {
            outState.putString("address", address);
            outState.putDouble("lat", lat);
            outState.putDouble("lon", lon);
        }
        if (task != null)
            outState.putParcelable("task", task);
        if (imageUrl != null)
            outState.putString("url", imageUrl);
        if (noteString != null&& !noteString.trim().isEmpty())
            outState.putString("note", noteString);
        if (reminder != null&& !reminder.trim().isEmpty()) {
            outState.putString("reminder", reminder);
            outState.putString("unite" , unite);
            outState.putInt("number" , number);
        }
        if (selectedImageUri != null)
            outState.putParcelable("imageUri", selectedImageUri);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.new_task_fragment , container , false);
        ButterKnife.bind(this , rootView);

        if(getActivity()!=null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

            if(((AppCompatActivity) getActivity()).getSupportActionBar() !=null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }


        return  rootView ;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance() ;
        mFirebaseStorage = FirebaseStorage.getInstance();
        mSingleTaskStorageReference= mFirebaseStorage.getReference().child("tasks_photos");



        if(!isNetworkConnected())
            imageLayout.setVisibility(View.GONE);
             else
            imageLayout.setVisibility(View.VISIBLE);

             if(savedInstanceState==null){
                 if(getActivity()!=null){
                     if(getActivity().getIntent()!=null && getActivity().getIntent().hasExtra("task"))
                     {
                         task = getActivity().getIntent().getParcelableExtra("task");
                         imageUrl =getActivity().getIntent().getStringExtra("url");
                         setDataInUI();
                     }
                 }
             }else{
                 final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
                 if (position != null)
                     mScrollView.post(new Runnable() {
                         public void run() {
                             mScrollView.scrollTo(position[0], position[1]);
                         }
                     });

                 if(isEditableMode) {
                     task = savedInstanceState.getParcelable("task");
                     imageUrl =savedInstanceState.getString("url");
                     loadImage(imageUrl);
                 }else{
                     loadImage(selectedImageUri);
                 }
                 mColor = savedInstanceState.getInt("color" , R.color.seashell);
                 category =savedInstanceState.getString("category");
                 title =savedInstanceState.getString("title");
                 isDueDate = savedInstanceState.getBoolean("isDueDate");
                 if (savedInstanceState.containsKey("note")) {
                     noteString = savedInstanceState.getString("note");
                     noteTV.setText(noteString);
                 }
                 if (savedInstanceState.containsKey("address")) {
                     address = savedInstanceState.getString("address");
                     lat = savedInstanceState.getDouble("lat");
                     lon = savedInstanceState.getDouble("lon");
                     addressTV.setText(address);
                 }
                 if(savedInstanceState.containsKey("imageUri")) {
                     selectedImageUri = savedInstanceState.getParcelable("imageUri");
                     loadImage(selectedImageUri);
                 }
                 titleED.setText(title);
                 if(isDueDate){
                     dueDateTV.setVisibility(View.GONE);
                     dateButton.setVisibility(View.VISIBLE);
                     timeButton.setVisibility(View.VISIBLE);
                     if(savedInstanceState.containsKey("date")) {
                         dueDate = savedInstanceState.getString("date");
                         dateButton.setText(dueDate);
                     }
                     if(savedInstanceState.containsKey("time")) {
                         dueTime = savedInstanceState.getString("time");
                         timeButton.setText(dueTime);
                     }

                     if(savedInstanceState.containsKey("reminder"))
                     {
                         reminder = savedInstanceState.getString("reminder");
                         unite = savedInstanceState.getString("unite");
                         number = savedInstanceState.getInt("number");
                         reminderTv.setText(reminder);
                     }
                 }



             }
        toolbar.setTitle(category);
        appBarLayout.setBackgroundColor(getResources().getColor(mColor));
        toolbar.setBackgroundColor(getResources().getColor(mColor));
        changeSttausBarColor();
        collapsingToolbarLayout.setBackgroundColor(getResources().getColor(mColor));
    }

    private void changeSttausBarColor(){
        Window window = getActivity().getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(),mColor));
    }

    private void setDataInUI(){
        if(task.getNote()!=null && !task.getNote().trim().isEmpty()) {
            noteString = task.getNote();
            noteTV.setText(noteString);
        }
        if(task.getAddress()!=null) {
            address = task.getAddress();
            addressTV.setText(address);
        }
        if(task.getAddressLat()!=null)
            lat = task.getAddressLat();
        if(task.getAddressLon()!=null)
            lon = task.getAddressLon();
        if(task.getTaskName()!=null) {
            title = task.getTaskName();
            titleED.setText(title);
        }
        if(task.getDueDate()!=null&& !task.getDueDate().isEmpty() ||task.getDueTime()!=null&& !task.getDueTime().isEmpty() ){
            isDueDate = true;
            dueDateTV.setVisibility(View.GONE);
            dateButton.setVisibility(View.VISIBLE);
            timeButton.setVisibility(View.VISIBLE);
            dueDate = task.getDueDate();
            if(dueDate!=null)
                dateButton.setText(dueDate);
            dueTime = task.getDueTime();
            if(dueTime!=null){
                timeButton.setText(dueTime);
            }
            if(task.getReminderUnite()!=null){
                number = task.getReminderNumber();
                unite = task.getReminderUnite();
                reminder = number + " " + unite + " before due date";
                reminderTv.setText(reminder);
            }
        }
        loadImage(imageUrl);
    }

    private void loadImage(final String url) {
        if (getActivity() != null)
            Glide.with(getActivity()).load(url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop().into(imageChoosenView);
    }
 private void loadImage(final Uri uri) {
        if (getActivity() != null)
            Glide.with(getActivity()).load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop().into(imageChoosenView);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null)
                    getActivity().onBackPressed();
            }
        });

        titleED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                titleED.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if(!b)
                            title = editable.toString();
                    }
                });

            }
        });


    }

    @OnClick(R.id.image_layout)
     void onButtonOnClicked() {
        ImageOptionsDialog fragmentDialog = new ImageOptionsDialog();
        fragmentDialog.setTargetFragment(NewTaskFragment.this, 0);
        if(getActivity()!=null)
        fragmentDialog.show(getFragmentManager(), "ImageFragmentDialog");

    }

    @OnClick(R.id.address_layout)
    void displayPlacePicker() {

assert getActivity()!=null;
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
           openPlacePicker();

        }

    }
    private void openPlacePicker(){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            if(getActivity()!=null)
                startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException ex) {
            ex.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void displayPlace(Place place) {

        lat = place.getLatLng().latitude;
        lon = place.getLatLng().longitude;
        address =String.valueOf(place.getAddress());
        addressTV.setText(address);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openPlacePicker();
                } else {
                    // Permission Denied
                    Intent callGPSSettingIntent = new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(callGPSSettingIntent);
                    Toast.makeText(getActivity(), "your Location will help us to provide you with the direction on google Maps", Toast.LENGTH_LONG)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @OnClick(R.id.note_layout)
    void openNoteActivity(){
        Intent intent = new Intent(getActivity(), NoteActivity.class);
        if(noteString!=null &&!noteString.isEmpty())
            intent.putExtra("notePre" , noteString);
        startActivityForResult(intent ,NOTE_ID);
        if(getActivity()!=null)
        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @OnClick(R.id.due_date_btn)
    void openDueDate(){
        isDueDate = true;
        dueDateTV.setVisibility(View.GONE);
        dateButton.setVisibility(View.VISIBLE);
        timeButton.setVisibility(View.VISIBLE);
    }


    @OnClick(R.id.date_btn)
    void openDatePicker(){
        DateDialogFragment dateDialogFragment;
        if(dueDate==null)
       dateDialogFragment = new DateDialogFragment();
        else {
            SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy" , Locale.ENGLISH);
            Date dateObj = null;
            try {
                dateObj = curFormater.parse(dueDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateDialogFragment = DateDialogFragment.newInstance(dateObj);


        }

        dateDialogFragment.setTargetFragment(NewTaskFragment.this , AppConfig.DATE_REQUEST_CODE);
        if(getActivity()!=null)
        dateDialogFragment.show( getActivity().getSupportFragmentManager() , "FragmentDialog");
    }

    @OnClick(R.id.time_btn)
    void openTimePicker() {
        TimePickerFrgament timePickerFrgament;
        if (dueTime == null)
            timePickerFrgament = new TimePickerFrgament();
        else {
            SimpleDateFormat curFormater = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
            Date dateObj = null;
            try {
                dateObj = curFormater.parse(dueTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            timePickerFrgament = TimePickerFrgament.newInstance(dateObj);
        }

        timePickerFrgament.setTargetFragment(NewTaskFragment.this, AppConfig.Time_REQUEST_CODE);
        if (getFragmentManager() != null)
            timePickerFrgament.show(getFragmentManager(), "FragmentDialog");
    }


    @OnClick(R.id.reminder_layout)
    void openReminderDialog(){
        if(isDueDate) {
            ReminderDialogFragment fragmentDialog = new ReminderDialogFragment();
            fragmentDialog.setTargetFragment(NewTaskFragment.this, REMINDER_RQUEST_CODE);
            if (getActivity() != null)
                fragmentDialog.show(getFragmentManager(), "RemindertDialog");
        }else
            Toast.makeText(getActivity(), getResources().getString(R.string.set_due_date_first), Toast.LENGTH_SHORT).show();
    }

    private void scheduleNotification(Notification notification, long delay) {

        Intent notificationIntent = new Intent(getActivity(), ReminderAlarmReciever.class);
        notificationIntent.putExtra(ReminderAlarmReciever.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(ReminderAlarmReciever.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        if(getActivity()!=null) {
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            assert alarmManager != null;
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
        }
    }

    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(getActivity());
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(title);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }

    private long calculateTimeInMiliSeconds(String unite, int number) {
        long delay = 0;
        switch (unite) {
            case "minutes":
                delay = number * 60000;
                break;
            case "hours":
                delay = number * 3600000;
                break;
            case "days":
                delay = number * 86400000;
                break;
        }
        return delay;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null != data) {
            switch (requestCode) {
                case PLACE_PICKER_REQUEST:
                    if(getActivity()!=null)
                    displayPlace(PlacePicker.getPlace(getActivity() , data));
                    break;
                case IMAGE_CODE:
                    String picturePath = "";
                    if (data.hasExtra("path")) {
                        picturePath = data.getStringExtra("path");
                         selectedImageUri = Uri.parse(data.getStringExtra("uri"));
                         loadImage(selectedImageUri);

                        break;

                    } else if (data.hasExtra("pathCamera")) {
                        picturePath = data.getStringExtra("pathCamera");
                         selectedImageUri = Uri.parse(data.getStringExtra("uri"));
                        break;
                    }


                case NOTE_ID:
                    noteString = data.getStringExtra("note");
                    if (!noteString.isEmpty())
                        noteTV.setText(noteString);
                    break;
                case AppConfig.DATE_REQUEST_CODE:
                    dueDate = data.getStringExtra("dueDate");
                    if (!dueDate.isEmpty())
                        dateButton.setText(dueDate);
                    break;
                case AppConfig.Time_REQUEST_CODE:
                    dueTime = data.getStringExtra("dueTime");
                    if (dueTime != null && !dueTime.isEmpty())
                        timeButton.setText(dueTime);
                    break;
                case REMINDER_RQUEST_CODE:
                    unite = data.getStringExtra("unite");
                    number = data.getIntExtra("number", 0);
                    reminder = number + " " + unite + "before due date";
                    reminderTv.setText(reminder);
                    break;
            }
        }
    }



    private void uploadTaskWithPhoto(){

        StorageReference photRef = mSingleTaskStorageReference.child(selectedImageUri.getLastPathSegment());
        photRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if(taskSnapshot.getDownloadUrl()!=null)
                 downloadUriString = taskSnapshot.getDownloadUrl().toString();
                oneTaskToCreate(downloadUriString);

            }
        });
    }
     private void oneTaskToCreate(String imageUri){
         if(firebaseAuth.getUid()!=null) {
             if (task == null) {
                 mDatabaseReference = mFirebaseDatabase.getReference().child(firebaseAuth.getUid())
                         .child(category).child("tasks");
                 String createdDay = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date());
                 DatabaseReference ref = mDatabaseReference.push();
                 WheelTask wheelTask = new WheelTask(ref.getKey() ,title, noteString, imageUri, dueDate, dueTime,
                         number, unite, createdDay, lat, lon, address);


                 ref.setValue(wheelTask, new DatabaseReference.CompletionListener() {
                     @Override
                     public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                         if (databaseError != null) {
                             System.out.println("Data could not be saved " + databaseError.getMessage());
                             if(reminder!=null){
                                 reminderBeforeInMiliSeconds = calculateTimeInMiliSeconds(unite, number);
                                 scheduleNotification(getNotification(), reminderBeforeInMiliSeconds);
                             }
                         } else {
                             System.out.println("Data saved successfully.");
                         }
                     }
                 });

             }
             else {

                 String createdDay = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date());
                 final WheelTask wheelTask = new WheelTask(task.getKey() ,title, noteString, imageUri, dueDate, dueTime,
                         number, unite, createdDay, lat, lon, address);

                 Query query = mFirebaseDatabase.getReference().child(firebaseAuth.getUid())
                         .child(category)
                         .child("tasks").child(task.getKey());

                 query.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         dataSnapshot.getRef().setValue(wheelTask);
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


    }

    private void saveTask() {

        if (selectedImageUri != null) {
            uploadTaskWithPhoto();
        }else
            oneTaskToCreate(null);

        if (getActivity() != null)
            getActivity().finish();
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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.save_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                title = titleED.getText().toString().trim();
                if( title.isEmpty())
                    errorText.setVisibility(View.VISIBLE);
                else {
                    errorText.setVisibility(View.GONE);
                    saveTask();
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
