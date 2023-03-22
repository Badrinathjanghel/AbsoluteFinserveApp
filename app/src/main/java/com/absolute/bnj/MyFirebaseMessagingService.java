package com.absolute.bnj;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessingServic";
    String click_action="";
    Intent intent;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "From: notification:-1 " + remoteMessage.getFrom());
        Log.d(TAG, "data: " + remoteMessage.getData());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

//            click_action=remoteMessage.getNotification().getClickAction();
//            if(!click_action.isEmpty()){
//                intent = new Intent(click_action);
//            }else{
//                intent = new Intent(this,   WebviewActivity.class);
//            }

            String title = remoteMessage.getData().get("title");
            String description = remoteMessage.getData().get("description");
            String url = remoteMessage.getData().get("url");
            String imgurl = remoteMessage.getData().get("image");
            String msgid = remoteMessage.getData().get("msgid");

            if(title.equals("")){
                title=getString(R.string.app_name);
            }
            Bitmap bitmapimg =getBitmapFromUri(imgurl);
//            Bitmap bitmapimg =null;
            sendNotification(title, description, bitmapimg, url,  msgid);
//            customNotification( title, description);
        }

    }

    private void sendNotification(String title,  String description, Bitmap image, String url, String msgid) {
        Log.d(TAG, "sendNotification: url: "+url);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        intent.putExtra("url", url);

        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_MUTABLE);
        }else{
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }

        //flag_one_shot

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Uri customNotificationSoundUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.notificationfinal);
//        Uri customNotificationSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://"+ getPackageName()+"/raw/notificationfinal");

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setContentInfo(description)

                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(image))/*Notification with Image*/

                        .setAutoCancel(true)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000, 1000})
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setSound(customNotificationSoundUri)
                        .setContentIntent(pendingIntent);

                        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                        bigTextStyle.setBigContentTitle(title);
                        bigTextStyle.bigText(description);
                        notificationBuilder.setStyle(bigTextStyle);

//        pendingIntent.cancel();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);




        //audio attributes for sound custom
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();


        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "AbsApp",
                    NotificationManager.IMPORTANCE_HIGH);

            channel.setSound(customNotificationSoundUri, attributes);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(Integer.parseInt(msgid) /* ID of notification */, notificationBuilder.build());
    }

    private Bitmap getBitmapFromUri(String imgurl) {
        try {
            URL url = new URL(imgurl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}
