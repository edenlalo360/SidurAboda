package com.example.siduraboda.screens;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.siduraboda.R;

//  יורשים מ-BaseActivity כדי לקבל את תפריט הצד
public class MainActivity extends BaseActivity {

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


        // אתחול הכפתורים הקיימים
        setupButtons();
    }

    private void setupButtons() {
        // סידור יום עבודה
        findViewById(R.id.mainTOsiduryomavoda).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SidurYomAbodaActivity.class)));

        // רשימת תלמידים
        findViewById(R.id.mainTOstudentslist).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, StudentsListActivity.class)));

        // רשימת טסטים
        findViewById(R.id.mainTOtestslist).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, TestsListActivity.class)));

        // הוספת תלמיד
        findViewById(R.id.mainTOaddstudent).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddStudentActivity.class)));

        // עדכון פרטים
        findViewById(R.id.mainTOupdate).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, UpDateActivity.class)));

        // קביעת שיעור
        findViewById(R.id.mainTOaddlesson).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddLessonActivity.class)));

    }

}