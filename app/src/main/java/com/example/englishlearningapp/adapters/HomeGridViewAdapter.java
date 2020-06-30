package com.example.englishlearningapp.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.activity.CameraActivity;
import com.example.englishlearningapp.activity.MainHomeActivity;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.activity.VietnameseActivity;
import com.example.englishlearningapp.fragments.SubjectsFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.HomeFragment;

import java.io.File;
import java.util.ArrayList;

public class HomeGridViewAdapter extends RecyclerView.Adapter <HomeGridViewAdapter.MyViewHolder>{


    ArrayList subjectImages;
    Context context;
    SubjectsFragment subjectsFragment = new SubjectsFragment();
    HomeFragment homeFragment;

    public HomeGridViewAdapter(Context context, ArrayList subjectImages, HomeFragment homeFragment) {
        this.context = context;
        this.subjectImages = subjectImages;
        this.homeFragment = homeFragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.image.setImageResource((Integer) subjectImages.get(position));
        final MainHomeActivity mainHomeActivity = (MainHomeActivity) context;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "You clicked " + subjectNames.get(position), Toast.LENGTH_SHORT).show();
                switch (position){
                    case 0:
                        /*Intent subjectIntent = new Intent(context, SubjectActivity.class);
                        context.startActivity(subjectIntent);*/
                        mainHomeActivity.showFragment(subjectsFragment);
                        break;
                    case 1:
                        //Navigate to Vietnamese - English Dictionary
                        Intent vaIntent = new Intent(context, VietnameseActivity.class);
                        context.startActivity(vaIntent);
                        break;
                    case 2:
                        onLaunchCamera();
                        break;
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return subjectImages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        ImageView image;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            image = itemView.findViewById(R.id.imageViewHomeItem);
        }
    }

    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    public static File photoFile;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onLaunchCamera() {
        if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ((MainHomeActivity) context).requestPermissions(new String[]{Manifest.permission.CAMERA}, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } else {
            // create Intent to take a picture and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Create a File reference for future access
            photoFile = getPhotoFileUri(photoFileName);

            // wrap File object into a content provider
            // required for API >= 24
            Uri fileProvider = FileProvider.getUriForFile(context, "com.example.englishlearningapp.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                // Start the image capture intent to take photo
                ((MainHomeActivity) context).startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }



    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }
}
