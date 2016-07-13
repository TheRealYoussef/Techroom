package com.google.firebase.quickstart.fcm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Youssef on 11-Jul-16.
 */
public class StudentDiscussionListAdapter extends ArrayAdapter<StudentDiscussionItem> {

    public StudentDiscussionListAdapter(Context context, ArrayList<StudentDiscussionItem> arrayList) {
        super(context, 0, arrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StudentDiscussionItem item = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.student_discussion_list_item, parent, false);
        TextView question = (TextView)convertView.findViewById(R.id.student_discussion_list_item_question);
        TextView answer = (TextView)convertView.findViewById(R.id.student_discussion_list_item_answer);
        TextView name = (TextView)convertView.findViewById(R.id.student_discussion_list_item_name);
        question.setText(item.question);
        answer.setText(item.answer);
        name.setText(item.name);
        return convertView;
    }
}

class StudentDiscussionItem {
    public String question, answer, name;
    public int discussionID;
    public StudentDiscussionItem(String q, String a, String n, int id) {
        question = q;
        answer = a;
        name = n;
        discussionID = id;
    }
}