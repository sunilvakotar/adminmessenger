package com.ruby.admin.messanger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import com.ruby.admin.messanger.gcm.CommonUtilities;
import com.ruby.admin.messanger.soap.SoapWebServiceInfo;
import com.ruby.admin.messanger.soap.SoapWebServiceUtility;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends Activity {
    /** Called when the activity is first created. */

    private String loginId;
    private String password;
    private int UserID;
    private String UserName;
    private ProgressDialog progressDialog;
    private String result;
    private TextView msgText;

    int mode = Activity.MODE_PRIVATE;
    SharedPreferences prefs;

    ConnectionDetector cd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        prefs = getSharedPreferences(CommonUtilities.SHARED_PREF_NAME, mode);
        boolean isLoggedIn = prefs.getBoolean(CommonUtilities.LOGGED_IN_PREF, false);
        if(isLoggedIn){
            Intent i = new Intent(LoginActivity.this, MessageActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

        cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            showAlertDialog(LoginActivity.this, "No Internet Connection",
                    "You don't have internet connection.");
        }

        final EditText loginEditText = (EditText) findViewById(R.id.loginUsername);
        final EditText passwordEditText = (EditText) findViewById(R.id.loginPassword);
        msgText = (TextView) findViewById(R.id.msgText);

        findViewById(R.id.btnSignin).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                msgText.setText(" ");

                /*Intent i = new Intent(LoginActivity.this, MessageActivity.class);
                i.putExtra("UserID", 1);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();*/
                loginId = loginEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();
                if(!loginId.equals("") && !password.equals("")){
                    new UserLogin().execute(new Object());
                }
            }
        });

    }

    private AlertDialog alertDialog;
    public void showAlertDialog(Context context, String title, String message) {
        alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        //alertDialog.setIcon(R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    class UserLogin extends AsyncTask<Object, Void, String> {

        private final static String TAG = "InventroyActivity.UserLogin";

        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute");
            progressDialog = ProgressDialog.show(LoginActivity.this,
                    "", "Loading..", true, false);
        }

        protected String doInBackground(Object... parametros) {
            Log.d(TAG, "Executando doInBackground de Login");
            if (loginId != "" && password != "") {
                String envelop = String.format(
                        SoapWebServiceInfo.LOGIN_ENVELOPE, loginId, password);
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
                    if(userId != null){
                        if(userId == 0){
                            msgText.setText("* Username Or Password Incorrect");
                        }else{
                            Intent i = new Intent(LoginActivity.this, MessageActivity.class);
                            i.putExtra("username", loginId);
                            i.putExtra("password", password);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                        }
                    }else{
                        msgText.setText("* Username Or Password Incorrect");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                msgText.setText("* Must be some problem with internet. Please try again.");
            }
            Log.d(TAG, "onPostExecute");
            progressDialog.dismiss();
        }
    }
}
