package com.google.firebase.quickstart.fcm;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Youssef on 07-Jul-16.
 */
public class Misc {

    public static AppCompatActivity activity;

    public static boolean checkUsername(StringBuilder username, String message) {
        StringBuilder sb = new StringBuilder(username);
        int index = sb.length() - 1;
        while (index >= 0 && (sb.charAt(index) == ' ' || sb.charAt(index) == '\t' || sb.charAt(index) == '\n' || sb.charAt(index) == '\r'))
            sb.deleteCharAt(index--);
        if (sb.length() == 0) {
            Toast.makeText(activity, message,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        username.replace(0, username.length(), sb.toString());
        return true;
    }

    public static boolean checkPassword(String password) {
        if (password.equals("")) {
            Toast.makeText(activity, activity.getResources().getString(R.string.password_is_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static void writeToFile(String fileName, String content) throws IOException {
        FileOutputStream fOut = activity.openFileOutput(fileName, activity.MODE_PRIVATE);
        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        osw.write(content);
        osw.close();
    }

    public static boolean checkPasswordMatchesConfirmPassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)){
            Toast.makeText(activity, "Confirm password does not match password",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static String encrypt(String str) {
        return str;
    }

    public static String readFromFile(String fileName) throws FileNotFoundException, IOException {
        FileInputStream fis = activity.openFileInput(fileName);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        return bufferedReader.readLine();
    }

    public static boolean deleteFile(String fileName) {
        File dir = activity.getFilesDir();
        File file = new File(dir, fileName);
        return file.delete();
    }
}
