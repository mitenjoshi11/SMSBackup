package com.example.smstest.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;

import com.example.smstest.R;
import com.example.smstest.activity.MainActivity;
import com.example.smstest.activity.SMSDetailActivity;
import com.example.smstest.manager.SMSManager;
import com.example.smstest.model.SMSEntity;
import com.example.smstest.util.Utility;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    private Bundle bundle;
    private String message;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                if (pdu_Objects != null) {

                    for (Object aObject : pdu_Objects) {

                        addNotification(context, getIncomingMessage(aObject, bundle));
                    }
                    this.abortBroadcast();
                    // End of loop
                }
            } // bundle null

        }
    }

    private void addNotification(Context context, SmsMessage smsMessage) {
        String senderNo = smsMessage.getDisplayOriginatingAddress();

        message = smsMessage.getDisplayMessageBody();

        SMSEntity smsEntity = new SMSEntity();
        smsEntity.setMsg(smsMessage.getDisplayMessageBody());
        smsEntity.setAddress(Utility.getContactName(context, senderNo));
        smsEntity.setTime("" + smsMessage.getTimestampMillis());

        SMSManager.getInstance().updateValue(smsEntity);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(Utility.getContactName(context, senderNo))
                        .setContentText(message);
        builder.setAutoCancel(true);

        Intent notificationIntent = new Intent(context, SMSDetailActivity.class);
        notificationIntent.putExtra(MainActivity.SMS_ADDRESS_KEY, smsEntity.getAddress());
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }
}
