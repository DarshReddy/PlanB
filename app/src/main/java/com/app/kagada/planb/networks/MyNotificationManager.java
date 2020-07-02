package com.app.kagada.planb.networks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

import com.app.kagada.planb.Constants;
import com.app.kagada.planb.R;
import com.app.kagada.planb.activities.LoginActivity;
import com.app.kagada.planb.activities.MainActivity;

import java.util.Map;
import java.util.Objects;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyNotificationManager {

    private Context mCtx;
    private static MyNotificationManager mInstance;

    private MyNotificationManager(Context context) {
        mCtx = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyNotificationManager(context);
        }
        return mInstance;
    }

    public void displayNotification(Map<String, String> data) {

        String title="Title";
        String body="body";
        int pk=0;
        boolean is_female=false;


        SharedPreferences pref = mCtx.getSharedPreferences("myToken", Context.MODE_PRIVATE);
        LoginActivity.authtoken = pref.getString("token", "");
        Intent resultIntent = new Intent(mCtx, MainActivity.class);
        resultIntent.putExtra("call_frag",Integer.valueOf(Objects.requireNonNull(data.get("call_frag"))));
        if (Objects.equals(data.get("call_frag"), "2")) {
            pk = Integer.parseInt(Objects.requireNonNull(data.get("pk")));
            is_female = Boolean.parseBoolean(data.get("is_female"));
            resultIntent.putExtra("pk",pk);
            resultIntent.putExtra("is_female", is_female);
            title = "Date Invite";
            body = "Your date was accepted by "+ data.get("datemail");
        }
        else if(Objects.equals(data.get("call_frag"), "3")) {
            title = "Rate Now";
            body = "Tell us how the date was?";
            resultIntent.putExtra("rated_date",Integer.valueOf(Objects.requireNonNull(data.get("rated_date"))));
        }


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx, Constants.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_planb)
                        .setContentTitle(title)
                        .setContentText(body);


        /*
         *  Clicking on the notification will take us to this intent
         *  Right now we are using the MainActivity as this is the only activity we have in our application
         *  But for your project you can customize it as you want
         * */


        /*
         *  Now we will create a pending intent
         *  The method getActivity is taking 4 parameters
         *  All parameters are describing themselves
         *  0 is the request code (the second parameter)
         *  We can detect this code in the activity that will open by this we can get
         *  Which notification opened the activity
         * */
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 100, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);
        /*
         *  Setting the pending intent to notification builder
         * */
        mBuilder.setAutoCancel(true);

        NotificationManager mNotifyMgr =
                (NotificationManager) mCtx.getSystemService(NOTIFICATION_SERVICE);

        /*
         * The first parameter is the notification id
         * better don't give a literal here (right now we are giving a int literal)
         * because using this id we can modify it later
         * */
        if (mNotifyMgr != null) {
            mNotifyMgr.notify(1, mBuilder.build());
        }
    }
}
