package gosuke.riasayu.riholock;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class RihoLockService extends Service {

    static final int RESULT_ENABLE = 1;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mDeviceAdmin;

    public RihoLockService() {
    }

    @Override
    public void onCreate(){
        // Prepare to work with the DevicePolicyManager
        mDevicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(this, AdminReceiver.class);

        // Activate Device Administrator
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
        //startActivityForResult(intent, RESULT_ENABLE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
