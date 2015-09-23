package gosuke.riasayu.riholock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Calendar;

public class ScreenReceiver extends BroadcastReceiver {
    static final String TAG = "RihoLock:ScreenReceiver";

    private int mRestrictionStartHour;
    private int mRestrictionEndHour;
    private Context mContext;
    private DevicePolicyManager mDevicePolicyManager;

    public ScreenReceiver(){
        mRestrictionStartHour = RihoLockPreference.DEFAULT_RESTRICTION_START_HOUR;
        mRestrictionEndHour = RihoLockPreference.DEFAULT_RESTRICTION_END_HOUR;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(Intent.ACTION_USER_PRESENT.equals(intent.getAction())){
            Log.d(TAG, "SCREEN ON");
            mContext = context;
            mDevicePolicyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);

            if(RihoLockPreference.getRestrictEnable(mContext) == false){
                return;
            }
           if(isRestrictionStartDateOver() == true)
            {
                resetRestrictionStartDate();
                resetAccumulatedTime();
            }
            if(isRestrictedHour() == true ){
                mDevicePolicyManager.lockNow();
                Log.d(TAG, "LockNow!!");
            }
            else{
                setRepeatAlarm(context, RihoLockPreference.ALARM_INTERVAL_SEC);
            }
            RihoLockPreference.saveUnlockedTime(mContext, System.currentTimeMillis());
            registerScreenOffReceiver(context);
        }
    }

    protected boolean isRestrictionStartDateOver() {
        int start_date =  RihoLockPreference.getStartDate(mContext);
        int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        Log.d(TAG, "Start Date:" +  start_date);
        Log.d(TAG, "Today:" + today);

        if(start_date != today){
            return true;
        }
        return false;
    }
    protected void resetRestrictionStartDate(){
        Calendar calendar = Calendar.getInstance();
        RihoLockPreference.saveStartDate(mContext, calendar.get(Calendar.DAY_OF_YEAR));
    }
    protected void resetAccumulatedTime(){
        RihoLockPreference.saveAccumulatedTime(mContext, 0);
    }

    protected boolean isRestrictedHour(){
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        Log.d(TAG, "Hour:" + hour);
        if(hour >= mRestrictionStartHour || hour < mRestrictionEndHour){
            return true;
        }
        return false;
    }

    protected void setAlarm(Context context, int sec) {
        Log.d(TAG, "setAlarm:" + sec);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, sec);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), getAlarmPendingIntent(context));
    }
    protected void setRepeatAlarm(Context context, int sec){
        Log.d(TAG, "setRepeatAlarm:" + sec);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), sec*1000, getAlarmPendingIntent(context));
    }
    // PendingIntentはメンバーとして保持してもCancel時には無効になってしまうので、そのつど同じものを作成する。
    protected PendingIntent getAlarmPendingIntent(Context context){
        Intent intent = new Intent(context, AlarmReceiver.class);
       // Intent intent = new Intent(context, ScreenReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    protected void registerScreenOffReceiver(Context context){
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        context.getApplicationContext().registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d(TAG, "SCREEN_OFF");
                        if (RihoLockPreference.getUnlockedTime(context) == 0) { // Unlockされる前にここに来た。
                            return;
                        }
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        alarmManager.cancel(getAlarmPendingIntent(context));
                        RihoLockPreference.saveTotalAccumulatedTime(context);
                        RihoLockPreference.saveUnlockedTime(mContext, 0); // Unlockした時間を無効にする。

                        //                    KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);
                        //                    Log.d(TAG, "Locked?:" + keyguardManager.inKeyguardRestrictedInputMode()); OFFになった時点ではLockされていなかった。
                    }
                }, filter);
    }
}
