package com.example.siduraboda.screens;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.example.siduraboda.adapters.LessonAdapter;
import com.example.siduraboda.adapters.StudentListAdapter;
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
import java.util.Objects;

public class StudentsListActivity extends AppCompatActivity {

    StudentListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_studets_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listAdapter = new StudentListAdapter(new StudentListAdapter.OnClickListener() {
            @Override
            public void onClick(Student student) {
                showStudentDetailsDialog(student);
            }

            @Override
            public void onLongClick(Student student) {
            }
        });

        RecyclerView recyclerView = findViewById(R.id.studentsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);

        Button button11 = findViewById(R.id.plusstudents); //הוספת תלמיד ברשימת תלמידים
        button11.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsListActivity.this, AddStudentActivity.class);
            startActivity(intent);
        });
    }

    private void showStudentDetailsDialog(Student student) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_im_student);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        TextView tvName = dialog.findViewById(R.id.tv_student_name_display);
        TextView tvTeacher = dialog.findViewById(R.id.tv_teacher_name_display);
        TextView tvPhone = dialog.findViewById(R.id.tv_student_phone);
        TextView tvAddress = dialog.findViewById(R.id.tv_student_address);
        TextView tvBirth = dialog.findViewById(R.id.tv_student_birthdate);
        TextView tvTheory = dialog.findViewById(R.id.tv_student_theory);
        TextView tvEyes = dialog.findViewById(R.id.tv_student_eyes);
        TextView tvHealth = dialog.findViewById(R.id.tv_student_health);

        TextView tvNoFuture = dialog.findViewById(R.id.tv_no_future_lessons);
        TextView tvNoPast = dialog.findViewById(R.id.tv_no_past_lessons);

        RecyclerView rvFuture = dialog.findViewById(R.id.rv_future_lessons);
        RecyclerView rvPast = dialog.findViewById(R.id.rv_past_lessons);

        Button btnEdit = dialog.findViewById(R.id.signoutStudent);
        btnEdit.setText("עריכת פרטי תלמיד");

        tvName.setText("תלמיד: " + student.getName());
        tvPhone.setText("טלפון: " + (student.getPhone() != null ? student.getPhone() : "-"));
        tvAddress.setText("כתובת: " + (student.getAddress() != null ? student.getAddress() : "-"));

        if (student.getBirthdate() != null) {
            SimpleDateFormat birthSdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvBirth.setText("תאריך לידה: " + birthSdf.format(student.getBirthdate()));
        }

        tvTheory.setText("תיאוריה: " + (student.getTheory() != null && student.getTheory() ? "בוצע" : "לא בוצע"));
        tvEyes.setText("בדיקת עיניים: " + (student.getCheckeye() != null && student.getCheckeye() ? "בוצע" : "לא בוצע"));
        tvHealth.setText("הצהרת בריאות: " + (student.getHealthdec() != null && student.getHealthdec() ? "בוצע" : "לא בוצע"));

        DatabaseService.getInstance().getUser(student.getTeacherId(), new DatabaseService.DatabaseCallback<Teacher>() {
            @Override
            public void onCompleted(Teacher teacher) {
                if (teacher != null) {
                    tvTeacher.setText("מורה: " + teacher.getFirstName() + " " + teacher.getLastName());
                }
            }

            @Override
            public void onFailed(Exception e) {
            }
        });

        updateStatusUI(dialog, student.getStatus());

        // עדכון סטטוס בלחיצה
        dialog.findViewById(R.id.status_process).setOnClickListener(v -> updateStudentStatus(student.getId(), "בתהליך", dialog));
        dialog.findViewById(R.id.status_test).setOnClickListener(v -> updateStudentStatus(student.getId(), "בטסט", dialog));
        dialog.findViewById(R.id.status_passed).setOnClickListener(v -> updateStudentStatus(student.getId(), "עבר", dialog));

        ArrayList<Lesson> futureLessons = new ArrayList<>();
        ArrayList<Lesson> pastLessons = new ArrayList<>();
        LessonAdapter futureAdapter = new LessonAdapter(futureLessons);
        LessonAdapter pastAdapter = new LessonAdapter(pastLessons);

        rvFuture.setLayoutManager(new LinearLayoutManager(this));
        rvFuture.setAdapter(futureAdapter);
        rvPast.setLayoutManager(new LinearLayoutManager(this));
        rvPast.setAdapter(pastAdapter);

        DatabaseService.getInstance().getLessonList(new DatabaseService.DatabaseCallback<List<Lesson>>() {
            @Override
            public void onCompleted(List<Lesson> lessons) {
                if (lessons == null) return;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String todayStr = sdf.format(new Date());
                Calendar now = Calendar.getInstance();
                int currentHour = now.get(Calendar.HOUR_OF_DAY);
                int currentMinute = now.get(Calendar.MINUTE);

                for (Lesson lesson : lessons) {
                    if (Objects.equals(lesson.getStudentId(), student.getId())) {
                        try {
                            // המרה של תאריך ושעת סיום לאובייקט זמן אחד
                            SimpleDateFormat fullSdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            String lessonEndTimeStr = lesson.getDate() + " " + (lesson.getDayAndHours() != null ? lesson.getDayAndHours().getEndTime().toString() : "00:00");
                            Date lessonEndTime = fullSdf.parse(lessonEndTimeStr);
                            Date nowTime = new Date();

                            if (lessonEndTime != null && lessonEndTime.before(nowTime)) {
                                pastLessons.add(lesson);
                            } else {
                                futureLessons.add(lesson);
                            }
                        } catch (Exception e) {
                            futureLessons.add(lesson);
                        }
                    }
                }

                tvNoFuture.setVisibility(futureLessons.isEmpty() ? View.VISIBLE : View.GONE);
                tvNoPast.setVisibility(pastLessons.isEmpty() ? View.VISIBLE : View.GONE);
                rvFuture.setVisibility(futureLessons.isEmpty() ? View.GONE : View.VISIBLE);
                rvPast.setVisibility(pastLessons.isEmpty() ? View.GONE : View.VISIBLE);

                futureAdapter.notifyDataSetChanged();
                pastAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
            }
        });

        btnEdit.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(StudentsListActivity.this, AddStudentActivity.class);
            intent.putExtra("EDIT_STUDENT_ID", student.getId());
            startActivity(intent);
        });

        dialog.show();
    }

    private void updateStudentStatus(String studentId, String newStatus, Dialog dialog) {
        DatabaseService.getInstance().updateStudent(studentId, student -> {
            if (student != null) {
                student.setStatus(newStatus);
            }
            return student;
        }, new DatabaseService.DatabaseCallback<Student>() {
            @Override
            public void onCompleted(Student student) {
                runOnUiThread(() -> {
                    updateStatusUI(dialog, newStatus);
                    Toast.makeText(StudentsListActivity.this, "סטטוס עודכן ל: " + newStatus, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailed(Exception e) {
                runOnUiThread(() -> Toast.makeText(StudentsListActivity.this, "שגיאה בעדכון סטטוס", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateStatusUI(Dialog dialog, String status) {
        TextView statusProcess = dialog.findViewById(R.id.status_process);
        TextView statusTest = dialog.findViewById(R.id.status_test);
        TextView statusPassed = dialog.findViewById(R.id.status_passed);

        statusProcess.setBackgroundResource(R.drawable.bg_status_inactive);
        statusTest.setBackgroundResource(R.drawable.bg_status_inactive);
        statusPassed.setBackgroundResource(R.drawable.bg_status_inactive);

        if (status == null) return;
        switch (status) {
            case "בתהליך":
                statusProcess.setBackgroundResource(R.drawable.bg_status_active);
                break;
            case "בטסט":
                statusTest.setBackgroundResource(R.drawable.bg_status_active);
                break;
            case "עבר":
                statusPassed.setBackgroundResource(R.drawable.bg_status_active);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseService.getInstance().getStudentList(new DatabaseService.DatabaseCallback<List<Student>>() {
            @Override
            public void onCompleted(List<Student> students) {
                String teacherId = SharedPreferencesUtil.getTeacherId(StudentsListActivity.this);
                students.removeIf(student -> !Objects.equals(student.getTeacherId(), teacherId));
                listAdapter.setList(students);
            }

            @Override
            public void onFailed(Exception e) {
            }
        });
    }
}
