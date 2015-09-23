package gosuke.riasayu.riholock;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class RihoLockService extends IntentService {

    static final String TAG ="RihoLockService";

    public RihoLockService() {
        super("RihoLockService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, intent.getAction() + "command:" + intent.getExtras().getInt("command"));
        RihoLockPreference.setResetFlag(getApplicationContext());
    }

}
