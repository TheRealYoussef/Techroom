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

    public static boolean checkUsername(StringBuilder username, String message, Context context) {
        StringBuilder sb = new StringBuilder(username);
        int index = sb.length() - 1;
        while (index >= 0 && (sb.charAt(index) == ' ' || sb.charAt(index) == '\t' || sb.charAt(index) == '\n' || sb.charAt(index) == '\r'))
            sb.deleteCharAt(index--);
        if (sb.length() == 0) {
            Toast.makeText(context, message,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        username.replace(0, username.length(), sb.toString());
        return true;
    }

    public static boolean checkPassword(String password, Context context) {
        if (password.equals("")) {
            Toast.makeText(context, context.getResources().getString(R.string.password_is_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static void writeToFile(String fileName, String content, Context context) throws IOException {
        FileOutputStream fOut = context.openFileOutput(fileName, context.MODE_PRIVATE);
        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        osw.write(content);
        osw.close();
    }

    public static boolean checkPasswordMatchesConfirmPassword(String password, String confirmPassword, Context context) {
        if (!password.equals(confirmPassword)){
            Toast.makeText(context, "Confirm password does not match password",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static String encrypt(String str) {
        return str;
    }

    public static String readFromFile(String fileName, Context context) throws FileNotFoundException, IOException {
        FileInputStream fis = context.openFileInput(fileName);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        return bufferedReader.readLine();
    }

    public static boolean deleteFile(String fileName, Context context) {
        File dir = context.getFilesDir();
        File file = new File(dir, fileName);
        return file.delete();
    }
}
