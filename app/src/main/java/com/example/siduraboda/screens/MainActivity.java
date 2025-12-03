package com.example.siduraboda.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
                Intent intent = new Intent(MainActivity.this, Studets_listActivity.class);
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



    }
}