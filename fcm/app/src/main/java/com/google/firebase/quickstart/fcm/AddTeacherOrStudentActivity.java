package com.google.firebase.quickstart.fcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Youssef on 08-Jul-16.
 */
public class AddTeacherOrStudentActivity extends AppCompatActivity {

    private int quota;
    private String school;
    private String country;
    private TextView teacherQuotaLabel;
    private Spinner spinner;
    private EditText usernameEdit;
    private EditText passwordEdit;
    private EditText confirmPasswordEdit;
    private String usernameEdited;
    private String password;
    private String confirmPassword;
    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_teacher_or_student_screen);
        try {
            init();

            teacherQuotaLabel.setText("Teacher quota: 0");

            getQuota();

            TextView signUp = (TextView) findViewById(R.id.add_teacher_or_student_screen_sign_up_label);
            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addTeacherOrStudent();
                }
            });

            TextView addTeachers = (TextView) findViewById(R.id.add_teacher_or_student_screen_add_teachers);
            addTeachers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   addQuota();
                }
            });
        }
        catch (IOException e) {
            Toast.makeText(AddTeacherOrStudentActivity.this, "Error occurred",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void init() throws IOException {
        quota = 0;
        teacherQuotaLabel = (TextView) findViewById(R.id.add_teacher_or_student_screen_teacher_quota);
        school = Misc.readFromFile(getResources().getString(R.string.school_file_name), this);
        country = Misc.readFromFile(getResources().getString(R.string.country_file_name), this);
        spinner = (Spinner)findViewById(R.id.add_teacher_or_student_screen_spinner);
        usernameEdit = (EditText)findViewById(R.id.add_teacher_or_student_screen_username_edit_text);
        passwordEdit = (EditText) findViewById(R.id.add_teacher_or_student_screen_password_edit_text);
        confirmPasswordEdit = (EditText) findViewById(R.id.add_teacher_or_student_screen_confirm_password_edit_text);
    }

    private void getQuota() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("SchoolName", school);
            jsonObject.put("Country", country);
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "GetQuota.php", jsonObject, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        JSONObject ret = (JSONObject) response.get(0);
                        int status = ret.getInt("Status");
                        if (status == 200) {
                            quota = ret.getInt("Quota");
                            teacherQuotaLabel.setText("Teacher quota: " + String.valueOf(quota));
                        } else {
                            String message = ret.getString("Message");
                            Toast.makeText(AddTeacherOrStudentActivity.this, message,
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(AddTeacherOrStudentActivity.this, e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    Toast.makeText(AddTeacherOrStudentActivity.this, e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueueSingleton.getInstance(AddTeacherOrStudentActivity.this).addToRequestQueue(request);
        } catch (JSONException e) {
        }
    }

    private boolean getInfoFromUI() {
        accountType = spinner.getSelectedItem().toString();
        StringBuilder username = new StringBuilder(usernameEdit.getText().toString());
        if (!Misc.checkUsername(username, getResources().getString(R.string.username_is_empty), this))
            return false;
        usernameEdited = username.toString();
        password = passwordEdit.getText().toString();
        if (!Misc.checkPassword(password, this))
            return false;
        confirmPassword = confirmPasswordEdit.getText().toString();
        if (!Misc.checkPasswordMatchesConfirmPassword(password, confirmPassword, this))
            return false;
        if (accountType.equals("Teacher") && quota == 0) {
            Toast.makeText(AddTeacherOrStudentActivity.this, "Teacher quota is 0. Buy a larger quota to add more teachers",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void addTeacherOrStudent() {
        if (!getInfoFromUI())
            return;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("SchoolName", school);
            jsonObject.put("Country", country);
            jsonObject.put("Username", usernameEdited);
            jsonObject.put("Password", password);
            String url = "AddStudent.php";
            if (accountType.equals("Teacher"))
                url = "AddTeacher.php";
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + url, jsonObject, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        JSONObject ret = (JSONObject) response.get(0);
                        int status = ret.getInt("Status");
                        if (status == 200) {
                            usernameEdit.setText("");
                            passwordEdit.setText("");
                            confirmPasswordEdit.setText("");
                            if (accountType.equals("Teacher")) {
                                --quota;
                                teacherQuotaLabel.setText("Teacher quota: " + String.valueOf(quota));
                            }
                            Toast.makeText(AddTeacherOrStudentActivity.this, accountType + " added successfully",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            String message = ret.getString("Message");
                            Toast.makeText(AddTeacherOrStudentActivity.this, message,
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(AddTeacherOrStudentActivity.this, e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    Toast.makeText(AddTeacherOrStudentActivity.this, e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueueSingleton.getInstance(AddTeacherOrStudentActivity.this).addToRequestQueue(request);
        } catch (JSONException e) {
        }
    }

    private void addQuota() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("SchoolName", school);
            jsonObject.put("Country", country);
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "AddQuota.php", jsonObject, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        JSONObject ret = (JSONObject) response.get(0);
                        int status = ret.getInt("Status");
                        if (status == 200) {
                            quota += 20;
                            teacherQuotaLabel.setText("Teacher quota: " + String.valueOf(quota));
                        } else {
                            String message = ret.getString("Message");
                            Toast.makeText(AddTeacherOrStudentActivity.this, message,
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(AddTeacherOrStudentActivity.this, e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    Toast.makeText(AddTeacherOrStudentActivity.this, e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueueSingleton.getInstance(AddTeacherOrStudentActivity.this).addToRequestQueue(request);
        } catch (JSONException e) {
        }
    }
}
