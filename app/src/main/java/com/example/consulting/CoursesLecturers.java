package com.example.consulting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class CoursesLecturers extends AppCompatActivity {

    TextView cname, dash_studentId, dash_studentFullName;
    EditText course_title;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_lecturers);

        session = new SessionManager(getApplicationContext());

        Intent intent = getIntent();
        //String lecturer_id = intent.getStringExtra("LECTURER_ID");
        String lecturer_name = intent.getStringExtra("LECTURER_NAME");
        String student_name = intent.getStringExtra("STUDENT_NAME");

        //Finding view with associated IDs
        //cname = findViewById(R.id.cname);
        dash_studentId = findViewById(R.id.dash_studentId);
        dash_studentFullName = findViewById(R.id.dash_studentFullName);
        course_title = findViewById(R.id.con_course_input);


        //Get the user data from SessionManager
        HashMap<String, String> user = session.getUserDetails();
        String studentId = user.get(SessionManager.KEY_STUDENT_ID);

        dash_studentId.setText(studentId);
        dash_studentFullName.setText(student_name);
        //cname.setText(lecturer_name);


        course_title.setText(lecturer_name);

    }



}
