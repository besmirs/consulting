package com.example.consulting;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "ConsultingPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    //Public variable in order to make access from outside)
    public static final String KEY_STUDENT_ID = "studentId";
    public static final String KEY_STUDENT_UNIQUE_DB_ID = "studentUniqueDbId";

    public static final String PROFESSOR_EMAIL = "pro_email";
    public static final String PROFESSOR_DB_ID = "pro_id";

    // Email address (make variable public to access from outside)
    //public static final String KEY_EMAIL = "email";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String studentId, String studentUniqueDbId){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing studentId
        editor.putString(KEY_STUDENT_ID, studentId);

        // Storing studentUniqueDbId
        editor.putString(KEY_STUDENT_UNIQUE_DB_ID, studentUniqueDbId);

        // commit changes
        editor.commit();
    }

    public void createLoginSessionProfessor(String professorDbId, String professor_email){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing professor email
        editor.putString(PROFESSOR_EMAIL, professor_email);

        // Storing studentUniqueDbId
        editor.putString(PROFESSOR_DB_ID, professorDbId);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, MainActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        //studentId
        user.put(KEY_STUDENT_ID, pref.getString(KEY_STUDENT_ID, null));

        //studentUniqueDbId
        user.put(KEY_STUDENT_UNIQUE_DB_ID, pref.getString(KEY_STUDENT_UNIQUE_DB_ID, null));

        // return user
        return user;
    }


    /**
     * Get stored session data
     * */
    public HashMap<String, String> getProfessorDetails(){
        HashMap<String, String> professor = new HashMap<String, String>();
        //studentId
        professor.put(PROFESSOR_EMAIL, pref.getString(PROFESSOR_EMAIL, null));

        //studentUniqueDbId
        professor.put(PROFESSOR_DB_ID, pref.getString(PROFESSOR_DB_ID, null));

        // return user
        return professor;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
