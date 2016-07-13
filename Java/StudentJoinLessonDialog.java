package com.google.firebase.quickstart.fcm;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Youssef on 09-Jul-16.
 */
public class StudentJoinLessonDialog extends DialogFragment {

    private String username;
    private String school;
    private String country;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        username = getArguments().getString("Username");
        school = getArguments().getString("SchoolName");
        country = getArguments().getString("Country");
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.student_join_lesson_dialog, container, false);
        Button join = (Button)view.findViewById(R.id.student_join_lesson_dialog_join_button);
        final EditText code = (EditText)view.findViewById(R.id.student_join_lesson_dialog_code);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("SchoolName", school);
                    jsonObject.put("Country", country);
                    jsonObject.put("Username", username);
                    jsonObject.put("Code", code.getText().toString());
                    JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "StudentAddLesson.php", jsonObject, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject ret = (JSONObject)response.get(0);
                                int status = ret.getInt("Status");
                                if (status == 200) {
                                    int lessonID = ret.getInt("LessonID");
                                    dismiss();
                                    Intent i = new Intent(view.getContext(), StudentLessonActivity.class);
                                    i.putExtra("LessonID", lessonID);
                                    startActivity(i);
                                } else {
                                    String message = ret.getString("Message");
                                    Toast.makeText(view.getContext(), message,
                                            Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(view.getContext(), e.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            Toast.makeText(view.getContext(), e.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    request.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueueSingleton.getInstance(view.getContext()).addToRequestQueue(request);
                } catch (JSONException e) {
                    Toast.makeText(view.getContext(), e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        Button cancel = (Button)view.findViewById(R.id.student_join_lesson_dialog_cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }
}