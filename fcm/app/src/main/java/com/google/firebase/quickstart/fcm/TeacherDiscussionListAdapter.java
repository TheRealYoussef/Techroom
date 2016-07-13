package com.google.firebase.quickstart.fcm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
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
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Youssef on 11-Jul-16.
 */
public class TeacherDiscussionListAdapter extends ArrayAdapter<TeacherDiscussionItem> {

    public TeacherDiscussionListAdapter(Context context, ArrayList<TeacherDiscussionItem> arrayList) {
        super(context, 0, arrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TeacherDiscussionItem item = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.teacher_discussion_list_item, parent, false);
        TextView question = (TextView)convertView.findViewById(R.id.teacher_discussion_list_item_question);
        final TextView answer = (TextView)convertView.findViewById(R.id.teacher_discussion_list_item_answer);
        TextView name = (TextView)convertView.findViewById(R.id.teacher_discussion_list_item_name);
        ImageButton delete = (ImageButton)convertView.findViewById(R.id.teacher_discussion_list_item_delete_button);
        final Switch show = (Switch)convertView.findViewById(R.id.teacher_discussion_list_item_show_switch);
        final EditText answerEdit = (EditText)convertView.findViewById(R.id.teacher_discussion_list_item_answer_edit);
        Button answerButton = (Button)convertView.findViewById(R.id.teacher_discussion_list_item_answer_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("DiscussionID", item.discussionID);
                    TeacherDiscussionListAdapter.this.remove(item);
                    JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getContext().getResources().getString(R.string.server_domain_name) + "DeleteDiscussion.php", jsonObject, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject ret = (JSONObject)response.get(0);
                                int status = ret.getInt("Status");
                                if (status == 409) {
                                    String message = ret.getString("Message");
                                    Toast.makeText(getContext(), message,
                                            Toast.LENGTH_LONG).show();
                                }

                            }
                            catch (JSONException e) {
                                Toast.makeText(getContext(), e.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            Toast.makeText(getContext(), e.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    request.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
                }
                catch (JSONException e) {
                }
            }
        });
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("DiscussionID", item.discussionID);
                    jsonObject.put("Visible", show.isChecked() ? 1 : 0);
                    JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getContext().getResources().getString(R.string.server_domain_name) + "ChangeDiscussionVisible.php", jsonObject, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject ret = (JSONObject)response.get(0);
                                int status = ret.getInt("Status");
                                if (status == 409) {
                                    String message = ret.getString("Message");
                                    Toast.makeText(getContext(), message,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (JSONException e) {
                                Toast.makeText(getContext(), e.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            Toast.makeText(getContext(), e.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    request.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
                }
                catch (JSONException e) {
                }
            }
        });
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answerEdit.getText().toString().equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("DiscussionID", item.discussionID);
                        jsonObject.put("Answer", answerEdit.getText().toString());
                        answer.setText(answerEdit.getText().toString());
                        answerEdit.setText("");
                        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getContext().getResources().getString(R.string.server_domain_name) + "AnswerDiscussion.php", jsonObject, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    JSONObject ret = (JSONObject)response.get(0);
                                    int status = ret.getInt("Status");
                                    if (status == 409) {
                                        String message = ret.getString("Message");
                                        Toast.makeText(getContext(), message,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                                catch (JSONException e) {
                                    Toast.makeText(getContext(), e.toString(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError e) {
                                Toast.makeText(getContext(), e.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        request.setRetryPolicy(new DefaultRetryPolicy(
                                10000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
                    }
                    catch (JSONException e) {
                    }
                }
            }
        });
        question.setText(item.question);
        answer.setText(item.answer);
        name.setText(item.name);
        show.setChecked(item.visible);
        return convertView;
    }
}

class TeacherDiscussionItem {
    public String question, answer, name;
    public boolean visible;
    public int discussionID;
    public TeacherDiscussionItem(String q, String a, String n, boolean v, int id) {
        question = q;
        answer = a;
        name = n;
        visible = v;
        discussionID = id;
    }
}