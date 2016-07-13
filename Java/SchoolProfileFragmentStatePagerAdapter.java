package com.google.firebase.quickstart.fcm;

/**
 * Created by Youssef on 08-Jul-16.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SchoolProfileFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    public SchoolProfileFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private TeachersListFragment teachersList;

    private StudentsListFragment studentsList;

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (teachersList == null) {
                    teachersList = new TeachersListFragment();
                }
                return teachersList;
            case 1:
                if (studentsList == null) {
                    studentsList = new StudentsListFragment();
                }
                return studentsList;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}