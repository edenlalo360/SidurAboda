package com.example.siduraboda.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siduraboda.R;
import com.example.siduraboda.adapters.LessonAdapter;
import com.example.siduraboda.models.Lesson;
import com.example.siduraboda.models.Student;
import com.example.siduraboda.models.Teacher;
import com.example.siduraboda.services.DatabaseService;
import com.example.siduraboda.utils.SharedPreferencesUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImStudentActivity extends AppCompatActivity {

    private TextView tvStudentName, tvTeacherName, tvStudentPhone, tvStudentAddress;
    private TextView tvStudentBirth, tvStudentTheory, tvStudentEyes, tvStudentHealth;
    private TextView statusProcess, statusTest, statusPassed;
    private TextView tvNoFutureLessons, tvNoPastLessons;
    private RecyclerView rvFutureLessons, rvPastLessons;
    private Button btnSignOut;

    private String currentStudentId;
    private ArrayList<Lesson> futureLessonsList;
    private ArrayList<Lesson> pastLessonsList;
    private LessonAdapter futureAdapter;
    private LessonAdapter pastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_student);

        // Get student ID from intent or SharedPreferences
        currentStudentId = getIntent().getStringExtra("STUDENT_ID");
        if (currentStudentId == null || currentStudentId.isEmpty()) {
            com.example.siduraboda.models.Student savedStudent = com.example.siduraboda.utils.SharedPreferencesUtil.getStudent(this);
            if (savedStudent != null) {
                currentStudentId = savedStudent.getId();
            }
        }

        initViews();
        setupRecyclerViews();

        if (currentStudentId != null && !currentStudentId.isEmpty()) {
            loadData();
        } else {
            Toast.makeText(this, "מזהה תלמיד חסר", Toast.LENGTH_SHORT).show();
        }

        btnSignOut.setOnClickListener(v -> signOut());
    }

    private void initViews() {
        tvStudentName = findViewById(R.id.tv_student_name_display);
        tvTeacherName = findViewById(R.id.tv_teacher_name_display);
        tvStudentPhone = findViewById(R.id.tv_student_phone);
        tvStudentAddress = findViewById(R.id.tv_student_address);
        tvStudentBirth = findViewById(R.id.tv_student_birthdate);
        tvStudentTheory = findViewById(R.id.tv_student_theory);
        tvStudentEyes = findViewById(R.id.tv_student_eyes);
        tvStudentHealth = findViewById(R.id.tv_student_health);
        statusProcess = findViewById(R.id.status_process);
        statusTest = findViewById(R.id.status_test);
        statusPassed = findViewById(R.id.status_passed);
        rvFutureLessons = findViewById(R.id.rv_future_lessons);
        rvPastLessons = findViewById(R.id.rv_past_lessons);
        tvNoFutureLessons = findViewById(R.id.tv_no_future_lessons);
        tvNoPastLessons = findViewById(R.id.tv_no_past_lessons);
        btnSignOut = findViewById(R.id.signoutStudent);
    }

    private void setupRecyclerViews() {
        futureLessonsList = new ArrayList<>();
        pastLessonsList = new ArrayList<>();
        futureAdapter = new LessonAdapter(futureLessonsList);
        pastAdapter = new LessonAdapter(pastLessonsList);

        rvFutureLessons.setLayoutManager(new LinearLayoutManager(this));
        rvFutureLessons.setAdapter(futureAdapter);
        rvPastLessons.setLayoutManager(new LinearLayoutManager(this));
        rvPastLessons.setAdapter(pastAdapter);

        rvFutureLessons.setNestedScrollingEnabled(false);
        rvPastLessons.setNestedScrollingEnabled(false);
    }

    protected void signOut() {
        SharedPreferencesUtil.signOutStudent(getApplicationContext());
        Intent landingIntent = new Intent(getApplicationContext(), LandingActivity.class);
        landingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(landingIntent);
        finish();
    }

    private void loadData() {
        // Load Student
        DatabaseService.getInstance().getStudent(currentStudentId, new DatabaseService.DatabaseCallback<Student>() {
            @Override
            public void onCompleted(Student student) {
                if (student != null) {
                    tvStudentName.setText("שלום, " + student.getName());
                    tvStudentPhone.setText("📞טלפון: " + (student.getPhone() != null ? student.getPhone() : ""));
                    tvStudentAddress.setText("📍כתובת: " + (student.getAddress() != null ? student.getAddress() : ""));

                    if (student.getBirthdate() != null) {
                        SimpleDateFormat birthSdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        tvStudentBirth.setText("📅תאריך לידה: " + birthSdf.format(student.getBirthdate()));
                    }

                    tvStudentTheory.setText("📝תיאוריה: " + (student.getTheory() != null && student.getTheory() ? "בוצע" : "לא בוצע"));
                    tvStudentEyes.setText("👁️בדיקת עיניים: " + (student.getCheckeye() != null && student.getCheckeye() ? "בוצע" : "לא בוצע"));
                    tvStudentHealth.setText("🏥הצהרת בריאות: " + (student.getHealthdec() != null && student.getHealthdec() ? "בוצע" : "לא בוצע"));

                    updateStatusUI(student.getStatus());

                    if (student.getTeacherId() != null) {
                        loadTeacher(student.getTeacherId());
                    }
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ImStudentActivity.this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
            }
        });

        // Load Lessons
        DatabaseService.getInstance().getLessonList(new DatabaseService.DatabaseCallback<List<Lesson>>() {
            @Override
            public void onCompleted(List<Lesson> lessons) {
                if (lessons == null) return;
                futureLessonsList.clear();
                pastLessonsList.clear();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String today = sdf.format(new Date());
                Calendar now = Calendar.getInstance();
                int currentHour = now.get(Calendar.HOUR_OF_DAY);
                int currentMinute = now.get(Calendar.MINUTE);

                for (Lesson lesson : lessons) {
                    if (lesson.getStudentId() != null && lesson.getStudentId().equals(currentStudentId)) {
                        try {
                            // המרה של תאריך ושעת סיום לאובייקט זמן אחד
                            SimpleDateFormat fullSdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            String lessonEndTimeStr = lesson.getDate() + " " + (lesson.getDayAndHours() != null ? lesson.getDayAndHours().getEndTime().toString() : "00:00");
                            Date lessonEndTime = fullSdf.parse(lessonEndTimeStr);
                            Date nowTime = new Date();

                            if (lessonEndTime != null && lessonEndTime.before(nowTime)) {
                                pastLessonsList.add(lesson);
                            } else {
                                futureLessonsList.add(lesson);
                            }
                        } catch (Exception e) {
                            futureLessonsList.add(lesson);
                        }
                    }
                }

                // Update visibility for empty states
                tvNoFutureLessons.setVisibility(futureLessonsList.isEmpty() ? View.VISIBLE : View.GONE);
                tvNoPastLessons.setVisibility(pastLessonsList.isEmpty() ? View.VISIBLE : View.GONE);
                rvFutureLessons.setVisibility(futureLessonsList.isEmpty() ? View.GONE : View.VISIBLE);
                rvPastLessons.setVisibility(pastLessonsList.isEmpty() ? View.GONE : View.VISIBLE);

                futureAdapter.notifyDataSetChanged();
                pastAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
            }
        });
    }

    private void loadTeacher(String teacherId) {
        DatabaseService.getInstance().getUser(teacherId, new DatabaseService.DatabaseCallback<Teacher>() {
            @Override
            public void onCompleted(Teacher teacher) {
                if (teacher != null) {
                    tvTeacherName.setText("מורה: " + teacher.getFirstName() + " " + teacher.getLastName());
                }
            }

            @Override
            public void onFailed(Exception e) {
            }
        });
    }

    private void updateStatusUI(String status) {
        statusProcess.setBackgroundResource(R.drawable.bg_status_inactive);
        statusTest.setBackgroundResource(R.drawable.bg_status_inactive);
        statusPassed.setBackgroundResource(R.drawable.bg_status_inactive);
        if (status == null) return;
        if (status.equals("בתהליך"))
            statusProcess.setBackgroundResource(R.drawable.bg_status_active);
        else if (status.equals("בטסט"))
            statusTest.setBackgroundResource(R.drawable.bg_status_active);
        else if (status.equals("עבר"))
            statusPassed.setBackgroundResource(R.drawable.bg_status_active);
    }
}
