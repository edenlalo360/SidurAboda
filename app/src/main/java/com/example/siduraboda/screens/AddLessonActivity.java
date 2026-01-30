package com.example.siduraboda.screens;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.siduraboda.R;
import com.example.siduraboda.models.Car;
import com.example.siduraboda.models.Lesson;
import com.example.siduraboda.models.Student;
import com.example.siduraboda.models.Teacher;
import com.example.siduraboda.services.DatabaseService;
import com.example.siduraboda.utils.SharedPreferencesUtil;
import com.example.siduraboda.utils.Validator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.UnaryOperator;

public class AddLessonActivity extends AppCompatActivity {

    EditText etLessonDate, etStartLesson;
    Spinner spStudent, spCar;
    Button btnSubmitLesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_lesson);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etLessonDate = findViewById(R.id.et_add_lesson_date);
        etStartLesson = findViewById(R.id.et_long_lesson);
        spStudent = findViewById(R.id.sp_choose_student);
        spCar = findViewById(R.id.sp_choose_car);
        btnSubmitLesson = findViewById(R.id.btn_add_lesson_submit);

        //מציג לוח שנה
        View.OnClickListener dateClickListener = v -> {
            EditText target = (EditText) v;
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {
                        String date = day + "/" + (month + 1) + "/" + year;
                        target.setText(date);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        };
        etLessonDate.setOnClickListener(dateClickListener);

        DatabaseService.getInstance().getStudentList(new DatabaseService.DatabaseCallback<List<Student>>() {
            @Override
            public void onCompleted(List<Student> students) {
                setupStudentSpinner(students);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });



        // מילוי ספינר רכב
        String[] options = {
                "בחר רכב לשיעור זה"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, options
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCar.setAdapter(adapter);

        btnSubmitLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Convert String to LocalDate (using your previous formatter)
                String dateInput = etLessonDate.getText().toString();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");
                LocalDate lessonDate = LocalDate.parse(dateInput, dateFormatter);

                // 2. Convert String to LocalTime
                String timeInput = etStartLesson.getText().toString();
                // Assumes input is "14:30" or "2:30". Use "H:mm" for 24hr or "h:mm a" for AM/PM
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
                LocalTime lessonTime = LocalTime.parse(timeInput, timeFormatter);

                String student = spStudent.getSelectedItem().toString() + "";
                String car = spCar.getSelectedItem().toString() + "";


                if(!checkInputlesson(lessonDate, etStartLesson, student,car)) {
                    return;
                }

                Lesson lesson = new Lesson(lessonDate, etStartLesson, student, car);

                String studentId = SharedPreferencesUtil.getTeacherId(AddLessonActivity.this);
                DatabaseService.getInstance().updateTeacher(studentId, new UnaryOperator<Student>() {
                    @Override
                    public Teacher apply(Student student) {
                        if (student == null) return null;
                        student.addLesson(lesson);
                        return student;
                    }
                }, new DatabaseService.DatabaseCallback<Student>() {
                    @Override
                    public void onCompleted(Student student) {
                        Toast.makeText(AddLessonActivity.this, "Lesson successfully added!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });
            }
        });
    }

    private void setupStudentSpinner(List<Student> students) {

        ArrayAdapter<Student> adapter = new ArrayAdapter<Student>(this, android.R.layout.simple_spinner_item, students) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView label = (TextView) super.getView(position, convertView, parent);
                Student student = getItem(position);
                label.setText(student.getName());
                return label;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView label = (TextView) super.getDropDownView(position, convertView, parent);
                // You can customize the dropdown text specifically here
                Student student = getItem(position);
                label.setText(student.getName());
                return label;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStudent.setAdapter(adapter);

        spStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Student selectedStudent = (Student) parent.getItemAtPosition(position);
                String selectedId = selectedStudent.getId(); // Success!
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /// Check if the input is valid
    /// @return true if the input is valid, false otherwise
    private boolean checkInputlesson(LocalDate lessonDate, etStartLesson, student,car) {

        if (!Validator.isLessonDateValid(lessonDate)) {
            etLessonDate.setError("Please enter the lesson date");
            etLessonDate.requestFocus();
            return false;
        }

        if (!Validator.isTimeValid(Startlesson)) {
            etStartLesson.setError("Car number must be between 7-8 characters long");
            etStartLesson.requestFocus();
            return false;
        }

        if (!Validator.isLicenseDateValid(student)) {
            Toast.makeText(AddLessonActivity.this, "Please select student", Toast.LENGTH_SHORT).show();
            spStudent.requestFocus();
            return false;
        }

        if (!Validator.isSpinnerCarValid(car)) {
            Toast.makeText(AddLessonActivity.this, "Please select car", Toast.LENGTH_SHORT).show();
            spCar.requestFocus();
            return false;
        }

        return true;

    }

}