package com.google.firebase.quickstart.fcm;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Youssef on 09-Jul-16.
 */
public class TeacherOrStudentProfileListAdapter extends ArrayAdapter<TeacherOrStudentProfileListItemContent> {

    public TeacherOrStudentProfileListAdapter(Context context, ArrayList<TeacherOrStudentProfileListItemContent> arrayList) {
        super(context, 0, arrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TeacherOrStudentProfileListItemContent item = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.teacher_or_student_profile_list_item, parent, false);
        CircularImageView photo = (CircularImageView) convertView.findViewById(R.id.teacher_or_student_profile_list_item_photo);
        if (!item.image.equals("")) {
            Picasso.with(convertView.getContext()).load(convertView.getResources().getString(R.string.server_domain_name) + item.image).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(photo);
            switch (item.orientation) {
                case 3:
                    photo.setRotation(180);
                    break;
                case 6:
                    photo.setRotation(90);
                    break;
                case 8:
                    photo.setRotation(270);
                    break;
            }
        }
        else
            photo.setImageDrawable(ResourcesCompat.getDrawable(convertView.getResources(), R.drawable.no_profile_image, null));
        TextView name = (TextView)convertView.findViewById(R.id.teacher_or_student_profile_list_item_name);
        name.setText(item.name);
        return convertView;
    }
}

class TeacherOrStudentProfileListItemContent {
    public String name, image;
    public int orientation;
    public TeacherOrStudentProfileListItemContent(String name, String image, int orientation) {
        this.name = name;
        this.image = image;
        this.orientation = orientation;
    }
}