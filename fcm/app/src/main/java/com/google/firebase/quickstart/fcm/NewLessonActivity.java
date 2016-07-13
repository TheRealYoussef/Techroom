package com.google.firebase.quickstart.fcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Youssef on 11-Jul-16.
 */
public class NewLessonActivity extends AppCompatActivity {

    private Button create;
    private Button cancel;
    private EditText title;
    private EditText description;
    private Switch canBeRecorded;
    private String school;
    private String country;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_lesson_screen);

        try {
            school = Misc.readFromFile(getResources().getString(R.string.school_file_name), this);
            country = Misc.readFromFile(getResources().getString(R.string.country_file_name), this);
            username = Misc.readFromFile(getResources().getString(R.string.username_file_name), this);

            cancel = (Button)findViewById(R.id.new_lesson_screen_cancel_button);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            title = (EditText)findViewById(R.id.new_lesson_screen_title_edit);
            description = (EditText)findViewById(R.id.new_lesson_screen_description_edit);
            canBeRecorded = (Switch)findViewById(R.id.new_lesson_screen_can_be_recorded_switch);

            create = (Button)findViewById(R.id.new_lesson_screen_create_button);
            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!title.getText().toString().equals("")) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("SchoolName", school);
                            jsonObject.put("Country", country);
                            jsonObject.put("Username", username);
                            jsonObject.put("Title", title.getText().toString());
                            jsonObject.put("Description", description.getText().toString());
                            jsonObject.put("CanBeRecorded", canBeRecorded.isChecked() ? 1 : 0);
                            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "TeacherAddLesson.php", jsonObject, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        JSONObject ret = (JSONObject)response.get(0);
                                        int status = ret.getInt("Status");
                                        if (status == 200) {
                                            int lessonID = ret.getInt("LessonID");
                                            Intent i = new Intent(NewLessonActivity.this, TeacherLessonActivity.class);
                                            i.putExtra("LessonID", lessonID);
                                            startActivity(i);
                                        } else {
                                            String message = ret.getString("Message");
                                            Toast.makeText(NewLessonActivity.this, message,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(NewLessonActivity.this, e.toString(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError e) {
                                    Toast.makeText(NewLessonActivity.this, e.toString(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                            request.setRetryPolicy(new DefaultRetryPolicy(
                                    10000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            RequestQueueSingleton.getInstance(NewLessonActivity.this).addToRequestQueue(request);
                        } catch (JSONException e) {
                        }
                    }
                }
            });
        }
        catch (IOException e) {
        }
    }
}
