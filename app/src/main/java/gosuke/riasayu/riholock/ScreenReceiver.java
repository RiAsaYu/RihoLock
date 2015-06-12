package gosuke.riasayu.riholock;

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

    }
}
