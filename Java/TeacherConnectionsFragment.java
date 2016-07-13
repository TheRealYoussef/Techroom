package com.google.firebase.quickstart.fcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
 * Created by Youssef on 12-Jul-16.
 */
public class TeacherConnectionsFragment extends Fragment {

    private ArrayList<ConnectionsListItem> arrayList;
    private ConnectionsListAdapter adapter;
    private ListView listView;
    private int lessonID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.list_screen, container, false);
        listView = (ListView)v.findViewById(R.id.list_screen_list);
        lessonID = getArguments().getInt("LessonID");
        arrayList = new ArrayList<ConnectionsListItem>();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("LessonID", lessonID);
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "GetStudentsInLesson.php", jsonObject, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        JSONObject ret = (JSONObject)response.get(0);
                        int status = ret.getInt("Status");
                        if (status == 200) {
                            JSONArray students = ret.getJSONArray("Students");
                            for (int i = 0; i < students.length(); ++i) {
                                JSONObject student = (JSONObject)students.get(i);
                                arrayList.add(new ConnectionsListItem(student.getInt("Muted") == 1, student.getInt("Connected") == 1, student.getString("Name"), student.getString("Username")));
                            }
                            adapter = new ConnectionsListAdapter(getContext(), arrayList, lessonID);
                            listView.setAdapter(adapter);
                        }
                        else {
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
        return v;
    }

    public void changeConnected(String username, boolean connected) {
        adapter.changeConnected(username, connected);
    }
}
