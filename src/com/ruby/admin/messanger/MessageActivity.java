package com.ruby.admin.messanger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import com.ruby.admin.messanger.adapter.MessageAdapter;
import com.ruby.admin.messanger.adapter.TitleAdapter;
import com.ruby.admin.messanger.bean.Message;
import com.ruby.admin.messanger.db.MessageDataSource;
import com.ruby.admin.messanger.gcm.CommonUtilities;
import com.ruby.admin.messanger.gcm.InitGCM;
import com.ruby.admin.messanger.soap.SoapWebServiceInfo;
import com.ruby.admin.messanger.soap.SoapWebServiceUtility;
import org.json.JSONException;
import org.json.JSONObject;

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
    private String title;

    private ListView titleListView;
    private List<String> titleList = new ArrayList<String>();
    private TitleAdapter titleAdapter;

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

       /* if(extra != null){
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
            password = prefs.getString(CommonUtilities.PASSWORD_PREF, null);
            new InitGCM().initGcmRegister(MessageActivity.this, username);
            new UserCheck().execute(new Object());
        }*/

        username = prefs.getString(CommonUtilities.USERNAME_PREF, null);
        if(extra != null){
            title = extra.getString("title");
            if(title != null){
                dataSource = new MessageDataSource(this);
                dataSource.open();
                messageList = dataSource.getAllMessagesByUser(username, title);
                messageAdapter = new MessageAdapter(MessageActivity.this, messageList);
                messageListView.setAdapter(messageAdapter);
                messageAdapter.notifyDataSetChanged();
            }
        }

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

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_logout:
                showAlertDialog(MessageActivity.this, "Logout", "If you logout then notification will not be received. Are you still want to Logout ?");
               /* prefEditor.remove(CommonUtilities.LOGGED_IN_PREF);
                prefEditor.commit();
                Intent i = new Intent(MessageActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();*/
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

    static final String TAG = "MessageActivity";

    public void showAlertDialog(final Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        //alertDialog.setIcon(R.drawable.fail);

        // Setting Yes Button
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                new LogoutTask().execute(new Object());
            }
        });

        // Setting Yes Button
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    class LogoutTask extends AsyncTask<Object, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute");
            progressDialog = ProgressDialog.show(MessageActivity.this,
                    "", "Please wait..", true, false);
        }

        @Override
        protected String doInBackground(Object... params) {

            String msg = "";
            try {
                if (username != null) {
                    String envelop = String.format(
                            SoapWebServiceInfo.UPDATE_REGISTRATION_ENVELOPE, username, "");
                    result = SoapWebServiceUtility.callWebService(envelop,
                            SoapWebServiceInfo.UPDATE_REGISTRATION_SOAP_ACTION,
                            SoapWebServiceInfo.UPDATE_REGISTRATION_RESULT_TAG);

                }

            } catch (Exception ex) {
                msg = "Error :" + ex.getMessage();
                Log.e(TAG, msg);
            }
            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String flag = (String)jsonObject.get("Flag");
                    if("True".equals(flag)){
                        progressDialog.dismiss();
                        prefEditor.remove(CommonUtilities.LOGGED_IN_PREF);
                        prefEditor.commit();
                        Intent i = new Intent(MessageActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error :" + e.getMessage());
                }
            }
            Log.d(TAG, "onPostExecute");
            progressDialog.dismiss();
        }
    }

    private ProgressDialog progressDialog;
    private String result;
    class UserCheck extends AsyncTask<Object, Void, String> {

        private final static String TAG = "MessageActivity.UserCheck";

        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute");
            /*progressDialog = ProgressDialog.show(MessageActivity.this,
                    "", "Loading..", true, false);*/
        }

        protected String doInBackground(Object... parametros) {
            Log.d(TAG, "doInBackground for Login check");
            if (username != null && password != null) {
                String envelop = String.format(
                        SoapWebServiceInfo.LOGIN_ENVELOPE, username, password);
                result = SoapWebServiceUtility.callWebService(envelop,
                        SoapWebServiceInfo.LOGIN_SOAP_ACTION,
                        SoapWebServiceInfo.LOGIN_RESULT_TAG);
            }

            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Integer userId = (Integer)jsonObject.get("UserId");
                    boolean isUserExist = true;
                    if(userId != null){
                        if(userId == 0){
                            isUserExist = false;
                        }
                    }else{
                        isUserExist = false;
                    }
                    if(!isUserExist){
                        new LogoutTask().execute(new Object());
                        /*prefEditor.remove(CommonUtilities.LOGGED_IN_PREF);
                        prefEditor.commit();
                        Intent i = new Intent(MessageActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "onPostExecute");
            /*progressDialog.dismiss();*/
        }
    }
}
