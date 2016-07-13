package com.google.firebase.quickstart.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
public class StudentLessonActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private StudentLessonFragmentStatePagerAdapter adapter;
    private ImageButton dontUnderstand;
    private ImageButton record;
    private Button name;
    private Button anonymously;
    private EditText question;
    private boolean currentlyRecording = false;
    private String school;
    private String country;
    private String username;
    private int lessonID;
    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_lesson_screen);

        try {
            school = Misc.readFromFile(getResources().getString(R.string.school_file_name));
            country = Misc.readFromFile(getResources().getString(R.string.country_file_name));
            username = Misc.readFromFile(getResources().getString(R.string.username_file_name));

            lessonID = getIntent().getExtras().getInt("LessonID");

            tabLayout = (TabLayout) findViewById(R.id.student_lesson_screen_tab_layout);
            viewPager = (ViewPager) findViewById(R.id.student_lesson_screen_view_pager);
            adapter = new StudentLessonFragmentStatePagerAdapter(getSupportFragmentManager(), lessonID);
            viewPager.setAdapter(adapter);

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    tabLayout.getTabAt(position).select();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            /**TODO
             * Create recording
             */

            question = (EditText)findViewById(R.id.student_lesson_screen_question_edit_text);

            dontUnderstand = (ImageButton)findViewById(R.id.student_lesson_screen_dont_understand_button);
            dontUnderstand.setEnabled(false);
            dontUnderstand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Username", username);
                    jsonObject.put("LessonID", lessonID);
                    JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "DontUnderstand.php", jsonObject, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject ret = (JSONObject) response.get(0);
                                int status = ret.getInt("Status");
                                if (status == 409) {
                                    String message = ret.getString("Message");
                                    Toast.makeText(StudentLessonActivity.this, message,
                                            Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(StudentLessonActivity.this, e.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            Toast.makeText(StudentLessonActivity.this, e.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    request.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueueSingleton.getInstance(StudentLessonActivity.this).addToRequestQueue(request);
                } catch (JSONException e) {
                }
                }
            });

            name = (Button) findViewById(R.id.student_lesson_screen_name_button);
            name.setEnabled(false);
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!question.getText().toString().equals("")) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("SchoolName", school);
                            jsonObject.put("Country", country);
                            jsonObject.put("Username", username);
                            jsonObject.put("LessonID", lessonID);
                            jsonObject.put("Anonymous", 0);
                            jsonObject.put("Question", question.getText().toString());
                            question.setText("");
                            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "AddDiscussion.php", jsonObject, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        JSONObject ret = (JSONObject) response.get(0);
                                        int status = ret.getInt("Status");
                                        if (status == 409) {
                                            String message = ret.getString("Message");
                                            Toast.makeText(StudentLessonActivity.this, message,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(StudentLessonActivity.this, e.toString(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError e) {
                                    Toast.makeText(StudentLessonActivity.this, e.toString(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                            request.setRetryPolicy(new DefaultRetryPolicy(
                                    10000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            RequestQueueSingleton.getInstance(StudentLessonActivity.this).addToRequestQueue(request);
                        } catch (JSONException e) {
                        }
                    }
                }
            });

            anonymously = (Button) findViewById(R.id.student_lesson_screen_anonymously_button);
            anonymously.setEnabled(false);
            anonymously.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!question.getText().toString().equals("")) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("SchoolName", school);
                            jsonObject.put("Country", country);
                            jsonObject.put("Username", username);
                            jsonObject.put("LessonID", lessonID);
                            jsonObject.put("Anonymous", 1);
                            jsonObject.put("Question", question.getText().toString());
                            question.setText("");
                            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "AddDiscussion.php", jsonObject, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        JSONObject ret = (JSONObject) response.get(0);
                                        int status = ret.getInt("Status");
                                        if (status == 409) {
                                            String message = ret.getString("Message");
                                            Toast.makeText(StudentLessonActivity.this, message,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(StudentLessonActivity.this, e.toString(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError e) {
                                    Toast.makeText(StudentLessonActivity.this, e.toString(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                            request.setRetryPolicy(new DefaultRetryPolicy(
                                    10000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            RequestQueueSingleton.getInstance(StudentLessonActivity.this).addToRequestQueue(request);
                        } catch (JSONException e) {
                        }
                    }
                }
            });

            record = (ImageButton) findViewById(R.id.student_lesson_screen_record_button);
            //TODO if lesson can be recorded
            record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**TODO
                     * Start recording
                     * Change icon
                     */
                }
            });
            //TODO else set unclickable
            record.setEnabled(false);

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("SchoolName", school);
                jsonObject.put("Country", country);
                jsonObject.put("Username", username);
                jsonObject.put("LessonID", lessonID);
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "GetStudentLessonNameAndMuted.php", jsonObject, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject ret = (JSONObject) response.get(0);
                            int status = ret.getInt("Status");
                            if (status == 200) {
                                name.setText(ret.getString("Name"));
                                boolean muted = ret.getInt("Muted") == 1;
                                if (!muted) {
                                    name.setEnabled(true);
                                    anonymously.setEnabled(true);
                                    dontUnderstand.setEnabled(true);
                                }
                            } else {
                                String message = ret.getString("Message");
                                Toast.makeText(StudentLessonActivity.this, message,
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(StudentLessonActivity.this, e.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Toast.makeText(StudentLessonActivity.this, e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueueSingleton.getInstance(StudentLessonActivity.this).addToRequestQueue(request);
            } catch (JSONException e) {
            }
        }
        catch (IOException e) {
        }

        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String id = intent.getStringExtra("ID");
                switch (id) {
                    case "Mute" :
                        dontUnderstand.setEnabled(false);
                        name.setEnabled(false);
                        anonymously.setEnabled(false);
                        break;
                    case "Unmute" :
                        dontUnderstand.setEnabled(true);
                        name.setEnabled(true);
                        anonymously.setEnabled(true);
                        break;
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
                new IntentFilter("StudentLessonActivity")
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Username", username);
            jsonObject.put("LessonID", lessonID);
            jsonObject.put("Connected", 1);
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "ChangeStudentConnected.php", jsonObject, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        JSONObject ret = (JSONObject) response.get(0);
                        int status = ret.getInt("Status");
                        if (status == 409) {
                            String message = ret.getString("Message");
                            Toast.makeText(StudentLessonActivity.this, message,
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(StudentLessonActivity.this, e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    Toast.makeText(StudentLessonActivity.this, e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueueSingleton.getInstance(StudentLessonActivity.this).addToRequestQueue(request);
        } catch (JSONException e) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Username", username);
            jsonObject.put("LessonID", lessonID);
            jsonObject.put("Connected", 0);
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "ChangeStudentConnected.php", jsonObject, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        JSONObject ret = (JSONObject) response.get(0);
                        int status = ret.getInt("Status");
                        if (status == 409) {
                            String message = ret.getString("Message");
                            Toast.makeText(StudentLessonActivity.this, message,
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(StudentLessonActivity.this, e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    Toast.makeText(StudentLessonActivity.this, e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueueSingleton.getInstance(StudentLessonActivity.this).addToRequestQueue(request);
        } catch (JSONException e) {
        }
    }
}
