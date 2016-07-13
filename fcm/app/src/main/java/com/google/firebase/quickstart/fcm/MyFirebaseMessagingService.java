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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().get("ID").equals("AddDiscussion")) {
            Intent intent = new Intent("AddDiscussion");
            intent.putExtra("DiscussionID", Integer.valueOf(remoteMessage.getData().get("DiscussionID")));
            intent.putExtra("Question", remoteMessage.getData().get("Question"));
            intent.putExtra("Name", remoteMessage.getData().get("Name"));
            broadcaster.sendBroadcast(intent);
        }
        else if (remoteMessage.getData().get("ID").equals("AnswerDiscussion")) {
            Intent intent = new Intent("StudentDiscussionFragment");
            intent.putExtra("ID", "AnswerDiscussion");
            intent.putExtra("DiscussionID", Integer.valueOf(remoteMessage.getData().get("DiscussionID")));
            intent.putExtra("Answer", remoteMessage.getData().get("Answer"));
            broadcaster.sendBroadcast(intent);
        }
        else if (remoteMessage.getData().get("ID").equals("DeleteDiscussion")) {
            Intent intent = new Intent("StudentDiscussionFragment");
            intent.putExtra("ID", "DeleteDiscussion");
            intent.putExtra("DiscussionID", Integer.valueOf(remoteMessage.getData().get("DiscussionID")));
            broadcaster.sendBroadcast(intent);
        }
        else if (remoteMessage.getData().get("ID").equals("DiscussionInvisible")) {
            Intent intent = new Intent("StudentDiscussionFragment");
            intent.putExtra("ID", "DeleteDiscussion");
            intent.putExtra("DiscussionID", Integer.valueOf(remoteMessage.getData().get("DiscussionID")));
            broadcaster.sendBroadcast(intent);
        }
        else if (remoteMessage.getData().get("ID").equals("DiscussionVisible")) {
            Intent intent = new Intent("StudentDiscussionFragment");
            intent.putExtra("ID", "AddDiscussion");
            intent.putExtra("DiscussionID", Integer.valueOf(remoteMessage.getData().get("DiscussionID")));
            intent.putExtra("Question", remoteMessage.getData().get("Question"));
            intent.putExtra("Answer", remoteMessage.getData().get("Answer"));
            intent.putExtra("Name", remoteMessage.getData().get("Name"));
            broadcaster.sendBroadcast(intent);
        }
        else if (remoteMessage.getData().get("ID").equals("StudentMute")) {
            Intent intent = new Intent("StudentLessonActivity");
            intent.putExtra("ID", "Mute");
            intent.putExtra("Username", remoteMessage.getData().get("Username"));
            broadcaster.sendBroadcast(intent);
        }
        else if (remoteMessage.getData().get("ID").equals("StudentUnmute")) {
            Intent intent = new Intent("StudentLessonActivity");
            intent.putExtra("ID", "Unmute");
            intent.putExtra("Username", remoteMessage.getData().get("Username"));
            broadcaster.sendBroadcast(intent);
        }
        else if (remoteMessage.getData().get("ID").equals("StudentConnected")) {
            Intent intent = new Intent("TeacherConnectionsFragment");
            intent.putExtra("ID", "Connected");
            intent.putExtra("Username", remoteMessage.getData().get("Username"));
            broadcaster.sendBroadcast(intent);
        }
        else if (remoteMessage.getData().get("ID").equals("StudentDisconnected")) {
            Intent intent = new Intent("TeacherConnectionsFragment");
            intent.putExtra("ID", "Disconnected");
            intent.putExtra("Username", remoteMessage.getData().get("Username"));
            broadcaster.sendBroadcast(intent);
        }
        else if (remoteMessage.getData().get("ID").equals("DontUnderstand")) {
            Intent intent = new Intent("TeacherLessonActivity");
            intent.putExtra("ID", "DontUnderstand");
            intent.putExtra("Message", remoteMessage.getData().get("Message"));
            broadcaster.sendBroadcast(intent);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(remoteMessage.getData().get("Message"))
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

}
