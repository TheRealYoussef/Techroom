package com.google.firebase.quickstart.fcm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Youssef on 06-Jul-16.
 */
public class ChooseEntityActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_entity_screen);

        TextView signInAsASchool = (TextView)findViewById(R.id.choose_entity_screen_sign_in_as_a_school);
        signInAsASchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseEntityActivity.this, SchoolSignInActivity.class);
                startActivity(i);
            }
        });

        TextView signInAsATeacherOrStudent = (TextView)findViewById(R.id.choose_entity_screen_sign_in_as_a_teacher_or_student);
        signInAsATeacherOrStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseEntityActivity.this, TeacherOrStudentSignInActivity.class);
                startActivity(i);
            }
        });
    }

}
