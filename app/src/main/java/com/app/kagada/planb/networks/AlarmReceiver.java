package com.app.kagada.planb.networks;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.app.kagada.planb.Constants;

import java.util.HashMap;
import java.util.Map;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Map<String, String> data = new HashMap<>();
        data.put("call_frag","3");
        data.put("rated_date",String.valueOf(intent.getIntExtra("rated_date",0)));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
            mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400});
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
        MyNotificationManager.getInstance(context).displayNotification(data);
    }
}
