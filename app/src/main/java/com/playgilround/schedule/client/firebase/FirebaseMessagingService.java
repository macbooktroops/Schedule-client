package com.playgilround.schedule.client.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.activity.FriendAssentActivity;
import com.playgilround.schedule.client.activity.LoginActivity;
import com.playgilround.schedule.client.activity.MainActivity;
import com.playgilround.schedule.client.gson.FriendAssentJsonData;
import com.playgilround.schedule.client.gson.FriendPushJsonData;
import com.playgilround.schedule.client.schedule.ScheduleJsonData;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 18-09-30
 * FireBase Message Service
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = FirebaseMessagingService.class.getSimpleName();
    private FriendAssentActivity mFriendAssentDialog;

    SharedPreferences pref;

    String assentTitle, assentMessage;

    int id;
    String name;

    /**
     * 앱이 실행중이 아닐 때 push가 온경우.
     * (앱이 실행중이 아닐 때 친구 요청이와서,
     * Notification Push Message 를 클릭 하지않고,
     * 앱 실행을 통해 친구 신청을 받으려고 할 경우)
     */
    public static boolean isChkPush = false;
    //Message Received
    //푸쉬 메세지 수신
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Log.d(TAG, "onMessageReceived =====" + remoteMessage.toString());
//        Log.d(TAG, "onMessageReceived2 =====" + remoteMessage.getData().toString());
//        Log.d(TAG, "onMessageReceived4 =====" + remoteMessage.toString());
//        Log.d(TAG, "onMessageReceived5 =====" + remoteMessage.toString());


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
    private void sendNotification(Map<String, String> dataMap) {
        Log.d(TAG, "DataMap -->" + dataMap.toString());

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

        // 최초 친구 신청 푸쉬 데이터 {type=friend, user={"friend_id":14,"name":"hyun","birth":870480000,"email":"c004112@gmail.com"}}

        //승낙 및 거절 푸쉬 데이터 {friend={"user_id":3,"is_friend_at":"2018-10-11 23:30:52","is_friend":2}, type=friend}
        String resPsh = dataMap.toString();

        /**
         * 최초 친구 신청과, 승낙 및 거절 푸쉬 구분을 위해, 앞글자 subString
         * 최초 친구 --> {typ
         * 승낙 및 거절 --> {fri
         */

        String retPush = resPsh.substring(0, 4);
        Log.d(TAG, "retPush -->" + retPush);

        //최초 친구 신청
        if (retPush.equals("{typ")) {
            Type list = new TypeToken<FriendPushJsonData>() {
            }.getType();

            FriendPushJsonData pushList = new Gson().fromJson(resPsh, list);

            String type = pushList.type;
            JsonObject user = pushList.user;

            FriendPushJsonData userList = new Gson().fromJson(user, list);

            id = userList.id;
            name = userList.name;

            Log.d(TAG, "type -->" + type + "--" + "user -->" + user + "--" + name + "--" + id);

//            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);

            Intent notificationIntent = new Intent(getApplicationContext(), LoginActivity.class);
            notificationIntent.putExtra("push", "FriendPush");
            notificationIntent.putExtra("pushName", name);
            notificationIntent.putExtra("pushId", id);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            int requestId = (int) System.currentTimeMillis();

            Log.d(TAG, "requestId");

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentTitle("친구 신청이 왔습니다.")
                    .setContentText(name + "님에게 친구신청이왔습니다.")
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(android.R.drawable.btn_star)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.add_friend))
                    .setBadgeIconType(R.mipmap.add_friend)
                    .setContentIntent(pendingIntent);

            notifyManager.notify(0, builder.build());

            //앱이 실행중일때에만, 다이얼로그 표시
            //기준은 MainActivity onDestroy 상태 기준
            if (MainActivity.isAppRunning) {
                Intent intent = new Intent(getApplicationContext(), FriendAssentActivity.class);
                intent.putExtra("PushName", name);
                intent.putExtra("PushId", id);
                startActivity(intent);
//            } else {
//                isChkPush = true;
            }
            Log.d(TAG, "MainActivity State ---> " + MainActivity.isAppRunning + "--" + isChkPush);


        //승낙 및 거절
        } else if (retPush.equals("{fri")) {
            Type list = new TypeToken<FriendAssentJsonData>() {
            }.getType();

            FriendAssentJsonData assentList = new Gson().fromJson(resPsh, list);

            JsonObject fJson = assentList.fJson;

            FriendAssentJsonData assentData = new Gson().fromJson(fJson, list);

            int id = assentData.id;
            String friendAt = assentData.friendAt; //친구 수락, 거절 누른 시간
            String name = assentData.name; //친구를 받아준 사람에 이름.
            int friend = assentData.friend; //0 친구 거부, 2 친구 완료 로 판단

            //{"user_id":1,"is_friend_at":"2018-10-12 08:11:28","is_friend":2} 친구 승낙
            //{"user_id":1,"is_friend_at":"2018-10-12 08:12:47","is_friend":0} 친구 거부
            Log.d(TAG, "Json Result -->" + fJson.toString() + "--" + id + "--" + friendAt + "--" + friend);


//            Intent notificationIntent = new Intent(getApplicationContext(), LoginActivity.class);
//            notificationIntent.putExtra("push", "FriendPush");
//            notificationIntent.putExtra("pushName", name);
//            notificationIntent.putExtra("pushId", id);
//            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

//            int requestId = (int) System.currentTimeMillis();

            Log.d(TAG, "requestId");
            if (friend == 2) {
                //친구 승낙
                assentTitle = "친구 수락";
                assentMessage = name + "님과 친구가 되셨습니다!";
            } else if (friend == 0) {
                assentTitle = "친구 거부";
                assentMessage = name + "님이 친구맺기를 거부하셨습니다.";
            } else {
                assentTitle = "Assent Error";
                assentMessage = "Assent Error";
            }

//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            Intent notificationIntent = new Intent(getApplicationContext(), LoginActivity.class);
            notificationIntent.putExtra("push", "FriendPush");
            notificationIntent.putExtra("pushName", name);
            notificationIntent.putExtra("pushId", id);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            int requestId = (int) System.currentTimeMillis();

            Log.d(TAG, "requestId");

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentTitle(assentTitle)
                    .setContentText(assentMessage)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(android.R.drawable.btn_star)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.add_friend))
                    .setBadgeIconType(R.mipmap.add_friend)
                    .setContentIntent(pendingIntent);

            notifyManager.notify(0, builder.build());

        } else if (retPush.equals("{sch")) {
          //스케줄 추가
            Type list = new TypeToken<ScheduleJsonData>() {
            }.getType();

            ScheduleJsonData scheduleList = new Gson().fromJson(resPsh, list);

            JsonObject fJson = scheduleList.sJson;

//            FriendAssentJsonData assentData = new Gson().fromJson(fJson, list);

//            int id = assentData.id;
//            String friendAt = assentData.friendAt; //친구 수락, 거절 누른 시간
//            String name = assentData.name; //친구를 받아준 사람에 이름.
//            int friend = assentData.friend; //0 친구 거부, 2 친구 완료 로 판단

            //{"user_id":1,"is_friend_at":"2018-10-12 08:11:28","is_friend":2} 친구 승낙
            //{"user_id":1,"is_friend_at":"2018-10-12 08:12:47","is_friend":0} 친구 거부
            Log.d(TAG, "Json Result -->" + fJson.toString());


//            Intent notificationIntent = new Intent(getApplicationContext(), LoginActivity.class);
//            notificationIntent.putExtra("push", "FriendPush");
//            notificationIntent.putExtra("pushName", name);
//            notificationIntent.putExtra("pushId", id);
//            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

//            int requestId = (int) System.currentTimeMillis();

            Log.d(TAG, "requestId");
            /*if (friend == 2) {
                //친구 승낙
                assentTitle = "친구 수락";
                assentMessage = name + "님과 친구가 되셨습니다!";
            } else if (friend == 0) {
                assentTitle = "친구 거부";
                assentMessage = name + "님이 친구맺기를 거부하셨습니다.";
            } else {
                assentTitle = "Assent Error";
                assentMessage = "Assent Error";
            }
*/
//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


         /*   Intent notificationIntent = new Intent(getApplicationContext(), LoginActivity.class);
            notificationIntent.putExtra("push", "FriendPush");
            notificationIntent.putExtra("pushName", name);
            notificationIntent.putExtra("pushId", id);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            int requestId = (int) System.currentTimeMillis();

            Log.d(TAG, "requestId");

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentTitle(assentTitle)
                    .setContentText(assentMessage)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(android.R.drawable.btn_star)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.add_friend))
                    .setBadgeIconType(R.mipmap.add_friend)
                    .setContentIntent(pendingIntent);

            notifyManager.notify(0, builder.build());
*/

        }




      /*  //앱을 이미 실행중일 경우 화면에 표시.
        if (isAppRunning(getApplicationContext())) {
            Log.d(TAG, "isAppRunning ? " + isAppRunning(getApplicationContext()));
            if (type.equals("friend")) {
                Log.d(TAG, "friend dialog -->" + name);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("pushData", "FriendPush");
                intent.putExtra("pushName", name);
                intent.putExtra("pushId", id);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
            *//*Looper.prepare();
            if (mFriendAssentDialog == null) {
                mFriendAssentDialog = new FriendAssentDialog(getApplicationContext(), this, name, id);
            }
            mFriendAssentDialog.show();

            Looper.loop();*//*
            }
        }*/
    }


  /*  //앱이 실행중인지 아닌지 판단
    boolean isAppRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < procInfos.size(); i++) {
            if (procInfos.get(i).processName.equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }*/

}
