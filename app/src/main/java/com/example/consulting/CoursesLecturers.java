package com.example.consulting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CoursesLecturers extends AppCompatActivity {

    EditText con_title_input, con_description_input, course_title, con_course_id;
    Button btn_submit;
    ProgressBar loading;
    SessionManager session;
    private static String URL_POST_CONSULTING = "http://trepcacorporation.com/consulting/consulting.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_lecturers);

        session = new SessionManager(getApplicationContext());
        btn_submit = findViewById(R.id.btn_submit);

        Intent intent = getIntent();
        //String lecturer_id = intent.getStringExtra("LECTURER_ID");
        String lecturer_name = intent.getStringExtra("LECTURER_NAME");
        String course_id = intent.getStringExtra("COURSE_ID");
        String course_name = intent.getStringExtra("COURSE_NAME");
        String student_name = intent.getStringExtra("STUDENT_NAME");

        //Finding view with associated IDs
        loading = findViewById(R.id.loading);
        con_title_input = findViewById(R.id.con_title_input);
        con_description_input = findViewById(R.id.con_description_input);
        course_title = findViewById(R.id.con_course_input);
        con_course_id = findViewById(R.id.con_course_id);


        //Get the user data from SessionManager
        //HashMap<String, String> user = session.getUserDetails();
        //String studentId = user.get(SessionManager.KEY_STUDENT_ID);



        course_title.setText(course_name.replace("}",""));
        con_course_id.setText(course_id);


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mTitle = con_title_input.getText().toString().trim();
                String mDesc = con_description_input.getText().toString().trim();
                String mCourseId = con_course_id.getText().toString().trim();

                if(!mTitle.isEmpty() || !mDesc.isEmpty())
                {
                    //Toast.makeText(CoursesLecturers.this, mCourseId, Toast.LENGTH_SHORT).show();
                    Consulting(mCourseId, mTitle, mDesc);
                } else {
                    con_title_input.setError("Title field is required!");
                    con_description_input.setError("Description field is required!");
                }
            }
        });


    }

    private void Consulting(final String course_id, final String consulting_title, final String consulting_desc) {
        loading.setVisibility(View.VISIBLE);
        btn_submit.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_CONSULTING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){

                                con_title_input.setText("");
                                con_description_input.setText("");

                                loading.setVisibility(View.GONE);
                                btn_submit.setVisibility(View.VISIBLE);
                                Toast.makeText(CoursesLecturers.this, "New consultation added!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.setVisibility(View.GONE);
                            btn_submit.setVisibility(View.VISIBLE);
                            Toast.makeText(CoursesLecturers.this, "Error Shqip: "+e.toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.setVisibility(View.GONE);
                btn_submit.setVisibility(View.VISIBLE);
                Toast.makeText(CoursesLecturers.this, "Error Shqip: "+error.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cou_id", course_id);
                params.put("con_title", consulting_title);
                params.put("con_desc", consulting_desc);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
