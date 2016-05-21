package gosuke.riasayu.riholock;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

        if(RihoLockPreference.getElapsedSecUntilLock(context) <= 0 ||
                RihoLockPreference.isRestrictedHour() == true) {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            devicePolicyManager.lockNow();
            Log.d(TAG, "Screen Locked!");
        }
        sendNotification(context);
    }

    private void sendNotification(Context context) {

        // LargeIcon の Bitmap を生成
       Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        // NotificationBuilderを作成
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);

        // Notificationをクリックしたときに発行されるIntent
        builder.setContentIntent(getCommandPendingIntent(context, 1));

        /*
        Set whether this is an ongoing notification.
        Ongoing notifications differ from regular notifications in the following ways:
        Ongoing notifications are sorted above the regular notifications in the notification panel.
        Ongoing notifications do not have an 'X' close button, and are not affected by the "Clear all" button.
         */
        builder.setOngoing(true);
        /*
        Supply a PendingIntent to send when the notification is cleared by the user directly from the notification panel.
        For example, this intent is sent when the user clicks the "Clear all" button, or the individual "X" buttons on notifications.
        This intent is not sent when the application calls NotificationManager.cancel(int).
         */
        //builder.setDeleteIntent(getCommandPendingIntent(context,1));

        // Notificationが最初に現れた時にステータスバーに表示されるテキスト。残り時間
        int remainTimeMs = (RihoLockPreference.ACCUMULATED_SEC_LIMIT - RihoLockPreference.getAccumulatedTime(context));
        int remainTime =remainTimeMs / 60;
        if ((remainTimeMs % 60) > 0){ // 切り上げ：59分59秒　→　60分
            remainTime += 1;
        }

        builder.setTicker( String.valueOf(remainTime) + ":"+ String.valueOf(RihoLockPreference.getAccumulatedTime(context)));
        // アイコン
        builder.setSmallIcon(R.mipmap.ic_launcher);

        // Notificationを開いたときに表示されるタイトル
        builder.setContentTitle(context.getText(R.string.app_name));
        // Notificationを開いたときに表示されるサブタイトル。デバック情報としてAccumulatedTimeを出す。
        builder.setContentText(String.valueOf(RihoLockPreference.getAccumulatedTime(context)));
        // Notificationを開いたときに表示されるアイコン
        builder.setLargeIcon(largeIcon);

        builder.setNumber(remainTime);

        // 通知するタイミング
        builder.setWhen(System.currentTimeMillis());
/*        // 通知時の音・バイブ・ライト
        builder.setDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS);
*/
        // タップでキャンセルされない。。
        builder.setAutoCancel(false);

//        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
//        PendingIntent pendingIntent = getCommandPendingIntent(context, 1 );
//        builder.addAction(R.mipmap.ic_launcher, "COMMAND1", pendingIntent);

        // NotificationManagerを取得
        NotificationManager manager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        // Notificationを作成して通知
        manager.notify(1, builder.build());
    }
    protected PendingIntent getCommandPendingIntent(Context context, int commandId){
        Intent intent = new Intent(context, RihoLockService.class);
        intent.putExtra("command", commandId);
        return PendingIntent.getService(context, commandId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
