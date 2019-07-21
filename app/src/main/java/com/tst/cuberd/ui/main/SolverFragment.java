package com.tst.cuberd.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tst.cuberd.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SolverFragment extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 222;
    private static final String TAG = "SolverFragment";
    String currentPhotoPath;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_solver, container, false);
        FloatingActionButton btnCamera = view.findViewById(R.id.fab_camera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(view);
            }
        });
        return view;
    }
    private void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(view.getContext().getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile(view);
            }catch (IOException ex){
                Log.d(TAG, "dispatchTakePictureIntent:" + ex);
            }
            if (photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(view.getContext(), view.getContext().getPackageName(), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private File createImageFile(View view) throws IOException {
        Log.d(TAG, "createImageFile: started.");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        if (view.getContext() == null){
            Log.d(TAG, "createImageFile: null context");
        }
        File storageDir = view.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
