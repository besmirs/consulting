package com.example.consulting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Dashboard extends AppCompatActivity {

    private TextView dash_studentId, dash_studentFullName;
    private String TAG = Dashboard.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;

    private long backPressedTime;

    SessionManager session;

    Intent myIntent;

    // URL to get contacts JSON
    //private static
    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();

        final String studentUniqueDB = user.get(SessionManager.KEY_STUDENT_UNIQUE_DB_ID);

        myIntent = new Intent(this, CoursesLecturers.class);


        dash_studentId = findViewById(R.id.dash_studentId);
        dash_studentFullName = findViewById(R.id.dash_studentFullName);

        Intent intent = getIntent();
        String sId = intent.getStringExtra("studentId");
        String sIdentification = intent.getStringExtra("studentIdentification");
        final String sFullName = intent.getStringExtra("studentFullName");

        dash_studentId.setText(sIdentification);
        dash_studentFullName.setText(sFullName);

        contactList = new ArrayList<>();


        lv = findViewById(R.id.listView);

        new GetContacts().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                String[] itemResponseJson =  lv.getItemAtPosition(position).toString().split(",");

                String[] course = itemResponseJson[0].split("=");
                String course_id = course[1];

                String[] coursesName = itemResponseJson[4].split("=");
                String course_name = coursesName[1];

                String[] lecturers = itemResponseJson[1].split("=");
                String lecturer = lecturers[1];

                String[] lecturers_ids = itemResponseJson[0].split("=");
                String lecturer_id = lecturers_ids[1];

                //Toast.makeText(Dashboard.this, lv.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                myIntent.putExtra("COURSE_ID", course_id);
                myIntent.putExtra("STUDENT_ID", studentUniqueDB);
                myIntent.putExtra("COURSE_NAME", course_name);
                myIntent.putExtra("LECTURER_ID", lecturer_id);
                myIntent.putExtra("LECTURER_NAME", lecturer);
                myIntent.putExtra("STUDENT_NAME", sFullName);


                startActivity(myIntent);

            }
        });


        // Define ActionBar object
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setTitle("Dashboard");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#09C6DF"));
        actionBar.setBackgroundDrawable(colorDrawable);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_navigation, menu);
        return super.onCreateOptionsMenu(menu);
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


    private class GetContacts extends AsyncTask<Void, Void, Void> {

        String url = "http://trepcacorporation.com/consulting/courses.php?cStudent=" + dash_studentId.getText().toString().trim();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Dashboard.this);
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


                    JSONArray contacts = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("cou_id");
                        String name = c.getString("cou_name");
                        String code = c.getString("cou_code");
                        String credits = c.getString("cou_credits");
                        String lecturer = c.getString("cou_lecturer");
                        //String gender = c.getString("gender");

                        // Phone node is JSON Object
                        //JSONObject phone = c.getJSONObject("phone");
                        // String mobile = phone.getString("mobile");
                        //String home = phone.getString("home");
                        //String office = phone.getString("office");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("cou_id", id);
                        contact.put("cou_name", name);
                        contact.put("cou_code", code);
                        contact.put("cou_credits", credits);
                        contact.put("cou_lecturer", lecturer);

                        // adding contact to contact list
                        contactList.add(contact);
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
                    Dashboard.this, contactList,
                    R.layout.list_item,
                    new String[]{"cou_name", "cou_code", "cou_lecturer", "cou_credits"},
                    new int[]{R.id.name, R.id.email, R.id.mobile, R.id.credits});

            lv.setAdapter(adapter);




        }

    }


}
