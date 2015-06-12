package gosuke.riasayu.riholock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class ScreenReceiver extends BroadcastReceiver {
    static final String TAG = "RihoLock:ScreenReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_USER_PRESENT.equals(intent.getAction()))
        {
            Log.d(TAG, "Unlock!");

            setResetAlarm(context);
            resetAccumulatedTime(context);

            if(IsRestrictedHour() == true )
            {
                DevicePolicyManager devicePolicyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                devicePolicyManager.lockNow();
            }
           // Intent service = new Intent(context, RihoLockService.class);
           // context.startService(service);
        }
    }

    protected boolean IsRestrictedHour()
    {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        Log.d(TAG, "Hour:" + hour);
        if(hour > 20 || hour < 11)
        {
            return true;
        }
        return false;
    }

    protected void setResetAlarm(Context context)
    {
        Intent i = new Intent(context, ResetReceiver.class); // ReceivedActivityを呼び出すインテントを作成
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, i, 0); // ブロードキャストを投げるPendingIntentの作成

        Calendar calendar = Calendar.getInstance(); // Calendar取得
        //calendar.setTimeInMillis(System.currentTimeMillis()); // 現在時刻を取得
        //calendar.add(Calendar.SECOND, 15); // 現時刻より15秒後を設定
        // 毎日24:00:00に累積使用時間をリセットする。
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager am = (AlarmManager)context.getSystemService(context.ALARM_SERVICE); // AlramManager取得
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender); // AlramManagerにPendingIntentを登録
    }

    protected void resetAccumulatedTime(Context context)
    {

    }
}
