package com.playgilround.schedule.client.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.activity.MainActivity;

import java.util.Map;

/**
 * 18-09-30
 * FireBase Message Service
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = FirebaseMessagingService.class.getSimpleName();

    //Message Received
    //푸쉬 메세지 수신
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived =====" + remoteMessage.toString());

//        if (remoteMessage.getData().size() > 0) {
//            sendNotification(remoteMessage.getData().get("message"));
//        }
        //data에 push Message 가 담김
        Map<String, String> data = remoteMessage.getData();

       /* String title = data.get("title");
        String message = data.get("content");

        Log.d(TAG, "FireBase data -->" + data.toString());*/

        sendNotification(data);
    }

    //푸쉬 메세지를 알림으로 표현하는 처리.
    private void sendNotification(Map<String, String> dataMap)  {


      String channelId = "channel";
      String channelName = "channel Name";

      //http://black-jin0427.tistory.com/20 오레오이상 NotificationManager 처리

      NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

      if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
          //현재 버전이 오레오 이상이면
          int importance = NotificationManager.IMPORTANCE_HIGH;

          NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);

          notifyManager.createNotificationChannel(mChannel);
      }

      NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);

      Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

      notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

      int requestId = (int) System.currentTimeMillis();

      Log.d(TAG, "requestId");

      PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

      builder.setContentTitle("Title")
              .setContentText("Content")
              .setDefaults(Notification.DEFAULT_ALL)
              .setAutoCancel(true)
              .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
              .setSmallIcon(android.R.drawable.btn_star)
              .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.add_friend))
              .setBadgeIconType(R.mipmap.add_friend)
              .setContentIntent(pendingIntent);

      notifyManager.notify(0, builder.build());

    }
}
