package gosuke.riasayu.riholock;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class RihoLockService extends Service {

    static final String TAG ="RihoLockService";

    public RihoLockService() {
    }

    @Override
    public void onCreate(){

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        boolean mode = isFlightModeEnabled(getApplicationContext());
        Log.i(TAG, "onStartCommand:" + mode);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private boolean isFlightModeEnabled(Context context) {
        boolean mode = false;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            // API 17 onwards
            mode = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
        } else {
            // API 16 and earlier.
            mode = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        }
        return mode;
    }

}
