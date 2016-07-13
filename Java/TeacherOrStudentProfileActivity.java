package com.google.firebase.quickstart.fcm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Youssef on 09-Jul-16.
 */
public class TeacherOrStudentProfileActivity extends AppCompatActivity {

    private ArrayList<StudentLessonsListItemContent> studentLessonsList;
    private ArrayList<TeacherLessonsListItemContent> teacherLessonsList;
    private String imgPath;
    private static int RESULT_LOAD_IMG;
    private CircularImageView photo;
    private EditText name;
    private String encodedString;
    private boolean imageChanged;
    private ProgressDialog progressDialog;
    private Bitmap currentBitmap;
    private String username;
    private String school;
    private String country;
    private String password;
    private int orientation;
    private ListView listView;
    private boolean isStudent;

    static {
        RESULT_LOAD_IMG = 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_or_student_profile_screen);

        try {
            initialize();

            photo.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.no_profile_image_large, null));
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadImageFromGallery(null);
                }
            });

            loadProfile();

            ImageButton save = (ImageButton) findViewById(R.id.teacher_or_student_profile_save_profile_button);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageChanged) {
                        progressDialog.setMessage("Uploading image");
                        progressDialog.show();
                        encodeImagetoString();
                        imageChanged = false;
                    }
                    else {
                        uploadProfileToServerWithoutImage();
                    }
                }
            });

            Button newLesson = (Button) findViewById(R.id.teacher_or_student_profile_add_new_lesson_button);
            newLesson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isStudent) {
                        StudentJoinLessonDialog dialog = new StudentJoinLessonDialog();
                        dialog.setCancelable(false);
                        Bundle arguments = new Bundle();
                        arguments.putString("Username", username);
                        arguments.putString("SchoolName", school);
                        arguments.putString("Country", country);
                        dialog.setArguments(arguments);
                        dialog.show(getSupportFragmentManager(), "dialog");
                    }
                    else {
                        Intent i = new Intent(TeacherOrStudentProfileActivity.this, NewLessonActivity.class);
                        startActivity(i);
                    }
                }
            });
        }
        catch (IOException e) {
            Toast.makeText(this, "Failed to sign in", Toast.LENGTH_LONG).show();
            Misc.deleteFile(getResources().getString(R.string.school_file_name));
            Misc.deleteFile(getResources().getString(R.string.country_file_name));
            Misc.deleteFile(getResources().getString(R.string.username_file_name));
            Misc.deleteFile(getResources().getString(R.string.password_file_name));
            finish();
        }
    }

    private void loadImageFromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    // When Image is selected from Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && data != null) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);
                cursor.close();
                if (currentBitmap != null)
                    currentBitmap.recycle();
                currentBitmap = BitmapFactory.decodeFile(imgPath);
                photo.setRotation(0);
                photo.setImageBitmap(currentBitmap);
                ExifInterface exif = new ExifInterface(imgPath);
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                setOrientation(orientation);
                imageChanged = true;
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void encodeImagetoString() {new AsyncTask<Void, Void, Void>() {

        @Override
        protected Void doInBackground(Void... params) {
            BitmapFactory.Options options = null;
            options = new BitmapFactory.Options();
            options.inSampleSize = 3;
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Must compress the Image to reduce image size to make upload easy
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byte[] byte_arr = stream.toByteArray();
            // Encode Image to String
            encodedString = Base64.encodeToString(byte_arr, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void msg) {
            uploadProfileWithImageToServer();
        }
    }.execute(null, null, null);
    }

    private void uploadProfileWithImageToServer() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Username", username);
            jsonObject.put("SchoolName", school);
            jsonObject.put("Country", country);
            jsonObject.put("Password", password);
            jsonObject.put("Name", name.getText());
            jsonObject.put("Image", encodedString);
            jsonObject.put("Orientation", orientation);
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "UpdateProfileWithImage.php", jsonObject, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        JSONObject ret = (JSONObject) response.get(0);
                        int status = ret.getInt("Status");
                        if (status == 409) {
                            String message = ret.getString("Message");
                            Toast.makeText(TeacherOrStudentProfileActivity.this, message,
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(TeacherOrStudentProfileActivity.this, e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                    progressDialog.hide();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    Toast.makeText(TeacherOrStudentProfileActivity.this, e.toString(),
                            Toast.LENGTH_LONG).show();
                    progressDialog.hide();
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueueSingleton.getInstance(TeacherOrStudentProfileActivity.this).addToRequestQueue(request);
        } catch (JSONException e) {
            Toast.makeText(TeacherOrStudentProfileActivity.this, e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void uploadProfileToServerWithoutImage() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Username", username);
            jsonObject.put("SchoolName", school);
            jsonObject.put("Country", country);
            jsonObject.put("Password", password);
            jsonObject.put("Name", name.getText());
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "UpdateProfileWithoutImage.php", jsonObject, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        JSONObject ret = (JSONObject) response.get(0);
                        int status = ret.getInt("Status");
                        if (status == 409) {
                            String message = ret.getString("Message");
                            Toast.makeText(TeacherOrStudentProfileActivity.this, message,
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(TeacherOrStudentProfileActivity.this, e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    Toast.makeText(TeacherOrStudentProfileActivity.this, e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueueSingleton.getInstance(TeacherOrStudentProfileActivity.this).addToRequestQueue(request);
        } catch (JSONException e) {
            Toast.makeText(TeacherOrStudentProfileActivity.this, e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setOrientation(int orientation) {
        switch (orientation) {
            case 3:
                photo.setRotation(180);
                break;
            case 6:
                photo.setRotation(90);
                break;
            case 8:
                photo.setRotation(270);
                break;
        }
    }

    private void initialize() throws IOException {
        school = Misc.readFromFile(getResources().getString(R.string.school_file_name));
        country = Misc.readFromFile(getResources().getString(R.string.country_file_name));
        username = Misc.readFromFile(getResources().getString(R.string.username_file_name));
        password = Misc.readFromFile(getResources().getString(R.string.password_file_name));
        imageChanged = false;
        encodedString = "";
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        studentLessonsList = new ArrayList<StudentLessonsListItemContent>();
        teacherLessonsList = new ArrayList<TeacherLessonsListItemContent>();
        listView = (ListView) findViewById(R.id.teacher_or_student_profile_list);
        photo = (CircularImageView) findViewById(R.id.teacher_or_student_profile_photo);
        name = (EditText) findViewById(R.id.teacher_or_student_profile_name);
    }

    private void loadProfile() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Username", username);
            jsonObject.put("SchoolName", school);
            jsonObject.put("Country", country);
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, getResources().getString(R.string.server_domain_name) + "GetProfile.php", jsonObject, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        JSONObject ret = (JSONObject) response.get(0);
                        int status = ret.getInt("Status");
                        if (status == 200) {
                            name.setText(ret.getString("Name"));
                            String image = ret.getString("Image");
                            if (!image.equals("")) {
                                Picasso.with(TeacherOrStudentProfileActivity.this).load(getResources().getString(R.string.server_domain_name) + image).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(photo);
                                setOrientation(ret.getInt("Orientation"));
                            }
                            isStudent = (ret.getInt("Account") == 0);
                            JSONArray lessons = ret.getJSONArray("Lessons");
                            if (isStudent) {
                                for (int i = 0; i < lessons.length(); ++i) {
                                    JSONObject lesson = (JSONObject)lessons.get(i);
                                    studentLessonsList.add(new StudentLessonsListItemContent(lesson.getString("Title"), lesson.getString("Description"), lesson.getString("Teacher"), lesson.getInt("LessonID")));
                                }
                                listView.setAdapter(new StudentLessonsListAdapter(TeacherOrStudentProfileActivity.this, studentLessonsList));
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent i = new Intent(TeacherOrStudentProfileActivity.this, StudentLessonActivity.class);
                                        i.putExtra("LessonID", studentLessonsList.get(position).lessonID);
                                        startActivity(i);
                                    }
                                });
                            }
                            else {
                                for (int i = 0; i < lessons.length(); ++i) {
                                    JSONObject lesson = (JSONObject)lessons.get(i);
                                    teacherLessonsList.add(new TeacherLessonsListItemContent(lesson.getString("Title"), lesson.getString("Description"), lesson.getInt("LessonID")));
                                }
                                listView.setAdapter(new TeacherLessonsListAdapter(TeacherOrStudentProfileActivity.this, teacherLessonsList));
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent i = new Intent(TeacherOrStudentProfileActivity.this, TeacherLessonActivity.class);
                                        i.putExtra("LessonID", teacherLessonsList.get(position).lessonID);
                                        startActivity(i);
                                    }
                                });
                            }
                        } else {
                            String message = ret.getString("Message");
                            Toast.makeText(TeacherOrStudentProfileActivity.this, message,
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(TeacherOrStudentProfileActivity.this, e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    Toast.makeText(TeacherOrStudentProfileActivity.this, e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueueSingleton.getInstance(TeacherOrStudentProfileActivity.this).addToRequestQueue(request);
        } catch (JSONException e) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }
}
