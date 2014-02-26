package com.ruby.admin.messanger.gcm;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ruby.admin.messanger.MessageActivity;
import com.ruby.admin.messanger.R;
import com.ruby.admin.messanger.bean.Message;
import com.ruby.admin.messanger.db.MessageDataSource;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GCMIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
	private static final String TAG = "GcmIntentService";
    private NotificationManager mNotificationManager;

    private MessageDataSource dataSource;
    SharedPreferences prefs;

    public GCMIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                Log.i(TAG, "Received: " + extras.toString());
                String msg = intent.getStringExtra("msg");

                dataSource = new MessageDataSource(this);
                dataSource.open();
                prefs = getSharedPreferences(CommonUtilities.SHARED_PREF_NAME, MODE_PRIVATE);

                Message newMessage = new Message();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa");
                newMessage.setDate(dateFormat.format(new Date()));
                newMessage.setMessage(msg);
                Integer userID = prefs.getInt(CommonUtilities.USER_PREF, Activity.MODE_PRIVATE);
                newMessage.setUserId(userID);
                dataSource.saveMessage(newMessage);

                CommonUtilities.displayMessage(this, msg);
                sendNotification(msg);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String title) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(GCMIntentService.this, MessageActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        notificationIntent.putExtra(CommonUtilities.EXTRA_MESSAGE, title);

        PendingIntent intent =
                PendingIntent.getActivity(GCMIntentService.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setContentTitle("tki")
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentText("New Message")
        .setContentIntent(intent);

        mBuilder.setAutoCancel(true);

        Notification notification = mBuilder.build();
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    
    
    
}