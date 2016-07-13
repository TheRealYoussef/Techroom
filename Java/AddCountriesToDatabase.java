package com.google.firebase.quickstart.fcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Youssef on 11-Jul-16.
 */
public class AddCountriesToDatabase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        String arr[] = getResources().getStringArray(R.array.countries_array);
        Log.d("AddCountiesToDatabase", String.valueOf(arr.length));
        JSONArray jsonArr = new JSONArray();
        for (int i = 0; i < arr.length; ++i) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("Name", arr[i]);
                jsonArr.put(obj);
            } catch (Exception e) {

            }
        }
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "CountryNames.php", jsonArr, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject ret = (JSONObject)response.get(0);
                    int status = ret.getInt("Status");
                    if (status == 200) {
                        Log.d("AddCountiesToDatabase", "Success");
                    }
                    else {
                        Log.d("AddCountiesToDatabase", "Status 409 " + ret.getString("Message"));
                    }
                }
                catch (JSONException e) {
                    Log.d("AddCountiesToDatabase", "JSONException " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Log.d("AddCountiesToDatabase", "Error Response " + e.toString());
            }
        });

        RequestQueueSingleton.getInstance(AddCountriesToDatabase.this).addToRequestQueue(request);
    }
}
