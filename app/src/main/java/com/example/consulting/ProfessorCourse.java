package com.example.consulting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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

public class ProfessorCourse extends AppCompatActivity {

    TextView available, professorName, tvCourseId, noRecords;
    private String TAG = ProfessorCourse.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;



    Intent myIntent;

    //private static
    ArrayList<HashMap<String, String>> courseConsultation = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_course);

        available = findViewById(R.id.available);
        professorName = findViewById(R.id.professor_name);
        tvCourseId = findViewById(R.id.tvCourseId);
        lv = findViewById(R.id.profListView);
        noRecords = findViewById(R.id.no_records);

        myIntent = new Intent(this, ProfessorCourseEdit.class);

        Intent intent = getIntent();
        final String course_id = intent.getStringExtra("COURSE_ID");
        final String course_name = intent.getStringExtra("COURSE_NAME");
        String professor_name = intent.getStringExtra("PROFESSOR_NAME");

        available.setText("Consultation for " + course_name);
        tvCourseId.setText(course_id);


        new GetCourseConsultation().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String[] itemResponseJson =  lv.getItemAtPosition(position).toString().split(",");

                String[] consultations = itemResponseJson[0].split("=");
                String consultation_id = consultations[1];

                myIntent.putExtra("COURSE_ID", course_id);
                myIntent.putExtra("CONSULTATION_ID", consultation_id);
                myIntent.putExtra("COURSE_NAME", course_name);
                startActivity(myIntent);
            }
        });
    }



    private class GetCourseConsultation extends AsyncTask<Void, Void, Void> {

        String url = "http://trepcacorporation.com/consulting/professor_courses_consultation.php?course_id=" +
                tvCourseId.getText().toString().trim();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ProfessorCourse.this);
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


                    JSONArray consultations = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < consultations.length(); i++) {
                        JSONObject c = consultations.getJSONObject(i);

                        String cid = c.getString("con_id");
                        String ctitle = c.getString("con_title");
                        String cdesc = c.getString("con_desc");
                        String capprove = c.getString("con_approve");

                        // tmp hash map for single contact
                        HashMap<String, String> consultation = new HashMap<>();

                        // adding each child node to HashMap key => value
                        consultation.put("con_id", cid);
                        consultation.put("con_title", ctitle);
                        consultation.put("con_desc", cdesc);
                        consultation.put("con_approve", capprove);


                        // adding contact to contact list
                        courseConsultation.add(consultation);
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


            final SimpleAdapter adapter = new SimpleAdapter(
                    ProfessorCourse.this, courseConsultation,
                    R.layout.list_consultation,
                    new String[]{"con_title", "con_desc"},
                    new int[]{R.id.name, R.id.mobile});

            if (adapter.getCount() == 0) {
                noRecords.setVisibility(View.VISIBLE);
            }

            adapter.notifyDataSetChanged();

            lv.setAdapter(adapter);

        }




    }
}
