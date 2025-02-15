package com.example.consulting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Professor_dashboard extends AppCompatActivity {

    TextView professor_name, professor_email, tvProfessorId;
    SessionManager session;
    private String TAG = Dashboard.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    private long backPressedTime;
    Intent myIntent;

    ArrayList<HashMap<String, String>> coursesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_dashboard);
        myIntent = new Intent(this, ProfessorCourse.class);


        tvProfessorId = findViewById(R.id.tvProfessorId);
        professor_name = findViewById(R.id.professor_name);
        professor_email = findViewById(R.id.professor_email);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> professor = session.getProfessorDetails();

        final String pro_id = professor.get(SessionManager.PROFESSOR_DB_ID);
        final String pro_email = professor.get(SessionManager.PROFESSOR_EMAIL);

        professor_email.setText(pro_email);
        tvProfessorId.setText(pro_id);


        coursesList = new ArrayList<>();


        lv = findViewById(R.id.profListView);

        new GetCourses().execute();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String[] itemResponseJson =  lv.getItemAtPosition(position).toString().split(",");

                String[] course = itemResponseJson[0].split("=");
                String course_id = course[1];

                String[] professors = itemResponseJson[1].split("=");
                String professor_name = professors[1];

                String[] courseName = itemResponseJson[5].split("=");
                String course_name = courseName[1];
                String cname = course_name.replace("}", "");

                //Toast.makeText(Professor_dashboard.this, lv.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();


                myIntent.putExtra("COURSE_ID", course_id);
                myIntent.putExtra("COURSE_NAME", cname);
                myIntent.putExtra("PROFESSOR_NAME", professor_name);


                startActivity(myIntent);
            }
        });

    }

    @Override
    public void onBackPressed(){

        if(backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Click back again to exit!", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }


    private class GetCourses extends AsyncTask<Void, Void, Void> {

        String url = "http://trepcacorporation.com/consulting/professor_courses.php?professor_id=" + tvProfessorId.getText().toString().trim();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Professor_dashboard.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    //JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    //JSONArray contacts = jsonObj.getJSONArray("contacts");


                    JSONArray courses = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < courses.length(); i++) {
                        JSONObject c = courses.getJSONObject(i);

                        String id = c.getString("cou_id");
                        String name = c.getString("cou_name");
                        String iname = (i+1) + ". " + c.getString("cou_name");
                        String code = c.getString("cou_code");
                        String credits = c.getString("cou_credits");
                        String lecturer = c.getString("cou_lecturer");


                        HashMap<String, String> course = new HashMap<>();

                        course.put("cou_id", id);
                        course.put("cou_name", name);
                        course.put("cou_iname", iname);
                        course.put("cou_code", code);
                        course.put("cou_credits", credits);
                        course.put("cou_lecturer", lecturer);

                        coursesList.add(course);
                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();


            ListAdapter adapter = new SimpleAdapter(
                    Professor_dashboard.this, coursesList,
                    R.layout.list_courses,
                    new String[]{"cou_iname"},
                    new int[]{R.id.course_name});

            lv.setAdapter(adapter);




        }

    }
}
