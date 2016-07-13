package com.google.firebase.quickstart.fcm;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Youssef on 13-Jul-16.
 */
public class ConnectionsListAdapter extends ArrayAdapter<ConnectionsListItem> {

    private int lessonID;

    public ConnectionsListAdapter(Context context, ArrayList<ConnectionsListItem> arrayList, int lessonID) {
        super(context, 0, arrayList);
        this.lessonID = lessonID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ConnectionsListItem item = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.connectivity_list_item, parent, false);
        TextView name = (TextView)convertView.findViewById(R.id.connectivity_list_item_name);
        name.setText(item.name);
        final ImageButton mute = (ImageButton)convertView.findViewById(R.id.connectivity_list_item_mute_button);

        if (item.muted)
            mute.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), android.R.drawable.ic_lock_silent_mode, null));
        else
            mute.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), android.R.drawable.ic_lock_silent_mode_off, null));

        ImageView connectedImage = (ImageView)convertView.findViewById(R.id.connectivity_list_item_image);
        if (item.connected)
            connectedImage.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), android.R.drawable.presence_online, null));
        else
            connectedImage.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), android.R.drawable.presence_busy, null));

        item.imageView = connectedImage;

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    item.muted = !item.muted;
                    if (item.muted)
                        mute.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), android.R.drawable.ic_lock_silent_mode, null));
                    else
                        mute.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), android.R.drawable.ic_lock_silent_mode_off, null));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("LessonID", lessonID);
                    jsonObject.put("Username", item.username);
                    jsonObject.put("Muted", item.muted ? 1 : 0);
                    JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getContext().getResources().getString(R.string.server_domain_name) + "ChangeStudentMute.php", jsonObject, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject ret = (JSONObject)response.get(0);
                                int status = ret.getInt("Status");
                                if (status == 409) {
                                    item.muted = !item.muted;
                                    if (item.muted)
                                        mute.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), android.R.drawable.ic_lock_silent_mode, null));
                                    else
                                        mute.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), android.R.drawable.ic_lock_silent_mode_off, null));
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
                            item.muted = !item.muted;
                            if (item.muted)
                                mute.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), android.R.drawable.ic_lock_silent_mode, null));
                            else
                                mute.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), android.R.drawable.ic_lock_silent_mode_off, null));
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

        return convertView;
    }

    public void changeConnected(String username, boolean connected) {
        for (int i = 0; i < getCount(); ++i) {
            ConnectionsListItem item = getItem(i);
            String lowerCaseUsername1 = username.toLowerCase();
            String lowerCaseUsername2 = item.username.toLowerCase();
            if (lowerCaseUsername1.equals(lowerCaseUsername2)) {
                item.connected = connected;
                if (item.connected)
                    item.imageView.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), android.R.drawable.presence_online, null));
                else
                    item.imageView.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), android.R.drawable.presence_busy, null));
                break;
            }
        }
    }
}

class ConnectionsListItem {
    public boolean muted, connected;
    public String name;
    public String username;
    public ImageView imageView;
    public ConnectionsListItem(boolean m, boolean c, String n, String u) {
        muted = m;
        connected = c;
        name = n;
        username = u;
    }
}