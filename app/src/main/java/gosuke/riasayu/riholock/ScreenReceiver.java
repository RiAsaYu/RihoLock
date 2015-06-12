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


    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_USER_PRESENT.equals(intent.getAction()))
        {
            Log.d(TAG, "Unlock!");
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            boolean b =  pref.getBoolean("restrict_enable", false);
            if(b == false)
            {
                return;
            }

            resetAccumulatedTime(context);

            if(IsRestrictedHour() == true )
            {
                DevicePolicyManager devicePolicyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                devicePolicyManager.lockNow();
            }
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

    protected void resetAccumulatedTime(Context context)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int start_date =  pref.getInt("start_date", 0);
        int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        if(start_date != today)
        {
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("accumulated_time", 0);
            editor.commit();
        }

        Log.d(TAG, "Start Date:" +  start_date);
        Log.d(TAG, "Today:" + today);
    }
}
