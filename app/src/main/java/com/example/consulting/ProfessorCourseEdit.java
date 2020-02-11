package com.example.consulting;

import androidx.appcompat.app.AppCompatActivity;

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

    private static String URL_EDIT_CONSULTING = "http://trepcacorporation.com/consulting/consulting.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_course_edit);

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
                } else {
                    tvClassroom.setVisibility(View.GONE);
                    tvConsultationDate.setVisibility(View.GONE);
                    tvConsultationTime.setVisibility(View.GONE);
                    etClassroom.setVisibility(View.GONE);
                    calCalendar.setVisibility(View.GONE);
                    tpTime.setVisibility(View.GONE);
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

                String selecteDate = calCalendar.getYear() + "-" + (calCalendar.getMonth() + 1) + "-" + calCalendar.getDayOfMonth();
                String selectedTime = String.format("%02d:%02d:00", tpTime.getCurrentHour(), tpTime.getCurrentMinute(), "00");

                Toast.makeText(ProfessorCourseEdit.this, selectedTime, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void EditConsulting(final String course_id, final String con_approve, final String con_classroom, final String con_date, final String con_time) {
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


                                //loading.setVisibility(View.GONE);
                                //btn_submit.setVisibility(View.VISIBLE);
                                Toast.makeText(ProfessorCourseEdit.this, "New consultation added!", Toast.LENGTH_SHORT).show();
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
                //params.put("cou_id", course_id);
                //params.put("stu_id", student_unique_id);
                //params.put("con_title", consulting_title);
                //params.put("con_desc", consulting_desc);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
