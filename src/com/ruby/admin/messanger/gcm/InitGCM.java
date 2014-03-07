package com.ruby.admin.messanger.gcm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ruby.admin.messanger.MessageActivity;
import com.ruby.admin.messanger.soap.SoapWebServiceInfo;
import com.ruby.admin.messanger.soap.SoapWebServiceUtility;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class InitGCM {

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "com.ruby.admin.messanger.registration_id";
	private static final String PROPERTY_APP_VERSION = "1.0";

	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	String SENDER_ID = "582461387259";
	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "InitGCM";

	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	Context mContext;

	String regid;
    String username;

	public void initGcmRegister(Context context, String username) {

		mContext = context;
        this.username = username;
		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.
		if (checkPlayServices(context)) {
			gcm = GoogleCloudMessaging.getInstance(context);
			regid = getRegistrationId(context);

			//if (regid.isEmpty()) {
				registerInBackground(context);
			//}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}

	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices(Context context) {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS) {
			Log.e(TAG, "Play service fail");
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return context.getSharedPreferences(CommonUtilities.SHARED_PREF_NAME,
				Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void registerInBackground(final Context context) {
		new AsyncTask() {

			@Override
			protected Object doInBackground(Object... params) {

				String msg = "";
				try {
                    if (regid.isEmpty()) {
                        if (gcm == null) {
                            gcm = GoogleCloudMessaging.getInstance(context);
                        }
                        regid = gcm.register(SENDER_ID);
                    }
					msg = "Device registered, registration ID=" + regid;
					Log.d(TAG, msg);

                    if (username != null && (regid != null && !regid.equals(""))) {
                        String envelop = String.format(
                                SoapWebServiceInfo.UPDATE_REGISTRATION_ENVELOPE, username, regid);
                        String result = SoapWebServiceUtility.callWebService(envelop,
                                SoapWebServiceInfo.UPDATE_REGISTRATION_SOAP_ACTION,
                                SoapWebServiceInfo.UPDATE_REGISTRATION_RESULT_TAG);

                        if (result != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                String flag = (String)jsonObject.get("Flag");
                                if("True".equals(flag)){
                                    storeRegistrationId(context, regid);
                                }
                            } catch (JSONException e) {
                                msg = "Error :" + e.getMessage();
                                Log.e(TAG, msg);
                            }
                        }
                    }

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.e(TAG, msg);
                    registerInBackground(context);
				}
				return msg;

			}
		}.execute(null, null, null);

	}

}
