package gosuke.riasayu.riholock;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }
    static final String TAG = "RihoLock:AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(RihoLockPreference.getRestrictEnable(context) == false){
            return;
        }
        Log.d(TAG, "onReceive!");
        RihoLockPreference.saveTotalAccumulatedTime(context);
        if(RihoLockPreference.getElapsedSecUntilLock(context) < 0) {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            devicePolicyManager.lockNow();
            Log.d(TAG, "Screen Locked!");
        }
        sendNotification(context);
    }

    private void sendNotification(Context context) {
        // Intent の作成
/* Notificationをクリックしたときに発行されるIntent*/
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

/*        // LargeIcon の Bitmap を生成
       Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_large);
*/
        // NotificationBuilderを作成
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);


        builder.setContentIntent(contentIntent);


        // ステータスバーに表示されるテキスト
        int remainTime = (RihoLockPreference.ACCUMULATED_SEC_LIMIT - RihoLockPreference.getAccumulatedTime(context)) / 60;
        builder.setTicker( String.valueOf(remainTime));
        // アイコン
        builder.setSmallIcon(R.mipmap.ic_launcher);

        // Notificationを開いたときに表示されるタイトル
        builder.setContentTitle(context.getText(R.string.app_name));
        // Notificationを開いたときに表示されるサブタイトル
        builder.setContentText(context.getText(R.string.app_name));
/*        // Notificationを開いたときに表示されるアイコン
        builder.setLargeIcon(largeIcon);
*/
        builder.setNumber(remainTime);

        // 通知するタイミング
        builder.setWhen(System.currentTimeMillis());
/*        // 通知時の音・バイブ・ライト
        builder.setDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS);
*/
        // タップでキャンセルされる。
        builder.setAutoCancel(true);

        // NotificationManagerを取得
        NotificationManager manager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        // Notificationを作成して通知
        manager.notify(1, builder.build());
    }
}
