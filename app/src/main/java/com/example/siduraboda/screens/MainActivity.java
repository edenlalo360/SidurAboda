package com.example.siduraboda.screens;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.siduraboda.R;
import com.example.siduraboda.utils.SharedPreferencesUtil;

// שינוי קריטי: יורשים מ-BaseActivity כדי לקבל את תפריט הצד
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // setContentView של BaseActivity יזריק את activity_main לתוך ה-content_frame
        setContentView(R.layout.activity_main);

        // טיפול ב-System Bars (Insets) - וודאי שה-ID ב-XML הוא 'main'
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // אתחול הכפתורים הקיימים שלך
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

        // ניהול רכב
        findViewById(R.id.mainTOinfocar).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, InfoCarActivity.class)));

        // עדכון פרטים
        findViewById(R.id.mainTOupdate).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, UpDateActivity.class)));

        // הוספת רכב
        findViewById(R.id.mainTOaddcar).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddCarActivity.class)));

        // קביעת שיעור
        findViewById(R.id.mainTOaddlesson).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddLessonActivity.class)));

        // התנתקות (שימוש בפונקציית signOut שנמצאת ב-BaseActivity או המקורית שלך)
        ImageButton btnSignOut = findViewById(R.id.btn_sign_out);
        if (btnSignOut != null) {
            btnSignOut.setOnClickListener(v -> signOut());
        }
    }

    // פונקציית ה-signOut המקורית שלך (אופציונלי: אפשר להשתמש בזו של BaseActivity)
    @Override
    protected void signOut() {
        Log.d(TAG, "Sign out button clicked");
        SharedPreferencesUtil.signOutTeacher(MainActivity.this);

        Intent landingIntent = new Intent(MainActivity.this, LandingActivity.class);
        landingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(landingIntent);
        finish();
    }
}