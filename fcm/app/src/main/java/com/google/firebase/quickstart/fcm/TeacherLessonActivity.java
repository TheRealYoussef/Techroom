package com.google.firebase.quickstart.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Youssef on 12-Jul-16.
 */
public class TeacherLessonActivity extends AppCompatActivity {

    private int lessonID;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TeacherLessonFragmentStatePagerAdapter adapter;
    private ImageButton codeButton;
    private ImageButton updateLessonButton;
    private DontUnderstandDialog dontUnderstandDialog;
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_lesson_screen);

        lessonID = getIntent().getExtras().getInt("LessonID");

        tabLayout = (TabLayout)findViewById(R.id.teacher_lesson_screen_tab_layout);
        viewPager = (ViewPager)findViewById(R.id.teacher_lesson_screen_view_pager);
        codeButton = (ImageButton)findViewById(R.id.teacher_lesson_screen_code_button) ;
        adapter = new TeacherLessonFragmentStatePagerAdapter(getSupportFragmentManager(), lessonID);
        viewPager.setAdapter(adapter);
        dontUnderstandDialog = new DontUnderstandDialog();

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

        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeDialog codeDialog = new CodeDialog();
                codeDialog.setCancelable(false);
                Bundle arguments = new Bundle();
                arguments.putInt("LessonID", lessonID);
                codeDialog.setArguments(arguments);
                codeDialog.show(getSupportFragmentManager(), "dialog");
            }
        });

        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String id = intent.getStringExtra("ID");
                String message = intent.getStringExtra("Message");
                switch (id) {
                    case "DontUnderstand" :
                        Bundle arguments = new Bundle();
                        arguments.putString("Message", message);
                        dontUnderstandDialog.setArguments(arguments);
                        dontUnderstandDialog.show(getSupportFragmentManager(), "dialog");
                        break;
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
                new IntentFilter("TeacherLessonActivity")
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}
