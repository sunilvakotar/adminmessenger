package com.ruby.admin.messanger;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    private Integer userId;
    private String username;
    private String password;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa");

    private ListView messageListView;
    private List<Message> messageList = new ArrayList<Message>();
    private MessageAdapter messageAdapter;
    private MessageDataSource dataSource;

    int mode = Activity.MODE_PRIVATE;
    SharedPreferences prefs;
    SharedPreferences.Editor prefEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        messageListView = (ListView) findViewById(R.id.messageList);

        prefs = getSharedPreferences(CommonUtilities.SHARED_PREF_NAME, mode);
        prefEditor = prefs.edit();

        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                CommonUtilities.DISPLAY_MESSAGE_ACTION));

        Bundle extra = getIntent().getExtras();

        if(extra != null){
            userId = extra.getInt("userId");
            username = extra.getString("username");
            password = extra.getString("password");
            if(userId > 0){
                new InitGCM().initGcmRegister(MessageActivity.this, username);
                prefEditor.putString(CommonUtilities.USERNAME_PREF, username);
                prefEditor.putString(CommonUtilities.PASSWORD_PREF, password);
                prefEditor.putBoolean(CommonUtilities.LOGGED_IN_PREF, true);
                prefEditor.commit();
            }
        }else{
            username = prefs.getString(CommonUtilities.USERNAME_PREF, null);
            new InitGCM().initGcmRegister(MessageActivity.this, username);
        }

        dataSource = new MessageDataSource(this);
        dataSource.open();
        messageList = dataSource.getAllMessagesByUser(username);
        messageAdapter = new MessageAdapter(MessageActivity.this, messageList);
        messageListView.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();

        boolean isLoggedIn = prefs.getBoolean(CommonUtilities.LOGGED_IN_PREF, false);
        if(!isLoggedIn){
            Intent i = new Intent(MessageActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_logout:
                prefEditor.remove(CommonUtilities.LOGGED_IN_PREF);
                prefEditor.commit();
                Intent i = new Intent(MessageActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
            WakeLocker.acquire(getApplicationContext());

            // Showing received message
            Message newMessage = new Message();
            newMessage.setDate(dateFormat.format(new Date()));
            newMessage.setMessage(msg);
            username = prefs.getString(CommonUtilities.USERNAME_PREF, null);
            newMessage.setUsername(username);
            //dataSource.saveMessage(newMessage);
            messageAdapter.addMessage(newMessage);
            messageAdapter.notifyDataSetChanged();
            //Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            WakeLocker.release();
        }
    };
}
