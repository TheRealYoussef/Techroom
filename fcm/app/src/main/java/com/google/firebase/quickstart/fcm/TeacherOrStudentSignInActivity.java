package com.google.firebase.quickstart.fcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Youssef on 06-Jul-16.
 */
public class TeacherOrStudentSignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_or_student_sign_in_screen);

        final List<String> schools =  new ArrayList<String>();
        final List<String> countries = new ArrayList<String>();
        final Spinner spinner = (Spinner)findViewById(R.id.teacher_or_student_sign_in_screen_spinner);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "GetSchools.php", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject ret = (JSONObject)response.get(0);
                    int status = ret.getInt("Status");
                    if (status == 200) {
                        JSONArray schoolsArray = ret.getJSONArray("Schools");
                        for (int i = 0; i < schoolsArray.length(); ++i) {
                            JSONObject school = (JSONObject)schoolsArray.get(i);
                            schools.add(school.getString("School"));
                            countries.add(school.getString("Country"));
                        }
                        ArrayAdapter<String> adapter = new SchoolsListAdapter(TeacherOrStudentSignInActivity.this, schools, countries);
                        spinner.setAdapter(adapter);
                    }
                    else {
                        String message = ret.getString("Message");
                        Toast.makeText(TeacherOrStudentSignInActivity.this, message,
                                Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e) {
                    Toast.makeText(TeacherOrStudentSignInActivity.this, e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Toast.makeText(TeacherOrStudentSignInActivity.this, e.toString(),
                        Toast.LENGTH_LONG).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(TeacherOrStudentSignInActivity.this).addToRequestQueue(request);

        final TextView signIn = (TextView)findViewById(R.id.teacher_or_student_sign_in_screen_sign_in_label);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder username = new StringBuilder(((EditText)findViewById(R.id.teacher_or_student_sign_in_screen_username_edit_text)).getText().toString());
                if (!Misc.checkUsername(username, getResources().getString(R.string.username_is_empty), TeacherOrStudentSignInActivity.this))
                    return;
                final String usernameEdited = username.toString();
                final String password = ((EditText)findViewById(R.id.teacher_or_student_sign_in_screen_password_edit_text)).getText().toString();
                if (!Misc.checkPassword(password, TeacherOrStudentSignInActivity.this))
                    return;
                final String school = spinner.getSelectedItem().toString();
                final String country = countries.get(spinner.getSelectedItemPosition());
                signIn.setEnabled(false);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Username", username);
                    jsonObject.put("SchoolName", school);
                    jsonObject.put("Country", country);
                    jsonObject.put("Password", password);
                    jsonObject.put("DeviceID", FirebaseInstanceId.getInstance().getToken());
                    JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "TeacherOrStudentSignIn.php", jsonObject, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject ret = (JSONObject)response.get(0);
                                int status = ret.getInt("Status");
                                if (status == 200) {
                                    try {
                                        Misc.writeToFile(getResources().getString(R.string.username_file_name), usernameEdited, TeacherOrStudentSignInActivity.this);
                                        Misc.writeToFile(getResources().getString(R.string.school_file_name), school, TeacherOrStudentSignInActivity.this);
                                        Misc.writeToFile(getResources().getString(R.string.password_file_name), password, TeacherOrStudentSignInActivity.this);
                                        Misc.writeToFile(getResources().getString(R.string.country_file_name), country, TeacherOrStudentSignInActivity.this);
                                        Intent i = new Intent(TeacherOrStudentSignInActivity.this, TeacherOrStudentProfileActivity.class);
                                        startActivity(i);
                                    }
                                    catch (IOException e) {
                                        Toast.makeText(TeacherOrStudentSignInActivity.this, "Error occurred",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                                else {
                                    String message = ret.getString("Message");
                                    Toast.makeText(TeacherOrStudentSignInActivity.this, message,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (JSONException e) {
                                Toast.makeText(TeacherOrStudentSignInActivity.this, e.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                            signIn.setEnabled(true);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            Toast.makeText(TeacherOrStudentSignInActivity.this, e.toString(),
                                    Toast.LENGTH_LONG).show();
                            signIn.setEnabled(true);
                        }
                    });
                    request.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueueSingleton.getInstance(TeacherOrStudentSignInActivity.this).addToRequestQueue(request);
                }
                catch (JSONException e) {
                }
            }
        });
    }
}
