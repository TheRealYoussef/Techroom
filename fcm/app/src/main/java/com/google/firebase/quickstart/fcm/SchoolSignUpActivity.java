package com.google.firebase.quickstart.fcm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Youssef on 07-Jul-16.
 */
public class SchoolSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.school_sign_up_screen);

        TextView signUp = (TextView)findViewById(R.id.school_sign_up_screen_sign_up_label);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder username = new StringBuilder(((EditText)findViewById(R.id.school_sign_up_screen_school_edit_text)).getText().toString());
                if (!Misc.checkUsername(username, getResources().getString(R.string.school_is_empty), SchoolSignUpActivity.this))
                    return;
                String usernameEdited = username.toString();
                String password = ((EditText)findViewById(R.id.school_sign_up_screen_password_edit_text)).getText().toString();
                if (!Misc.checkPassword(password, SchoolSignUpActivity.this))
                    return;
                String confirmPassword = ((EditText)findViewById(R.id.school_sign_up_screen_confirm_password_edit_text)).getText().toString();
                String country = ((Spinner)findViewById(R.id.school_sign_up_screen_spinner)).getSelectedItem().toString();
                if (!Misc.checkPasswordMatchesConfirmPassword(password, confirmPassword, SchoolSignUpActivity.this))
                    return;
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("message/rfc822");
                Uri data = Uri.parse("mailto:?subject=" + "Techroom: School Sign Up&to=TheRealYoussef@aucegypt.edu&body=" + Misc.encrypt(usernameEdited + '\n' + password + '\n' + country));
                intent.setData(data);
                startActivity(Intent.createChooser(intent, "Send Email"));
                finish();
            }
        });
    }

}
