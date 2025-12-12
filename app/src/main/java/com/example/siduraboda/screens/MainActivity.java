package com.example.siduraboda.screens;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.siduraboda.R;
import com.example.siduraboda.utils.SharedPreferencesUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferencesUtil.getUser(this).getFirstName();

        Button button1 = findViewById(R.id.mainTOsiduryomavoda); //סידור יום עבודה
        button1.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(MainActivity.this, SidurYomAbodaActivity.class);
                   startActivity(intent);
               }
           }
        );

        Button button2 = findViewById(R.id.mainTOstudentslist); //רשימת תלמידים
        button2.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Intent intent = new Intent(MainActivity.this, StudentsListActivity.class);
                                           startActivity(intent);
                                       }
                                   }
        );

        Button button3 = findViewById(R.id.mainTOtestslist); //רשימת טסטים
        button3.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Intent intent = new Intent(MainActivity.this, TestsListActivity.class);
                                           startActivity(intent);
                                       }
                                   }
        );

        Button button4 = findViewById(R.id.mainTOaddstudent); //הוספת תלמיד
        button4.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Intent intent = new Intent(MainActivity.this, AddStudentActivity.class);
                                           startActivity(intent);
                                       }
                                   }
        );

        Button button5 = findViewById(R.id.mainTOinfocar); //ניהול רכב
        button5.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(MainActivity.this, InfoCarActivity.class);
                   startActivity(intent);
               }
           }
        );


        ImageButton button15 = findViewById(R.id.btn_sign_out); //התנתקות
        button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        }
        );
    }

        private void signOut() {
            Log.d(TAG, "Sign out button clicked");
            SharedPreferencesUtil.signOutUser(MainActivity.this);

            Log.d(TAG, "User signed out, redirecting to LandingActivity");
            Intent landingIntent = new Intent(MainActivity.this, LandingActivity.class);
            landingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(landingIntent);
        }
}