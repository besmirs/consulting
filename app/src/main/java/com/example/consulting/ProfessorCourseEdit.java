package com.example.consulting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
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

public class ProfessorCourseEdit extends AppCompatActivity {

    CheckBox cbApprove;
    TextView tvClassroom, tvConsultationDate, tvConsultationTime;
    EditText etClassroom;
    DatePicker calCalendar;
    TimePicker tpTime;
    Button btn_submit;

    Intent myIntent;

    private static String URL_EDIT_CONSULTING = "http://trepcacorporation.com/consulting/consulting_edit.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_course_edit);

        final Intent intent = getIntent();
        final String cid = intent.getStringExtra("COURSE_ID");
        final String consulation_id = intent.getStringExtra("CONSULTATION_ID");
        String cname = intent.getStringExtra("COURSE_NAME");

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(cname);

        myIntent = new Intent(this, Professor_dashboard.class);

        cbApprove = findViewById(R.id.cbApprove);
        tvClassroom = findViewById(R.id.tvClassroom);
        tvConsultationDate = findViewById(R.id.tvConsultationDate);
        tvConsultationTime = findViewById(R.id.tvConsultationTime);
        etClassroom = findViewById(R.id.etClassroom);
        calCalendar = findViewById(R.id.calCalendar);
        tpTime = findViewById(R.id.tpTime);
        btn_submit = findViewById(R.id.btn_submit);


        cbApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CheckBox) view).isChecked()) {
                    tvClassroom.setVisibility(View.VISIBLE);
                    tvConsultationDate.setVisibility(View.VISIBLE);
                    tvConsultationTime.setVisibility(View.VISIBLE);
                    etClassroom.setVisibility(View.VISIBLE);
                    calCalendar.setVisibility(View.VISIBLE);
                    tpTime.setVisibility(View.VISIBLE);
                    btn_submit.setVisibility(View.VISIBLE);
                } else {
                    tvClassroom.setVisibility(View.GONE);
                    tvConsultationDate.setVisibility(View.GONE);
                    tvConsultationTime.setVisibility(View.GONE);
                    etClassroom.setVisibility(View.GONE);
                    calCalendar.setVisibility(View.GONE);
                    tpTime.setVisibility(View.GONE);
                    btn_submit.setVisibility(View.GONE);
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String approve;
                if(cbApprove.isChecked()) {
                    approve = "1";
                } else {
                    approve = "0";
                }

                String mClassRoom = etClassroom.getText().toString().trim();
                String currentClassRoom;

                if(!mClassRoom.isEmpty()) {
                    currentClassRoom = mClassRoom;
                } else
                {
                    currentClassRoom = "";
                }

                String selecteDate = calCalendar.getYear() + "-" + (calCalendar.getMonth() + 1) + "-" + calCalendar.getDayOfMonth();
                String selectedTime = String.format("%02d:%02d:00", tpTime.getCurrentHour(), tpTime.getCurrentMinute(), "00");

                EditConsulting(consulation_id, approve, currentClassRoom, selecteDate, selectedTime);
            }
        });

    }


    private void EditConsulting(final String consultationId, final String con_approve, final String con_classroom, final String con_date, final String con_time) {
        //loading.setVisibility(View.VISIBLE);
        //btn_submit.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT_CONSULTING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){
                                Toast.makeText(ProfessorCourseEdit.this, "Consultation is approved!", Toast.LENGTH_SHORT).show();
                                //finish();
                                startActivity(myIntent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //loading.setVisibility(View.GONE);
                            //btn_submit.setVisibility(View.VISIBLE);
                            Toast.makeText(ProfessorCourseEdit.this, "Error Shqip: "+e.toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //loading.setVisibility(View.GONE);
                //btn_submit.setVisibility(View.VISIBLE);
                Toast.makeText(ProfessorCourseEdit.this, "Error Shqip: "+error.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("con_id", consultationId);
                params.put("con_approve", con_approve);
                params.put("con_classroom", con_classroom);
                params.put("con_date", con_date);
                params.put("con_time", con_time);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
