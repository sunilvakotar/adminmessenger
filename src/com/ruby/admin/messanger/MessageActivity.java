package com.ruby.admin.messanger;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.ruby.admin.messanger.gcm.CommonUtilities;
import com.ruby.admin.messanger.gcm.InitGCM;

/**
 * Created by Sunil Vakotar on 2/24/14.
 */
public class MessageActivity extends Activity {

    private TextView textView;
    private int userID;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        new InitGCM().initGcmRegister(MessageActivity.this);

        textView = (TextView) findViewById(R.id.msgText);
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                CommonUtilities.DISPLAY_MESSAGE_ACTION));

        Bundle extra = getIntent().getExtras();

        if(extra != null){
            userID = extra != null ? extra.getInt("UserID") : null;
            userName = extra != null ? extra.getString("UserName") : null;

        }

        String msg = getIntent().getStringExtra("msg");
        if(msg != null){
            textView.setText(msg);
        }
    }

    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            //WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */

            // Showing received message
            textView.setText(newMessage);
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            //WakeLocker.release();
        }
    };
}
