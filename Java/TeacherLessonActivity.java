package com.google.firebase.quickstart.fcm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
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
    private static TeacherLessonFragmentStatePagerAdapter adapter;
    private ImageButton codeButton;
    private ImageButton updateLessonButton;
    public static Handler newDiscussionHandler;
    public static Handler studentConnectedHandler;
    public static Handler studentDisconnectedHandler;
    public static Handler dontUnderstandHandler;
    private static DontUnderstandDialog dontUnderstandDialog;
    private static FragmentManager fragmentManager;

    static {
        newDiscussionHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (adapter != null) {
                    adapter.addDiscussion((Integer)msg.obj);
                }
            }
        };

        studentConnectedHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (adapter != null) {
                    adapter.changeConnected((String)msg.obj, true);
                }
            }
        };

        studentDisconnectedHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (adapter != null) {
                    adapter.changeConnected((String)msg.obj, false);
                }
            }
        };

        dontUnderstandHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (dontUnderstandDialog != null) {
                    String message = (String)msg.obj;
                    Bundle arguments = new Bundle();
                    arguments.putString("Message", message);
                    dontUnderstandDialog.setArguments(arguments);
                    dontUnderstandDialog.show(fragmentManager, "dialog");
                }
            }
        };
    }

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
        fragmentManager = getSupportFragmentManager();

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
    }

}
