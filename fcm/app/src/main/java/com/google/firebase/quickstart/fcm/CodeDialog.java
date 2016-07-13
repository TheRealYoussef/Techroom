package com.google.firebase.quickstart.fcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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

/**
 * Created by Youssef on 12-Jul-16.
 */
public class CodeDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.code_dialog, container, false);
        Button ok = (Button)view.findViewById(R.id.code_dialog_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        final TextView code = (TextView)view.findViewById(R.id.code_dialog_code);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("LessonID", getArguments().getInt("LessonID"));
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "GetLessonCode.php", jsonObject, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        JSONObject ret = (JSONObject)response.get(0);
                        int status = ret.getInt("Status");
                        if (status == 200) {
                            code.setText(ret.getString("Code"));
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
        return view;
    }
}
