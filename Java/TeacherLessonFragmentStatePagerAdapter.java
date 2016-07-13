package com.google.firebase.quickstart.fcm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Youssef on 12-Jul-16.
 */
public class TeacherLessonFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    int lessonID;

    public TeacherLessonFragmentStatePagerAdapter(FragmentManager fm, int lessonID) {
        super(fm);
        this.lessonID = lessonID;
    }

    private TeacherDiscussionFragment discussionList;

    private SlidesFragment slides;

    private TeacherConnectionsFragment connectionsList;

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (discussionList == null) {
                    discussionList = new TeacherDiscussionFragment();
                    Bundle arguments = new Bundle();
                    arguments.putInt("LessonID", lessonID);
                    discussionList.setArguments(arguments);
                }
                return discussionList;
            case 1:
                if (slides == null) {
                    slides = new SlidesFragment();
                }
                return slides;
            case 2:
                if (connectionsList == null) {
                    connectionsList = new TeacherConnectionsFragment();
                    Bundle arguments = new Bundle();
                    arguments.putInt("LessonID", lessonID);
                    connectionsList.setArguments(arguments);
                }
                return connectionsList;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public void addDiscussion(int discussionID) {
        if (discussionList != null)
            discussionList.addDiscussion(discussionID);
    }

    public void changeConnected(String username, boolean connected) {
        if (connectionsList != null)
            connectionsList.changeConnected(username, connected);
    }
}