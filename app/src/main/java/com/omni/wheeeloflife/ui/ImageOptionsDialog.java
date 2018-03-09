package com.omni.wheeeloflife.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.omni.wheeeloflife.R;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;


public class ImageOptionsDialog extends DialogFragment {

    private Unbinder unbinder;
    public static final int IMAGE_CODE = 1;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = getActivity().getLayoutInflater();
        View view = inflator.inflate(R.layout.photo_popup, null);
       unbinder = ButterKnife.bind(this, view);


        builder.setView(view);
        Dialog dialog = builder.create();

        return dialog;
    }


    @OnClick(R.id.open_Gallery_option)
     void checkGalleryPermissions() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        LoadImage();// init the contact list

    }


    //access to permsions
    @OnClick(R.id.open_camera_option)
    void checkCameraPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_CAMERA_ASK_PERMISSIONS);
                return ;
            }
        }

        takePhoto();// init the contact list

    }



 private int tag = 1 ;
    public void takePhoto(){
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent , tag);


    }


    //get acces to location permission
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    //get acces to location permsion
    final private int REQUEST_CODE_CAMERA_ASK_PERMISSIONS = 223;


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LoadImage();// init the contact list
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(),getString( R.string.permission_required), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case REQUEST_CODE_CAMERA_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();// init the contact list
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), getString( R.string.permission_required), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private int RESULT_LOAD_IMAGE = 346;

    private void LoadImage() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            assert selectedImage != null;
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            Intent intent = new Intent();
            intent.putExtra("path" , picturePath);
            intent.putExtra("uri" , selectedImage.toString());
            getTargetFragment().onActivityResult(IMAGE_CODE,RESULT_OK,intent);
            getDialog().dismiss();
            cursor.close();

        }

        if(requestCode == tag&& resultCode == RESULT_OK && null != data){
            Bundle args = data.getExtras();
            assert args != null;
            Bitmap img = (Bitmap) args.get("data");

            Uri selectedImage = getImageUri(getActivity() ,img);

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);

            Intent intent = new Intent();
            intent.putExtra("pathCamera" , picturePath);
            intent.putExtra("uri" , selectedImage.toString());
            getTargetFragment().onActivityResult(IMAGE_CODE,RESULT_OK,intent);


            getDialog().dismiss();
            cursor.close();


        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
