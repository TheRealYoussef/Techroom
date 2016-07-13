package com.google.firebase.quickstart.fcm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Youssef on 11-Jul-16.
 */
public class StudentLessonFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    int lessonID;

    public StudentLessonFragmentStatePagerAdapter(FragmentManager fm, int lessonID) {
        super(fm);
        this.lessonID = lessonID;
    }

    private StudentDiscussionFragment discussionList;

    private SlidesFragment slides;

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (discussionList == null) {
                    discussionList = new StudentDiscussionFragment();
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
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
