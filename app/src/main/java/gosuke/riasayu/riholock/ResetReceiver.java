package gosuke.riasayu.riholock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ResetReceiver extends BroadcastReceiver {
    public ResetReceiver() {
    }
    static final String TAG = "RihoLock:ResetReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "ResetReceiver:Receive Alarm" );
    }
}
