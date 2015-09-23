package gosuke.riasayu.riholock;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by GOTO Hiroshi on 2015/06/13.
 */
public class RihoLockPreference {
    private static final String TAG = "RihoLockPreference";
    private static final int DEFAULT_RESTRICTION_START_HOUR =21;
    private static final int DEFAULT_RESTRICTION_END_HOUR   =7;
    private static final int MAX_RESET_COUNT = 10;

    public static final int ACCUMULATED_SEC_LIMIT = (60*60);

    private static final String PREFERENCE_KEY_UNLOCKED_TIME = "unlocked_time";
    private static final String PREFERENCE_KEY_RESTRICT_ENABLE = "restrict_enable";
    private static final String PREFERENCE_KEY_START_DATE = "start_date";
    private static final String PREFERENCE_KEY_ACCUMULATED_TIME = "accumulated_time";
    private static final String RESET_FLAG = "reset_flag";


    /*
    Lockをリセットするには以下の手順を行うこと。
    １、SCREEN OFF状態でMAX_RESET_COUNT回Notificationのアイコンをクリックする。
    ２、SCREEN ONする。
    SCREEN ONからOFFに移るとRESET Flagがクリアされる。
     */
    static public boolean IsResetFlagEnable(Context context){
       return  (getResetFlag(context) >= MAX_RESET_COUNT) ?  true :  false;
    }
    static public void setResetFlag(Context context){
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        int value = sharedPreference.getInt(RESET_FLAG, 0) + 1;
        saveResetFlag(context, value);
        Log.d(TAG, "setResetFlag:" + value);
    }
    static public void clearResetFlag(Context context){
        saveResetFlag(context, 0);
    }
    private  static int getResetFlag(Context context){
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreference.getInt(RESET_FLAG, 0);
    }
    private static void saveResetFlag(Context context, int value){
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putInt(RESET_FLAG, value);
        editor.commit();
    }

    static public long getUnlockedTime(Context context){
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        return  sharedPreference.getLong(PREFERENCE_KEY_UNLOCKED_TIME, 0);
    }
    static public void saveUnlockedTime(Context context, long value){
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putLong(PREFERENCE_KEY_UNLOCKED_TIME, value);
        editor.commit();
    }
    static public boolean getRestrictEnable(Context context){
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
       return true;//sharedPreference.getBoolean(PREFERENCE_KEY_RESTRICT_ENABLE, false);
    }
    static public int getStartDate(Context context){
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreference.getInt(PREFERENCE_KEY_START_DATE, 0);
    }
    static public void saveStartDate(Context context, int value){
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putInt(PREFERENCE_KEY_START_DATE, value);
        editor.commit();
    }
    static public int getAccumulatedTime(Context context){
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreference.getInt(PREFERENCE_KEY_ACCUMULATED_TIME, 0);
    }
    static public void saveAccumulatedTime(Context context, int value){
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putInt(PREFERENCE_KEY_ACCUMULATED_TIME, value);
        editor.commit();
    }
    static public void saveTotalAccumulatedTime(Context context) {
        long unlocked_ms = RihoLockPreference.getUnlockedTime(context);
        long current_ms = System.currentTimeMillis();
        int elapsed_sec = (int)(current_ms - unlocked_ms) / 1000;
        int accumulated_sec = RihoLockPreference.getAccumulatedTime(context);
        int total_accumulated_sec =  (accumulated_sec + elapsed_sec);
        Log.d(TAG, "total_accumulated_sec:" + total_accumulated_sec);
        RihoLockPreference.saveAccumulatedTime(context, total_accumulated_sec);
        RihoLockPreference.saveUnlockedTime(context, current_ms);
    }
    static public final int getElapsedSecUntilLock(Context context){
        int accumulated_time = RihoLockPreference.getAccumulatedTime(context);
        return (ACCUMULATED_SEC_LIMIT - accumulated_time);
    }

    static public boolean isRestrictedHour(){
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        Log.d(TAG, "Hour:" + hour);
        if(hour >= DEFAULT_RESTRICTION_START_HOUR || hour < DEFAULT_RESTRICTION_END_HOUR){
            return true;
        }
        return false;
    }
}
