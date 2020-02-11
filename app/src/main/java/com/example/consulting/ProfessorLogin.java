package com.example.consulting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfessorLogin extends AppCompatActivity {

    private EditText studentId, studentPassword;
    private Button btn_login;
    private ProgressBar loading;
    private TextView professor_login;
    private TextInputLayout layoutStudentId;
    private TextInputLayout layoutStudentPassword;
    private static String URL_LOGIN = "http://trepcacorporation.com/consulting/login_professor.php";

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        studentId = findViewById(R.id.studentId);
        studentPassword = findViewById(R.id.studentPassword);
        btn_login = findViewById(R.id.btn_login);
        loading = findViewById(R.id.loading);
        professor_login = findViewById(R.id.professor_login);
        layoutStudentId = findViewById(R.id.layoutStudentId);
        layoutStudentPassword = findViewById(R.id.layoutStudentPassword);

        studentId.setHint("Professor email address");
        studentId.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        professor_login.setText("Student Login");

        professor_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mProfessorEmail = studentId.getText().toString().trim();
                String mProfessorPassword = studentPassword.getText().toString().trim();

                if(!mProfessorEmail.isEmpty() || !mProfessorPassword.isEmpty()){
                    Login(mProfessorEmail, mProfessorPassword);

                    studentId.setText("");
                    studentPassword.setText("");
                } else {
                    layoutStudentId.setError("Please provide email");
                    layoutStudentPassword.setError("Please provide password");
                }
            }
        });
    }


    private void Login(final String professor_email, final String professor_password) {
        loading.setVisibility(View.VISIBLE);
        btn_login.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            JSONArray jsonArray = jsonObject.getJSONArray("login");
                            if(success.equals("1")){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String professor_id = object.getString("professor_id").trim();
                                    String professor_email = object.getString("professor_email").trim();
                                    String professor_fullname = object.getString("professor_fullname").trim();

                                    session.createLoginSessionProfessor(professor_id, professor_email);

                                    Intent intent = new Intent(ProfessorLogin.this, Professor_dashboard.class);
                                    intent.putExtra("PROFESSOR_ID", professor_id);
                                    intent.putExtra("PROFESSOR_EMAIL", professor_email);
                                    intent.putExtra("PROFESSOR_FULLNAME", professor_fullname);
                                    startActivity(intent);

                                    loading.setVisibility(View.GONE);
                                    btn_login.setVisibility(View.VISIBLE);

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.setVisibility(View.GONE);
                            btn_login.setVisibility(View.VISIBLE);
                            Toast.makeText(ProfessorLogin.this, "Error Shqip: "+e.toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.setVisibility(View.GONE);
                btn_login.setVisibility(View.VISIBLE);
                Toast.makeText(ProfessorLogin.this, "Error Shqip: "+error.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("pro_email", professor_email);
                params.put("pro_password", professor_password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
