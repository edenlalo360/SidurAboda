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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siduraboda.R;
import com.example.siduraboda.adapters.SidurYomAbodaAdapter;
import com.example.siduraboda.models.Lesson;

public class SidurYomAbodaActivity extends AppCompatActivity {

    SidurYomAbodaAdapter adapter;
    RecyclerView rvWorkday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sidur_yom_aboda);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.siduryom), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvWorkday = findViewById(R.id.rv_sidur_yom_aboda);
        rvWorkday.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SidurYomAbodaAdapter(new SidurYomAbodaAdapter.OnClickListener() {
            @Override
            public void onClick(Lesson lesson) {

            }

            @Override
            public void onLongClick(Lesson lesson) {

            }
        });

        rvWorkday.setAdapter(adapter);

        Button button6 = findViewById(R.id.siduryomavodaTOmain); //סידור יום עבודה לבית
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SidurYomAbodaActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

    }
}