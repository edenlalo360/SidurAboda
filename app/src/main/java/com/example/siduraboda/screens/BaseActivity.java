package com.example.siduraboda.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.siduraboda.R;
import com.example.siduraboda.models.Car;
import com.example.siduraboda.models.Teacher;
import com.example.siduraboda.services.DatabaseService;
import com.example.siduraboda.utils.SharedPreferencesUtil;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected DatabaseService databaseService;
    protected DrawerLayout drawerLayout;
    protected Toolbar toolbar;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        databaseService = DatabaseService.getInstance();

        // טוען את השלד של התפריט
        super.setContentView(R.layout.activity_base);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // כפתור ה-Hamburger (3 קווים)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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
        Teacher currentTeacher = SharedPreferencesUtil.getTeacher(this);
        if (currentTeacher != null && usernameText != null) {
            usernameText.setText("שלום, " + currentTeacher.getFirstName());
        }
    }

    private void addCarsToMenu() {
        Teacher teacher = SharedPreferencesUtil.getTeacher(this);
        if (teacher != null) {
            Menu menu = navigationView.getMenu();
            SubMenu carSubMenu = menu.addSubMenu("הרכבים שלי");

            ArrayList<Car> cars = teacher.getCars();
            for (int i = 0; i < cars.size(); i++) {
                Car car = cars.get(i);
                String carTitle = "רכב " + (i + 1) + ": " + car.getType();
                carSubMenu.add(Menu.NONE, i, Menu.NONE, carTitle)
                        .setIcon(android.R.drawable.ic_menu_directions);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Teacher teacher = SharedPreferencesUtil.getTeacher(this);

        // לחיצה על רכב מהרשימה
        if (teacher != null && teacher.getCars() != null && id < teacher.getCars().size()) {
            Car selectedCar = teacher.getCars().get(id);
            Intent intent = new Intent(this, InfoCarActivity.class);
            intent.putExtra("car_number", selectedCar.getCarNumber());
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            signOut();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void signOut() {
        SharedPreferencesUtil.signOutTeacher(this);
        Intent intent = new Intent(this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}