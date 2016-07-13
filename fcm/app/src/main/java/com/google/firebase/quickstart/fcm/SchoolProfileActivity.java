package com.google.firebase.quickstart.fcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Youssef on 08-Jul-16.
 */
public class SchoolProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.school_profile_screen);

        try {
            Misc.readFromFile(getResources().getString(R.string.school_file_name), this);
            Misc.readFromFile(getResources().getString(R.string.country_file_name), this);
            Misc.readFromFile(getResources().getString(R.string.password_file_name), this);

            final TabLayout tabLayout = (TabLayout) findViewById(R.id.school_profile_screen_tab_layout);
            final ViewPager viewPager = (ViewPager) findViewById(R.id.school_profile_screen_view_pager);
            SchoolProfileFragmentStatePagerAdapter adapter = new SchoolProfileFragmentStatePagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(adapter);

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    tabLayout.getTabAt(position).select();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            ImageButton addTeacherOrStudent = (ImageButton) findViewById(R.id.school_profile_screen_add_teacher_or_student_button);
            addTeacherOrStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SchoolProfileActivity.this, AddTeacherOrStudentActivity.class);
                    startActivity(i);
                }
            });
        }
        catch (IOException e) {
            Toast.makeText(this, "Failed to sign in", Toast.LENGTH_LONG).show();
            Misc.deleteFile(getResources().getString(R.string.school_file_name), this);
            Misc.deleteFile(getResources().getString(R.string.country_file_name), this);
            Misc.deleteFile(getResources().getString(R.string.password_file_name), this);
            finish();
        }
    }
}
