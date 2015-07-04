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

    public static final int DEFAULT_RESTRICTION_START_HOUR =20;
    public static final int DEFAULT_RESTRICTION_END_HOUR   =7;
    public static final int ACCUMULATED_SEC_LIMIT = (60*60);

    private int mRestrictionStartHour;
    private int mRestrictionEndHour;
    private Context mContext;
    private DevicePolicyManager mDevicePolicyManager;

    public ScreenReceiver(){
        mRestrictionStartHour = DEFAULT_RESTRICTION_START_HOUR;
        mRestrictionEndHour = DEFAULT_RESTRICTION_END_HOUR;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_USER_PRESENT.equals(intent.getAction())){
            Log.d(TAG, "Unlock!");
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
            }
            else{
                setRestrictionAlarm(mContext);
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
        if(hour > mRestrictionStartHour || hour < mRestrictionEndHour){
            return true;
        }
        return false;
    }

    protected void setRestrictionAlarm(Context context){
        int elapsed_sec = getElapsedSecUntilLock();
        setAlarm(context, elapsed_sec);
    }
    protected int getElapsedSecUntilLock(){
        int accumulated_time = RihoLockPreference.getAccumulatedTime(mContext);
        return (ACCUMULATED_SEC_LIMIT - accumulated_time);
    }
    protected void setAlarm(Context context, int elapsed_sec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, elapsed_sec);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(),  getAlarmPendingIntent(context));
    }
    // PendingIntentはメンバーとして保持してもCancel時には無効になってしまうので、そのつど同じものを作成する。
    protected PendingIntent getAlarmPendingIntent(Context context){
        Intent intent = new Intent(context, AlarmReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    protected void registerScreenOffReceiver(Context context){
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        context.getApplicationContext().registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d(TAG, "SCREEN_OFF");
                        if(RihoLockPreference.getUnlockedTime(context) == 0){ // Unlockされる前にここに来た。
                            return;
                        }
                        saveTotalAccumulatedTime();
                        RihoLockPreference.saveUnlockedTime(mContext, 0); // Unlockした時間を無効にする。
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        alarmManager.cancel(getAlarmPendingIntent(context));
    //                    KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);
    //                    Log.d(TAG, "Locked?:" + keyguardManager.inKeyguardRestrictedInputMode()); OFFになった時点ではLockされていなかった。
                    }
                }, filter);
    }

    protected void saveTotalAccumulatedTime() {
        long unlocked_ms = RihoLockPreference.getUnlockedTime(mContext);
        long current_ms = System.currentTimeMillis();
        int elapsed_sec = (int)(current_ms - unlocked_ms) / 1000;
        int accumulated_sec = RihoLockPreference.getAccumulatedTime(mContext);
        int total_accumulated_sec =  (accumulated_sec + elapsed_sec);
        Log.d(TAG, "total_accumulated_sec:" + total_accumulated_sec);
        RihoLockPreference.saveAccumulatedTime(mContext, total_accumulated_sec);
    }
}
