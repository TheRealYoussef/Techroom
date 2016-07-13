/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.firebase.quickstart.fcm;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().get("ID").equals("AddDiscussion")) {
            try {
                Message message = TeacherLessonActivity.newDiscussionHandler.obtainMessage();
                message.obj = new Integer(remoteMessage.getData().get("DiscussionID"));
                TeacherLessonActivity.newDiscussionHandler.sendMessage(message);
            }
            catch (ExceptionInInitializerError e) {
            }
            catch (NoClassDefFoundError e) {
            }
            catch (Exception e) {
            }
        }
        else if (remoteMessage.getData().get("ID").equals("AnswerDiscussion")) {
            try {
                Message message = StudentLessonActivity.answerDiscussionHandler.obtainMessage();
                message.obj = new Integer(remoteMessage.getData().get("DiscussionID"));
                StudentLessonActivity.answerDiscussionHandler.sendMessage(message);
            }
            catch (ExceptionInInitializerError e) {
            }
            catch (NoClassDefFoundError e) {
            }
            catch (Exception e) {
            }
        }
        else if (remoteMessage.getData().get("ID").equals("DeleteDiscussion")) {
            try {
                Message message = StudentLessonActivity.deleteDiscussionHandler.obtainMessage();
                message.obj = new Integer(remoteMessage.getData().get("DiscussionID"));
                StudentLessonActivity.deleteDiscussionHandler.sendMessage(message);
            }
            catch (ExceptionInInitializerError e) {
            }
            catch (NoClassDefFoundError e) {
            }
            catch (Exception e) {
            }
        }
        else if (remoteMessage.getData().get("ID").equals("DiscussionInvisible")) {
            try {
                Message message = StudentLessonActivity.deleteDiscussionHandler.obtainMessage();
                message.obj = new Integer(remoteMessage.getData().get("DiscussionID"));
                StudentLessonActivity.deleteDiscussionHandler.sendMessage(message);
            }
            catch (ExceptionInInitializerError e) {
            }
            catch (NoClassDefFoundError e) {
            }
            catch (Exception e) {
            }
        }
        else if (remoteMessage.getData().get("ID").equals("DiscussionVisible")) {
            try {
                Message message = StudentLessonActivity.addDiscussionHandler.obtainMessage();
                message.obj = new Integer(remoteMessage.getData().get("DiscussionID"));
                StudentLessonActivity.addDiscussionHandler.sendMessage(message);
            }
            catch (ExceptionInInitializerError e) {
            }
            catch (NoClassDefFoundError e) {
            }
            catch (Exception e) {
            }
        }
        else if (remoteMessage.getData().get("ID").equals("StudentMute")) {
            try {
                Message message = StudentLessonActivity.muteHandler.obtainMessage();
                StudentLessonActivity.muteHandler.sendMessage(message);
            }
            catch (ExceptionInInitializerError e) {
            }
            catch (NoClassDefFoundError e) {
            }
            catch (Exception e) {
            }
        }
        else if (remoteMessage.getData().get("ID").equals("StudentUnmute")) {
            try {
                Message message = StudentLessonActivity.unmuteHandler.obtainMessage();
                StudentLessonActivity.unmuteHandler.sendMessage(message);
            }
            catch (ExceptionInInitializerError e) {
            }
            catch (NoClassDefFoundError e) {
            }
            catch (Exception e) {
            }
        }
        else if (remoteMessage.getData().get("ID").equals("StudentConnected")) {
            try {
                Message message = TeacherLessonActivity.studentConnectedHandler.obtainMessage();
                message.obj = new String(remoteMessage.getData().get("Username"));
                TeacherLessonActivity.studentConnectedHandler.sendMessage(message);
            }
            catch (ExceptionInInitializerError e) {
            }
            catch (NoClassDefFoundError e) {
            }
            catch (Exception e) {
            }
        }
        else if (remoteMessage.getData().get("ID").equals("StudentDisconnected")) {
            try {
                Message message = TeacherLessonActivity.studentDisconnectedHandler.obtainMessage();
                message.obj = new String(remoteMessage.getData().get("Username"));
                TeacherLessonActivity.studentDisconnectedHandler.sendMessage(message);
            }
            catch (ExceptionInInitializerError e) {
            }
            catch (NoClassDefFoundError e) {
            }
            catch (Exception e) {
            }
        }
        else if (remoteMessage.getData().get("ID").equals("DontUnderstand")) {
            try {
                Message message = TeacherLessonActivity.dontUnderstandHandler.obtainMessage();
                message.obj = new String(remoteMessage.getData().get("Message"));
                TeacherLessonActivity.dontUnderstandHandler.sendMessage(message);
            }
            catch (ExceptionInInitializerError e) {
            }
            catch (NoClassDefFoundError e) {
            }
            catch (Exception e) {
            }
        }



        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(remoteMessage.getData().get("Message"))
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

}
