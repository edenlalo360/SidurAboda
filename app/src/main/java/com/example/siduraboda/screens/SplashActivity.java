package com.example.siduraboda.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.siduraboda.R;

public class SplashActivity extends AppCompatActivity {

    // זמן הצגה במילי-שניות (3 שניות)
    private static final int SPLASH_DISPLAY_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // תמיכה במסך מלא (Edge-to-Edge)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // טיפול ברווחים של פסי המערכת (סטטוס בר וכו')
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // שימוש ב-Handler כדי לבצע פעולה לאחר עיכוב (במקום Thread כמו בדוגמה של המורה - זה יותר מקובל באנדרואיד)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // יצירת Intent כדי לעבור ל-MainActivity
                // שים לב: ודא שיש לך Activity בשם MainActivity (או שנה את השם למה שקיים אצלך)
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);

                // הוספת הדגלים החשובים כדי שלא יהיה ניתן לחזור לספלאש
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // התחלת ה-Activity החדשה
                startActivity(intent);

                // סגירת ה-SplashActivity
                finish();
            }
        }, SPLASH_DISPLAY_TIME);
    }
}