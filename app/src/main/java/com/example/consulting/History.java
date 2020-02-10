package com.example.consulting;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class History extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView course_hidden_id, student_hidden_id, history_no_records;
    private String TAG = History.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    private LinearLayout history_listview;
    //private TabLayout tabs;

    SessionManager session;


    //private static
    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        actionBar.setTitle("Course history");

        session = new SessionManager(getApplicationContext());


        final Intent intent = getIntent();
        String course_id = intent.getStringExtra("HISTORY_COURSE_ID");
        String student_id = intent.getStringExtra("HISTORY_STUDENT_ID");



        contactList = new ArrayList<>();

        lv = findViewById(R.id.listView);
        course_hidden_id = findViewById(R.id.course_unique_id);
        student_hidden_id = findViewById(R.id.student_unique_id);
        history_no_records = findViewById(R.id.history_no_records);
        history_listview = findViewById(R.id.history_listview);


        course_hidden_id.setText(course_id);
        student_hidden_id.setText(student_id);

        new GetContacts().execute();



    }

    // Required to start MainActivity when ActionBar back icon pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // Required to start MainActivity when HW back KEY pressed
    @Override
    public void onBackPressed() {
        finish();
    }


    private class GetContacts extends AsyncTask<Void, Void, Void> {

        String url = "http://trepcacorporation.com/consulting/history.php?course_id=" +
                course_hidden_id.getText().toString().trim() + "&student_unique_id=" +
                student_hidden_id.getText().toString().trim();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(History.this);
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

                        //String id = c.getString("con_id");
                        String name = c.getString("con_title");
                        String code = c.getString("con_desc");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        //contact.put("con_id", id);
                        contact.put("con_title", name);
                        contact.put("con_desc", code);


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
                    History.this, contactList,
                    R.layout.list_consultation,
                    new String[]{"con_title", "con_desc"},
                    new int[]{R.id.name, R.id.mobile});

            if(adapter.getCount() == 0)
            {
                history_no_records.setVisibility(View.VISIBLE);
                history_listview.setVisibility(View.GONE);
            }

            lv.setAdapter(adapter);




        }

    }


}
