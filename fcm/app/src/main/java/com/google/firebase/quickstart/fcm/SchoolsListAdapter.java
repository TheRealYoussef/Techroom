package com.google.firebase.quickstart.fcm;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Youssef on 11-Jul-16.
 */
public class SchoolsListAdapter extends ArrayAdapter<String> {

    List<String> countries;

    public SchoolsListAdapter(Context context, List<String> itemList, List<String> countries) {
        super(context, 0, itemList);
        this.countries = countries;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
        }
        TextView text = (TextView)convertView.findViewById(R.id.spinner_item_text);
        text.setText(getItem(position) + " (" + countries.get(position) + ")");
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        String item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.drop_down_spinner_item, parent, false);
        }
        CheckedTextView text = (CheckedTextView)convertView.findViewById(R.id.drop_down_spinner_item_text);
        text.setText(getItem(position) + " (" + countries.get(position) + ")");
        return convertView;
    }
}
