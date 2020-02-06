package com.example.consulting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class CoursesLecturers extends AppCompatActivity {

    TextView coure_input, dash_studentId, dash_studentFullName;
    Button submit_consultation;
    EditText title_input, course_input;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_lecturers);

        submit_consultation = findViewById(R.id.submit_consultation);
        title_input = findViewById(R.id.con_title_input);
        course_input = findViewById(R.id.con_course_input);


        session = new SessionManager(getApplicationContext());

        Intent intent = getIntent();
        //String lecturer_id = intent.getStringExtra("LECTURER_ID");
        String lecturer_name = intent.getStringExtra("LECTURER_NAME");
        String student_name = intent.getStringExtra("STUDENT_NAME");

        //Finding view with associated IDs
        //cname = findViewById(R.id.cname);
        //dash_studentId = findViewById(R.id.dash_studentId);
        //dash_studentFullName = findViewById(R.id.dash_studentFullName);


        //Get the user data from SessionManager
        HashMap<String, String> user = session.getUserDetails();
        String studentId = user.get(SessionManager.KEY_STUDENT_ID);

        //dash_studentId.setText(studentId);
        //dash_studentFullName.setText(student_name);
        //cname.setText(lecturer_name);


        course_input.setText(lecturer_name);


        submit_consultation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(CoursesLecturers.this, "Button is Clicked!", Toast.LENGTH_SHORT).show();
                String mCourseTitle = title_input.getText().toString().trim();

                if(mCourseTitle.isEmpty())
                {
                    title_input.setError("This field is required!");
                } else {
                    title_input.setError("This field is requiredaaaaa!");
                }
            }
        });


    }



}
