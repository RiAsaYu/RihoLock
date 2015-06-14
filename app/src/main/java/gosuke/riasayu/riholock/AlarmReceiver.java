package gosuke.riasayu.riholock;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        SetAccumulatedTimeToOverTime(context);
        devicePolicyManager.lockNow();
        Log.d(TAG, "Screen Locked!");
    }
    private void SetAccumulatedTimeToOverTime(Context context){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt("accumulated_time", ScreenReceiver.ACCUMULATED_SEC_LIMIT);
        editor.commit();
    }
}
