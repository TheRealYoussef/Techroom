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
public class StudentLessonsListAdapter extends ArrayAdapter<StudentLessonsListItemContent> {

    public StudentLessonsListAdapter(Context context, ArrayList<StudentLessonsListItemContent> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StudentLessonsListItemContent item = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.student_lessons_list_item, parent, false);
        TextView title = (TextView)convertView.findViewById(R.id.student_lessons_list_item_name);
        TextView description = (TextView)convertView.findViewById(R.id.student_lessons_list_item_description);
        TextView teacher = (TextView)convertView.findViewById(R.id.student_lessons_list_item_teacher);
        title.setText(item.title);
        description.setText(item.description);
        teacher.setText(item.teacher);
        return convertView;
    }
}

class StudentLessonsListItemContent {
    public String title, description, teacher;
    public int lessonID;
    public StudentLessonsListItemContent(String title, String description, String teacher, int lessonID) {
        this.title = title;
        this.description = description;
        this.teacher = teacher;
        this.lessonID = lessonID;
    }
}