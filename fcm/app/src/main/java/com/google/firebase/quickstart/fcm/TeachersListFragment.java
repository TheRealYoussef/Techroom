package com.google.firebase.quickstart.fcm;

import android.support.v4.app.Fragment;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Youssef on 08-Jul-16.
 */
public class TeachersListFragment extends Fragment {

    private ArrayList<TeacherOrStudentProfileListItemContent> arrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.list_screen, container, false);
        arrayList = new ArrayList<TeacherOrStudentProfileListItemContent>();
        try {
            String school = Misc.readFromFile(getResources().getString(R.string.school_file_name), getContext());
            String country = Misc.readFromFile(getResources().getString(R.string.country_file_name), getContext());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("SchoolName", school);
                jsonObject.put("Country", country);
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "GetTeachersList.php", jsonObject, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject ret = (JSONObject) response.get(0);
                            int status = ret.getInt("Status");
                            if (status == 200) {
                                JSONArray teachers = ret.getJSONArray("Teachers");
                                for (int i = 0; i < teachers.length(); ++i) {
                                    JSONObject teacher = (JSONObject)teachers.get(i);
                                    arrayList.add(new TeacherOrStudentProfileListItemContent(teacher.getString("Name"), teacher.getString("Image"), teacher.getInt("Orientation")));
                                }
                                ListView listView = (ListView)v.findViewById(R.id.list_screen_list);
                                listView.setAdapter(new TeacherOrStudentProfileListAdapter(v.getContext(), arrayList));
                            } else {
                                String message = ret.getString("Message");
                                Toast.makeText(getActivity(), message,
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), e.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Toast.makeText(getActivity(), e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(request);
            } catch (JSONException e) {
            }
        }
        catch (IOException e) {
            Toast.makeText(getActivity(), "Failed to load teachers list",
                    Toast.LENGTH_LONG).show();
        }
        return v;
    }
}
