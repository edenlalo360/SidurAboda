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
import com.example.siduraboda.adapters.StudentListAdapter;
import com.example.siduraboda.models.Student;
import com.example.siduraboda.services.DatabaseService;

import java.util.List;

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

            }

            @Override
            public void onLongClick(Student student) {

            }
        });

        RecyclerView recyclerView = findViewById(R.id.studentsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);

        Button button7 = findViewById(R.id.studentslistTOmain); //רשימת תלמידים לבית
        button7.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Intent intent = new Intent(StudentsListActivity.this, MainActivity.class);
                                           startActivity(intent);
                                       }
                                   }
        );

        Button button11 = findViewById(R.id.plusstudents); //הוספת תלמיד ברשימת תלמידים
        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentsListActivity.this, AddStudentActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        DatabaseService.getInstance().getStudentList(new DatabaseService.DatabaseCallback<List<Student>>() {
            @Override
            public void onCompleted(List<Student> students) {
                listAdapter.setList(students);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }
}