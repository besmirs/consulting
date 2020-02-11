package com.example.consulting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Professor_dashboard extends AppCompatActivity {

    TextView professor_name, professor_email, tvProfessorId;
    SessionManager session;
    private String TAG = Dashboard.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;

    ArrayList<HashMap<String, String>> coursesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_dashboard);

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
    }


    private class GetCourses extends AsyncTask<Void, Void, Void> {

        String url = "http://trepcacorporation.com/consulting/professor_courses.php?professor_id=" + tvProfessorId.getText().toString().trim();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Professor_dashboard.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

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


                        // tmp hash map for single contact
                        HashMap<String, String> course = new HashMap<>();

                        // adding each child node to HashMap key => value
                        course.put("cou_id", id);
                        course.put("cou_name", name);
                        course.put("cou_iname", iname);
                        course.put("cou_code", code);
                        course.put("cou_credits", credits);
                        course.put("cou_lecturer", lecturer);

                        // adding contact to contact list
                        coursesList.add(course);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
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
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
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
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    Professor_dashboard.this, coursesList,
                    R.layout.list_courses,
                    new String[]{"cou_iname"},
                    new int[]{R.id.course_name});

            lv.setAdapter(adapter);




        }

    }
}
