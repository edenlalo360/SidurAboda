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
import com.example.siduraboda.models.Student;
import com.example.siduraboda.services.DatabaseService;

import java.util.List;

public class SidurYomAbodaActivity extends AppCompatActivity {

    private final java.util.List<Lesson> allLessons = new java.util.ArrayList<>(); // כל השיעורים מה-DB
    SidurYomAbodaAdapter adapter;
    RecyclerView rvWorkday;
    private java.time.LocalDate startOfDisplayedWeek; // יום ראשון של השבוע שמוצג
    private Button[] dayButtons; // מערך לכפתורי הימים
    private android.widget.TextView tvDateHeader; // התאריך למעלה

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
        // 1. קישור התאריך למעלה
        tvDateHeader = findViewById(R.id.textView2);

        // 2. קישור כפתורי הימים למערך (לפי ה-IDs ב-XML שלך)
        dayButtons = new Button[]{
                findViewById(R.id.button20), // א'
                findViewById(R.id.button19), // ב'
                findViewById(R.id.button18), // ג'
                findViewById(R.id.button15), // ד'
                findViewById(R.id.button14), // ה'
                findViewById(R.id.button13)  // ו'
        };

        // 3. הגדרת השבוע הנוכחי (מתחיל ביום ראשון הקרוב/הנוכחי)
        startOfDisplayedWeek = java.time.LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY));

        // 4. עדכון הטקסט על הכפתורים
        updateCalendarUI();

        // 5. הגדרת כפתורי החצים (+ ו -)
        findViewById(R.id.button9).setOnClickListener(v -> moveWeek(7));  // שבוע קדימה
        findViewById(R.id.button10).setOnClickListener(v -> moveWeek(-7)); // שבוע אחורה

        Button button6 = findViewById(R.id.siduryomavodaTOmain); //סידור יום עבודה לעמוד בית
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
        loadLessons();
        loadUsers();
    }

    private void loadLessons() {
        String teacherId = com.example.siduraboda.utils.SharedPreferencesUtil.getTeacherId(this);

        DatabaseService.getInstance().getLessonList(new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(java.util.List<Lesson> lessons) {
                allLessons.clear();
                // סינון רק השיעורים ששייכים למורה המחובר
                for (Lesson lesson : lessons) {
                    if (lesson.getTeacherId() != null && lesson.getTeacherId().equals(teacherId)) {
                        allLessons.add(lesson);
                    }
                }
                // אחרי שהמידע נטען, נציג את השיעורים של היום הנוכחי
                filterLessonsByDate(java.time.LocalDate.now());
            }

            @Override
            public void onFailed(Exception e) {
                android.widget.Toast.makeText(SidurYomAbodaActivity.this, "שגיאה בטעינת שיעורים", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUsers() {
        DatabaseService.getInstance().getStudentList(new DatabaseService.DatabaseCallback<List<Student>>() {

            @Override
            public void onCompleted(List<Student> studentList) {
                adapter.setStudentList(studentList);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });

    }

    // פונקציה שמעדכנת את הכפתורים לפי השבוע הנבחר
    private void updateCalendarUI() {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM");
        String[] dayNames = {"א'", "ב'", "ג'", "ד'", "ה'", "ו'"};

        for (int i = 0; i < dayButtons.length; i++) {
            java.time.LocalDate date = startOfDisplayedWeek.plusDays(i);
            dayButtons[i].setText(dayNames[i] + "\n" + date.format(formatter));

            // כשלוחצים על יום, נסנן את הרשימה
            final java.time.LocalDate finalDate = date;
            dayButtons[i].setOnClickListener(v -> filterLessonsByDate(finalDate));
        }

        // עדכון התאריך למעלה
        tvDateHeader.setText(startOfDisplayedWeek.getMonth().name() + " " + startOfDisplayedWeek.getYear());
    }

    // פונקציה להזזת שבוע
    private void moveWeek(int days) {
        startOfDisplayedWeek = startOfDisplayedWeek.plusDays(days);
        updateCalendarUI();
        // הצגת השיעורים של יום ראשון בשבוע החדש
        filterLessonsByDate(startOfDisplayedWeek);
    }

    // פונקציה לסינון השיעורים לפי תאריך
    private void filterLessonsByDate(java.time.LocalDate date) {
        // הפיכת ה-LocalDate לפורמט dd/MM/yyyy כפי שנשמר ב-Firebase
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateStr = date.format(formatter);

        java.util.List<Lesson> filtered = new java.util.ArrayList<>();

        for (Lesson lesson : allLessons) {
            // השוואה בין התאריך שנבחר לתאריך של השיעור
            if (lesson.getDate() != null && lesson.getDate().equals(dateStr)) {
                filtered.add(lesson);
            }
        }
        adapter.setList(filtered);

        // סימון ויזואלי של היום הנבחר בכותרת
        tvDateHeader.setText(date.format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", new java.util.Locale("he"))));
    }
}