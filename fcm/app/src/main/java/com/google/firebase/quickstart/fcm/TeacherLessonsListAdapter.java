package com.google.firebase.quickstart.fcm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Youssef on 09-Jul-16.
 */
public class TeacherLessonsListAdapter extends ArrayAdapter<TeacherLessonsListItemContent> {

    public TeacherLessonsListAdapter(Context context, ArrayList<TeacherLessonsListItemContent> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TeacherLessonsListItemContent item = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.teacher_lessons_list_item, parent, false);
        TextView title = (TextView)convertView.findViewById(R.id.teacher_lessons_list_item_name);
        TextView description = (TextView)convertView.findViewById(R.id.teacher_lessons_list_item_description);
        title.setText(item.title);
        description.setText(item.description);
        return convertView;
    }
}

class TeacherLessonsListItemContent {
    public String title, description;
    public int lessonID;
    public TeacherLessonsListItemContent(String title, String description, int lessonID) {
        this.title = title;
        this.description = description;
        this.lessonID = lessonID;
    }
}