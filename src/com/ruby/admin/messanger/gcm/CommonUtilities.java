package com.ruby.admin.messanger.gcm;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Sunil Vakotar on 2/24/14.
 */
public final class CommonUtilities {

    // give your server registration url here
    static final String SERVER_URL = "http://10.0.2.2/gcm_server_php/register.php";

    // Google project id
    static final String SENDER_ID = "903913289319";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "AndroidHive GCM";

    public static final String DISPLAY_MESSAGE_ACTION =
            "com.gcmclienttest.gcm.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "msg";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}