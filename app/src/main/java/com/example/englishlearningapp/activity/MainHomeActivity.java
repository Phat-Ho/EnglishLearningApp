package com.example.englishlearningapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.HomeGridViewAdapter;
import com.example.englishlearningapp.fragments.LoginFragment;
import com.example.englishlearningapp.fragments.SettingFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.HistoryFavoriteFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.HomeFragment;
import com.example.englishlearningapp.navigation_bottom_fragments.ProfileFragment;
import com.example.englishlearningapp.utils.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.FileOutputStream;
import java.io.IOException;

public class MainHomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    final Fragment homeFragment = new HomeFragment();
    final Fragment loginFragment = new LoginFragment();
    final Fragment settingFragment = new SettingFragment();
    final Fragment profileFragment = new ProfileFragment();
    final Fragment historyFavFragment = new HistoryFavoriteFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment activeFragment = homeFragment;
    private static final String TAG = "MainHomeActivity";
    LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        loginManager = new LoginManager(MainHomeActivity.this);
        setHomeFragment();
        bottomNavigation = findViewById(R.id.navigation_bottom);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        showFragment(homeFragment);
                        return true;
                    case R.id.navigation_history:
                        showFragment(historyFavFragment);
                        return true;
                    case R.id.navigation_setting:
                        showFragment(settingFragment);
                        return true;
                    case R.id.navigation_profile:
                        if(loginManager.isLogin()){
                            showFragment(profileFragment);
                        }else{
                            showFragment(loginFragment);
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private void setHomeFragment(){
        fm.beginTransaction().add(R.id.container, homeFragment, "home").commit();
    }

    public void showFragment(Fragment fragToShow){
        fm.beginTransaction().replace(R.id.container, fragToShow).commit();
        activeFragment = fragToShow;
    }

    @Override
    public void onBackPressed() {
        if(activeFragment == homeFragment){
            finish();
        }else{
            bottomNavigation.setSelectedItemId(R.id.navigation_home);
            activeFragment = homeFragment;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == HomeGridViewAdapter.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Bitmap takenImage = BitmapFactory.decodeFile(HomeGridViewAdapter.photoFile.getAbsolutePath());

                            rotateImage(takenImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                // RESIZE BITMAP, see section below
//                passBitmapIntent(takenImage);

            } else { // Result was a failure
//                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void passBitmapIntent(Bitmap takenImage){
        try {
            //Write file
            String filename = "bitmap.jpeg";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            takenImage.compress(Bitmap.CompressFormat.JPEG, 20, stream);


            //Cleanup
            stream.close();
            takenImage.recycle();

            //Pop intent
            Intent in1 = new Intent(this, CameraActivity.class);
            in1.putExtra("image", filename);
            startActivity(in1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rotateImage(Bitmap bmp) throws IOException {
        androidx.exifinterface.media.ExifInterface exifInterface = null;
        exifInterface = new androidx.exifinterface.media.ExifInterface(HomeGridViewAdapter.photoFile.toString());

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        passBitmapIntent(rotatedBitmap);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == HomeGridViewAdapter.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();

            }

        }
    }
}
