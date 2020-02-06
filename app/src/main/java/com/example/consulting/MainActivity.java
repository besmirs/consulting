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
import com.android.volley.DefaultRetryPolicy;
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

public class MainActivity extends AppCompatActivity {

    private EditText studentId, studentPassword;
    private Button btn_login;
    private ProgressBar loading;
    private TextView contactSupport;
    private TextInputLayout layoutStudentId;
    private TextInputLayout layoutStudentPassword;
    private static String URL_LOGIN = "http://trepcacorporation.com/consulting/login.php";

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
        contactSupport = findViewById(R.id.contactSupport);

        layoutStudentId = findViewById(R.id.layoutStudentId);
        layoutStudentPassword = findViewById(R.id.layoutStudentPassword);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mStudentId = studentId.getText().toString().trim();
                String mStudentPassword = studentPassword.getText().toString().trim();

                if(!mStudentId.isEmpty() || !mStudentPassword.isEmpty()){
                    Login(mStudentId, mStudentPassword);
                } else {
                    layoutStudentId.setError("Please provide student ID");
                    layoutStudentPassword.setError("Please provide password");
                }
            }
        });
    }


    private void Login(final String studentId, final String studentPassword) {
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

                                    String studentId = object.getString("studentId").trim();
                                    String studentIdentification = object.getString("studentIdentification").trim();
                                    String studentFullName = object.getString("studentFullName").trim();

                                    session.createLoginSession(studentIdentification, studentId);

                                    Intent intent = new Intent(MainActivity.this, Dashboard.class);
                                    intent.putExtra("studentId", studentId);
                                    intent.putExtra("studentIdentification", studentIdentification);
                                    intent.putExtra("studentFullName", studentFullName);
                                    startActivity(intent);

                                    loading.setVisibility(View.GONE);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.setVisibility(View.GONE);
                            btn_login.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "Error Shqip: "+e.toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.setVisibility(View.GONE);
                btn_login.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Error Shqip: "+error.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("studentId", studentId);
                params.put("studentPassword", studentPassword);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
