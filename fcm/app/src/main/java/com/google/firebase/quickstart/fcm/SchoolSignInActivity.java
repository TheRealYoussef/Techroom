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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Youssef on 07-Jul-16.
 */
public class SchoolSignInActivity extends AppCompatActivity {

    private final static String TAG;

    static {
        TAG = "SchoolSignIn";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.school_sign_in_screen);

        TextView signInAsASchool= (TextView)findViewById(R.id.school_sign_in_screen_contact_us_label);
        signInAsASchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SchoolSignInActivity.this, SchoolSignUpActivity.class);
                startActivity(i);
            }
        });

        final List<String> schools =  new ArrayList<String>();
        final List<String> countries = new ArrayList<String>();
        final Spinner spinner = (Spinner)findViewById(R.id.school_sign_in_screen_school_spinner);

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
                        ArrayAdapter<String> adapter = new SchoolsListAdapter(SchoolSignInActivity.this, schools, countries);
                        spinner.setAdapter(adapter);
                    }
                    else {
                        String message = ret.getString("Message");
                        Toast.makeText(SchoolSignInActivity.this, message,
                                Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e) {
                    Toast.makeText(SchoolSignInActivity.this, e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Toast.makeText(SchoolSignInActivity.this, e.toString(),
                        Toast.LENGTH_LONG).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(SchoolSignInActivity.this).addToRequestQueue(request);

        final TextView signIn = (TextView)findViewById(R.id.school_sign_in_screen_sign_in_label);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String school = schools.get(spinner.getSelectedItemPosition());
                final String password = ((EditText)findViewById(R.id.school_sign_in_screen_password_edit_text)).getText().toString();
                if (!Misc.checkPassword(password, SchoolSignInActivity.this))
                    return;
                final String country = countries.get(spinner.getSelectedItemPosition());
                signIn.setEnabled(false);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("SchoolName", school);
                    jsonObject.put("Country", country);
                    jsonObject.put("Password", password);
                    jsonObject.put("DeviceID", FirebaseInstanceId.getInstance().getToken());
                    JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "SchoolSignIn.php", jsonObject, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject ret = (JSONObject)response.get(0);
                                int status = ret.getInt("Status");
                                if (status == 200) {
                                    try {
                                        Misc.writeToFile(getResources().getString(R.string.school_file_name), school, SchoolSignInActivity.this);
                                        Misc.writeToFile(getResources().getString(R.string.password_file_name), password, SchoolSignInActivity.this);
                                        Misc.writeToFile(getResources().getString(R.string.country_file_name), country, SchoolSignInActivity.this);
                                        Intent i = new Intent(SchoolSignInActivity.this, SchoolProfileActivity.class);
                                        startActivity(i);
                                    }
                                    catch (IOException e) {
                                        Toast.makeText(SchoolSignInActivity.this, "Error occurred",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                                else {
                                    String message = ret.getString("Message");
                                    Toast.makeText(SchoolSignInActivity.this, message,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (JSONException e) {
                                Toast.makeText(SchoolSignInActivity.this, e.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                            signIn.setEnabled(true);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            Toast.makeText(SchoolSignInActivity.this, e.toString(),
                                    Toast.LENGTH_LONG).show();
                            signIn.setEnabled(true);
                        }
                    });
                    request.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueueSingleton.getInstance(SchoolSignInActivity.this).addToRequestQueue(request);
                }
                catch (JSONException e) {
                }
            }
        });
    }
}
