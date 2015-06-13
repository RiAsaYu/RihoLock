package gosuke.riasayu.riholock;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

public class ScreenReceiver extends BroadcastReceiver {
    static final String TAG = "RihoLock:ScreenReceiver";

    public static final int DEFAULT_RESTRICTION_START_HOUR =20;
    public static final int DEFAULT_RESTRICTION_END_HOUR   =7;

    private int mRestrictionStartHour;
    private int mRestrictionEndHour;
    private  SharedPreferences mSharedPreferences;

    public ScreenReceiver(){
        mRestrictionStartHour = DEFAULT_RESTRICTION_START_HOUR;
        mRestrictionEndHour = DEFAULT_RESTRICTION_END_HOUR;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_USER_PRESENT.equals(intent.getAction())){
            Log.d(TAG, "Unlock!");
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if(isRestrictionEnable() == false){
                return;
            }
            resetRestrictionIfNeed();
            lockScreenIfNeed(context);
        }
    }

    protected boolean isRestrictionEnable(){
        boolean b =  mSharedPreferences.getBoolean("restrict_enable", false);
        if(b == false){
            return false;
        }
        return true;
    }

    protected void resetRestrictionIfNeed(){
        if(resetRestrictionDate() == true){
            resetAccumulatedTime();
        }
    }
    private boolean resetRestrictionDate(){
        int start_date =  mSharedPreferences.getInt("start_date", 0);
        int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        Log.d(TAG, "Start Date:" +  start_date);
        Log.d(TAG, "Today:" + today);

        if(start_date != today){
            return true;
        }
        return false;
    }
    private void resetAccumulatedTime(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("accumulated_time", 0);
        editor.commit();
    }

    protected void lockScreenIfNeed(Context context){
        if(isRestrictedHour() == true ){
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            devicePolicyManager.lockNow();
        }
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
}
