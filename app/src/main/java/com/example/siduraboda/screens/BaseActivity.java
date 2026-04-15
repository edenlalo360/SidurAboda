package com.example.siduraboda.screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.siduraboda.R;
import com.example.siduraboda.models.Car;
import com.example.siduraboda.models.Teacher;
import com.example.siduraboda.services.DatabaseService;
import com.example.siduraboda.utils.ImageUtil;
import com.example.siduraboda.utils.SharedPreferencesUtil;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BaseActivity";

    protected DatabaseService databaseService;
    protected DrawerLayout drawerLayout;
    protected Toolbar toolbar;
    protected NavigationView navigationView;

    private ImageView profileImageView;
    private ActivityResultLauncher<Intent> selectImageLauncher;
    private ActivityResultLauncher<Intent> captureImageLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        databaseService = DatabaseService.getInstance();
        super.setContentView(R.layout.activity_base);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ImageUtil.requestPermission(this);
        initImageLaunchers();
    }

    private void initImageLaunchers() {
        // בחירה מגלריה
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                            updateProfileImage(bitmap);
                        } catch (Exception e) {
                            Log.e(TAG, "Error loading image from gallery", e);
                        }
                    }
                });

        // צילום ממצלמה
        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        updateProfileImage(bitmap);
                    }
                });
    }

    private void updateProfileImage(Bitmap bitmap) {
        if (profileImageView != null) {
            profileImageView.setImageBitmap(bitmap);

            // המרה ל-Base64 ושמירה ב-Database
            String base64Image = ImageUtil.convertTo64Base(profileImageView);
            Teacher currentTeacher = SharedPreferencesUtil.getTeacher(this);

            if (currentTeacher != null && base64Image != null) {
                currentTeacher.setProfileImage(base64Image);

                // עדכון ב-Firebase
                databaseService.updateTeacher(currentTeacher.getUid(), teacher -> {
                    teacher.setProfileImage(base64Image);
                    return teacher;
                }, new DatabaseService.DatabaseCallback<Teacher>() {
                    @Override
                    public void onCompleted(Teacher updatedTeacher) {
                        // עדכון מקומי ב-SharedPreferences
                        SharedPreferencesUtil.saveTeacher(BaseActivity.this, updatedTeacher);
                        Toast.makeText(BaseActivity.this, "תמונת פרופיל עודכנה", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.e(TAG, "Failed to update profile image", e);
                    }
                });
            }
        }
    }

    private void showImageSourceDialog() {
        String[] options = {"גלריה", "מצלמה"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("בחר מקור תמונה");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) { // Gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                selectImageLauncher.launch(intent);
            } else { // Camera
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureImageLauncher.launch(takePictureIntent);
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Menu menu = navigationView.getMenu();
        menu.clear();
        updateHeader();
        addCarsToMenu();
    }

    @Override
    public void setContentView(int layoutResID) {
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        if (contentFrame != null) {
            getLayoutInflater().inflate(layoutResID, contentFrame, true);
        }
    }

    private void updateHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView usernameText = headerView.findViewById(R.id.usernameText);
        profileImageView = headerView.findViewById(R.id.profileImageView);

        Teacher currentTeacher = SharedPreferencesUtil.getTeacher(this);
        if (currentTeacher != null) {
            if (usernameText != null) {
                usernameText.setText("שלום, " + currentTeacher.getFirstName());
            }

            // טעינת תמונה קיימת אם יש
            if (profileImageView != null) {
                if (currentTeacher.getProfileImage() != null && !currentTeacher.getProfileImage().isEmpty()) {
                    Bitmap bitmap = ImageUtil.convertFrom64base(currentTeacher.getProfileImage());
                    profileImageView.setImageBitmap(bitmap);
                }

                // הגדרת לחיצה לשינוי תמונה
                profileImageView.setOnClickListener(v -> showImageSourceDialog());
            }
        }
    }

    private void addCarsToMenu() {
        Teacher teacher = SharedPreferencesUtil.getTeacher(this);
        if (teacher == null) return;

        Menu menu = navigationView.getMenu();
        SubMenu carSubMenu = menu.addSubMenu("הרכבים שלי");

        ArrayList<Car> cars = teacher.getCars();
        if (cars != null) {
            for (int i = 0; i < cars.size(); i++) {
                Car car = cars.get(i);
                String carTitle = "רכב " + (i + 1) + ": " + car.getType();
                carSubMenu.add(Menu.NONE, i, Menu.NONE, carTitle)
                        .setIcon(android.R.drawable.ic_menu_directions)
                        .setOnMenuItemClickListener(item -> {
                            Intent intent = new Intent(BaseActivity.this, InfoCarActivity.class);
                            intent.putExtra("car", car);
                            startActivity(intent);
                            return false;
                        });
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}