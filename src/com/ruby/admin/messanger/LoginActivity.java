package com.ruby.admin.messanger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        final EditText loginEditText = (EditText) findViewById(R.id.loginUsername);
        final EditText passwordEditText = (EditText) findViewById(R.id.loginPassword);
        msgText = (TextView) findViewById(R.id.msgText);

        findViewById(R.id.btnSignin).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
				/*Intent i = new Intent(MunimjiInvoiceActivity.this, MainScreen.class);
				i.putExtra("UserID", 5);
				i.putExtra("UserName", "Vishal");
				startActivity(i);*/

                msgText.setText(" ");
                //loginId = loginEditText.getText().toString().trim();
                //password = passwordEditText.getText().toString().trim();
                loginId = "Admin";
                password = "Admin";
                if(loginId != null && password != null){
                    //new UserLogin().execute(new Object());
                    Intent i = new Intent(LoginActivity.this, MessageActivity.class);
                    i.putExtra("UserID", 1);
                    startActivity(i);
                }
            }
        });


		/*Button btnSignin = (Button)findViewById(R.id.btnSignin);



		 btnSignin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent i = new Intent(MunimjiInvoiceActivity.this, MainScreen.class);
				i.putExtra("UserID", 5);
				i.putExtra("UserName", "Vishal");

				msgText.setText(" ");
				loginId = loginEditText.getText().toString().trim();
				password = passwordEditText.getText().toString().trim();
				//new UserLogin().execute(new Object());

			}
		});*/
        /*SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date utilDate = formatter.parse("2012/08/25");
            Date currentDate = new Date();
            if(currentDate.after(utilDate)){
                finish();
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
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
            Log.d(TAG, "Executando doInBackground de EfetuaLogin");
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
                            i.putExtra("UserID", userId);
                            startActivity(i);
                        }
                    }else{
                        msgText.setText("* Username Or Password Incorrect");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "onPostExecute");
            progressDialog.dismiss();
        }
    }
}
