package com.google.firebase.quickstart.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import java.util.ArrayList;

/**
 * Created by Youssef on 11-Jul-16.
 */
public class TeacherDiscussionFragment extends Fragment {

    private ArrayList<TeacherDiscussionItem> discussions;
    private ListView listView;
    private TeacherDiscussionListAdapter adapter;
    private int lessonID;
    private View v;
    private BroadcastReceiver broadcastReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_screen, container, false);
        discussions = new ArrayList<TeacherDiscussionItem>();
        listView = (ListView)v.findViewById(R.id.list_screen_list);
        lessonID = getArguments().getInt("LessonID");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("LessonID", lessonID);
            jsonObject.put("DeviceID", FirebaseInstanceId.getInstance().getToken());
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "GetTeacherDiscussions.php", jsonObject, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        JSONObject ret = (JSONObject)response.get(0);
                        int status = ret.getInt("Status");
                        if (status == 200) {
                            JSONArray arr = ret.getJSONArray("Discussions");
                            for (int i = 0; i < arr.length(); ++i) {
                                JSONObject discussion = (JSONObject)arr.get(i);
                                discussions.add(new TeacherDiscussionItem(discussion.getString("Question"), discussion.getString("Answer"), discussion.getString("Name"), discussion.getInt("Visible") == 1, discussion.getInt("DiscussionID")));
                            }
                            adapter = new TeacherDiscussionListAdapter(v.getContext(), discussions);
                            listView.setAdapter(adapter);
                        }
                        else {
                            String message = ret.getString("Message");
                            Toast.makeText(v.getContext(), message,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (JSONException e) {
                        Toast.makeText(v.getContext(), e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    Toast.makeText(v.getContext(), e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueueSingleton.getInstance(v.getContext()).addToRequestQueue(request);
        }
        catch (JSONException e) {
        }

        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                int discussionID = intent.getIntExtra("DiscussionID", 0);
                String question = intent.getStringExtra("Question");
                String name = intent.getStringExtra("Name");
                adapter.add(new TeacherDiscussionItem(question, "", name, false, discussionID));
            }
        };
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver((broadcastReceiver),
                new IntentFilter("AddDiscussion")
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }
}
