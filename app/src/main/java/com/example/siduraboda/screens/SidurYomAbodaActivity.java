package com.example.siduraboda.screens;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.siduraboda.utils.SharedPreferencesUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SidurYomAbodaActivity extends AppCompatActivity {

    private final List<Lesson> allLessons = new ArrayList<>();
    private SidurYomAbodaAdapter adapter;
    private RecyclerView rvWorkday;
    private LocalDate startOfDisplayedWeek;
    private Button[] dayButtons;
    private TextView tvDateHeader;

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

        tvDateHeader = findViewById(R.id.textView2);
        dayButtons = new Button[]{
                findViewById(R.id.button20), findViewById(R.id.button19),
                findViewById(R.id.button18), findViewById(R.id.button15),
                findViewById(R.id.button14), findViewById(R.id.button13)
        };

        startOfDisplayedWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY));
        updateCalendarUI();

        findViewById(R.id.button9).setOnClickListener(v -> moveWeek(7));
        findViewById(R.id.button10).setOnClickListener(v -> moveWeek(-7));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLessons();
        loadUsers();
    }

    private void loadLessons() {
        String teacherId = SharedPreferencesUtil.getTeacherId(this);
        DatabaseService.getInstance().getLessonList(new DatabaseService.DatabaseCallback<List<Lesson>>() {
            @Override
            public void onCompleted(List<Lesson> lessons) {
                allLessons.clear();
                for (Lesson lesson : lessons) {
                    if (lesson.getTeacherId() != null && lesson.getTeacherId().equals(teacherId)) {
                        allLessons.add(lesson);
                    }
                }
                filterLessonsByDate(LocalDate.now());
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(SidurYomAbodaActivity.this, "שגיאה בטעינה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUsers() {
        DatabaseService.getInstance().getStudentList(new DatabaseService.DatabaseCallback<List<Student>>() {
            @Override
            public void onCompleted(List<Student> list) {
                adapter.setStudentList(list);
            }

            @Override
            public void onFailed(Exception e) {
            }
        });
    }

    private void updateCalendarUI() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");
        String[] dayNames = {"א'", "ב'", "ג'", "ד'", "ה'", "ו'"};
        for (int i = 0; i < dayButtons.length; i++) {
            LocalDate date = startOfDisplayedWeek.plusDays(i);
            dayButtons[i].setText(dayNames[i] + "\n" + date.format(formatter));
            dayButtons[i].setOnClickListener(v -> filterLessonsByDate(date));
        }
        tvDateHeader.setText(startOfDisplayedWeek.getMonth().name() + " " + startOfDisplayedWeek.getYear());
    }

    private void moveWeek(int days) {
        startOfDisplayedWeek = startOfDisplayedWeek.plusDays(days);
        updateCalendarUI();
        filterLessonsByDate(startOfDisplayedWeek);
    }

    private void filterLessonsByDate(LocalDate date) {
        for (int i = 0; i < dayButtons.length; i++) {
            LocalDate buttonDate = startOfDisplayedWeek.plusDays(i);

            if (buttonDate.equals(date)) {
                // יום נבחר - עיגול כחול כהה וטקסט לבן
                dayButtons[i].setBackgroundResource(R.drawable.button_selected);
                dayButtons[i].setTextColor(android.graphics.Color.WHITE);
            } else {
                // יום רגיל - שקוף וטקסט כחול
                dayButtons[i].setBackgroundResource(R.drawable.button_border);
                dayButtons[i].setTextColor(android.graphics.Color.parseColor("#388FC3"));
            }
            // ביטול ה-Tint כדי שהצבעים של ה-Drawables יעבדו
            dayButtons[i].setBackgroundTintList(null);
        }

        String dateStr = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        List<Lesson> filtered = new ArrayList<>();
        for (Lesson lesson : allLessons) {
            if (lesson.getDate() != null && lesson.getDate().equals(dateStr)) {
                filtered.add(lesson);
            }
        }
        // --- מיון לפי שעה ---
        filtered.sort((l1, l2) -> l1.getDayAndHours().getStartTime().toString().compareTo(l2.getDayAndHours().getStartTime().toString()));
        adapter.setList(filtered);
        tvDateHeader.setText(date.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("he"))));
    }
}