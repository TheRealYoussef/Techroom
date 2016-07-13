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
public class StudentDiscussionFragment extends Fragment {

    private ArrayList<StudentDiscussionItem> discussions;
    private ListView listView;
    private StudentDiscussionListAdapter adapter;
    private int lessonID;
    private View v;
    private BroadcastReceiver broadcastReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_screen, container, false);
        discussions = new ArrayList<StudentDiscussionItem>();
        listView = (ListView)v.findViewById(R.id.list_screen_list);
        lessonID = getArguments().getInt("LessonID");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("LessonID", lessonID);
            jsonObject.put("DeviceID", FirebaseInstanceId.getInstance().getToken());
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "GetStudentDiscussions.php", jsonObject, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        JSONObject ret = (JSONObject)response.get(0);
                        int status = ret.getInt("Status");
                        if (status == 200) {
                            JSONArray arr = ret.getJSONArray("Discussions");
                            for (int i = 0; i < arr.length(); ++i) {
                                JSONObject discussion = (JSONObject)arr.get(i);
                                discussions.add(new StudentDiscussionItem(discussion.getString("Question"), discussion.getString("Answer"), discussion.getString("Name"), discussion.getInt("DiscussionID")));
                            }
                            adapter = new StudentDiscussionListAdapter(v.getContext(), discussions);
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
                String id = intent.getStringExtra("ID");
                int discussionID = intent.getIntExtra("DiscussionID", 0);
                String question = intent.getStringExtra("Question");
                String answer = intent.getStringExtra("Answer");
                String name = intent.getStringExtra("Name");
                switch (id) {
                    case "AnswerDiscussion" :
                        for (int i = 0; i < discussions.size(); ++i) {
                            if (discussions.get(i).discussionID == discussionID) {
                                discussions.get(i).answer = answer;
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case "DeleteDiscussion" :
                        for (int i = 0; i < discussions.size(); ++i) {
                            if (discussions.get(i).discussionID == discussionID) {
                                adapter.remove(discussions.get(i));
                                break;
                            }
                        }
                        break;
                    case "AddDiscussion" :
                        boolean alreadyAdded = false;
                        for (int i = 0; i < adapter.getCount(); ++i) {
                            StudentDiscussionItem item = adapter.getItem(i);
                            if (item.discussionID == discussionID) {
                                alreadyAdded = true;
                                break;
                            }
                        }
                        if (!alreadyAdded)
                            adapter.add(new StudentDiscussionItem(question, answer, name, discussionID));
                        break;
                }
            }
        };
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver((broadcastReceiver),
                new IntentFilter("StudentDiscussionFragment")
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }
}
