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
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ruby.admin.messanger.LoginActivity;
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
        //Toast.makeText(GCMIntentService.this, "On Handle Intent", Toast.LENGTH_LONG).show();
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
                sendNotification("Error","Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Error", "Deleted messages on server: " +
                        extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                Log.i(TAG, "Received: " + extras.toString());
                String msg = intent.getStringExtra("msg");
                String title = intent.getStringExtra("title");

                dataSource = new MessageDataSource(this);
                dataSource.open();
                prefs = getSharedPreferences(CommonUtilities.SHARED_PREF_NAME, MODE_PRIVATE);

                Message newMessage = new Message();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa");
                newMessage.setDate(dateFormat.format(new Date()));
                newMessage.setMessage(msg);
                newMessage.setTitle(title);
                String username = prefs.getString(CommonUtilities.USERNAME_PREF, null);
                newMessage.setUsername(username);
                dataSource.saveMessage(newMessage);

                CommonUtilities.displayMessage(this, msg);
                sendNotification(title, msg);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String title, String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        //Notification notification = new Notification(icon, message, when);

        Intent notificationIntent = new Intent(GCMIntentService.this, LoginActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //notificationIntent.putExtra(CommonUtilities.EXTRA_MESSAGE, title);

        PendingIntent intent =
                PendingIntent.getActivity(GCMIntentService.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setContentTitle(getString(R.string.app_name))
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentText(title)
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