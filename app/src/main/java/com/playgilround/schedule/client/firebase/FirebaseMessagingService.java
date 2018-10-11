package com.playgilround.schedule.client.firebase;

import android.app.Activity;
import android.app.ActivityManager;
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
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.playgilround.calendar.widget.calendar.retrofit.APIClient;
import com.playgilround.calendar.widget.calendar.retrofit.APIInterface;
import com.playgilround.schedule.client.R;
import com.playgilround.schedule.client.activity.LoginActivity;
import com.playgilround.schedule.client.activity.MainActivity;
import com.playgilround.schedule.client.dialog.FriendAssentDialog;
import com.playgilround.schedule.client.friend.json.FriendPushJsonData;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 18-09-30
 * FireBase Message Service
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = FirebaseMessagingService.class.getSimpleName();
    private FriendAssentDialog mFriendAssentDialog;

    SharedPreferences pref;

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

        //{type=friend, user={"friend_id":14,"name":"hyun","birth":870480000,"email":"c004112@gmail.com"}}
        String resPsh = dataMap.toString();

        Type list = new TypeToken<FriendPushJsonData>() {
        }.getType();

        FriendPushJsonData pushList = new Gson().fromJson(resPsh, list);

        String type = pushList.type;
        JsonObject user = pushList.user;

        FriendPushJsonData userList = new Gson().fromJson(user, list);

        int id = userList.id;
        String name = userList.name;

        Log.d(TAG, "type -->" + type + "--" + "user -->" + user + "--" + name + "--" + id);
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

        Intent notificationIntent = new Intent(getApplicationContext(), LoginActivity.class);
        notificationIntent.putExtra("push", "FriendPush");
        notificationIntent.putExtra("pushName", name);
        notificationIntent.putExtra("pushId", id);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

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

        //앱을 이미 실행중일 경우 화면에 표시.
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
            /*Looper.prepare();
            if (mFriendAssentDialog == null) {
                mFriendAssentDialog = new FriendAssentDialog(getApplicationContext(), this, name, id);
            }
            mFriendAssentDialog.show();

            Looper.loop();*/
            }
        }
    }

    //앱이 실행중인지 아닌지 판단
    boolean isAppRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < procInfos.size(); i++) {
            if (procInfos.get(i).processName.equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }

}
