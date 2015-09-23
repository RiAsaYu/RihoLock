package gosuke.riasayu.riholock;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by GOTO Hiroshi on 2015/06/13.
 */
public class RihoLockPreference {
    static final String TAG = "RihoLockPreference";
    public static final int DEFAULT_RESTRICTION_START_HOUR =21;
    public static final int DEFAULT_RESTRICTION_END_HOUR   =7;
    public static final int ACCUMULATED_SEC_LIMIT = (60*60);
    public static final int ALARM_INTERVAL_SEC = 10;

    public static final String PREFERENCE_KEY_UNLOCKED_TIME = "unlocked_time";
    public static final String PREFERENCE_KEY_RESTRICT_ENABLE = "restrict_enable";
    public static final String PREFERENCE_KEY_START_DATE = "start_date";
    public static final String PREFERENCE_KEY_ACCUMULATED_TIME = "accumulated_time";


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
}
