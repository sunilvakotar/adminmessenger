package com.ruby.admin.messanger;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.ruby.admin.messanger.adapter.MessageAdapter;
import com.ruby.admin.messanger.bean.Message;
import com.ruby.admin.messanger.db.MessageDataSource;
import com.ruby.admin.messanger.gcm.CommonUtilities;
import com.ruby.admin.messanger.gcm.InitGCM;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Sunil Vakotar on 2/24/14.
 */
public class MessageActivity extends Activity {

    private int userID;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa");

    private ListView messageListView;
    private List<Message> messageList = new ArrayList<Message>();
    private MessageAdapter messageAdapter;
    private MessageDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        messageListView = (ListView) findViewById(R.id.messageList);

        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                CommonUtilities.DISPLAY_MESSAGE_ACTION));

        Bundle extra = getIntent().getExtras();

        if(extra != null){
            userID = extra != null ? extra.getInt("UserID") : null;
            new InitGCM().initGcmRegister(MessageActivity.this, userID);
        }

        /*Message message = new Message();
        message.setMessage("Hello This is first message form Admin.");
        message.setDate(new Date());
        messageList.add(message);*/

        dataSource = new MessageDataSource(this);
        dataSource.open();
        messageList = dataSource.getAllMessages();
        messageAdapter = new MessageAdapter(MessageActivity.this, messageList);
        messageListView.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();

        String msg = getIntent().getStringExtra("msg");
        if(msg != null){
            Message newMessage = new Message();
            newMessage.setDate(dateFormat.format(new Date()));
            newMessage.setMessage(msg);
            messageAdapter.addMessage(newMessage);
            messageAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            //WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */

            // Showing received message
            Message newMessage = new Message();
            newMessage.setDate(dateFormat.format(new Date()));
            newMessage.setMessage(msg);
            dataSource.saveMessage(newMessage);
            messageAdapter.addMessage(newMessage);
            messageAdapter.notifyDataSetChanged();
            //Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            //WakeLocker.release();
        }
    };
}
