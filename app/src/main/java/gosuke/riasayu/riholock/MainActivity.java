package gosuke.riasayu.riholock;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import java.util.Calendar;


public class MainActivity extends PreferenceActivity {

    private static final String TAG = "RihoLock";
    static final int RESULT_ENABLE = 1;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mDeviceAdmin;

    private CheckBoxPreference mRestrictionEnableCheckbox;
    private ListPreference mRestrictionTimeList;
    private boolean mAdminActivated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prepare to work with the DevicePolicyManager
        mDevicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(this, AdminReceiver.class);

        // Activate Device Administrator
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
        startActivityForResult(intent, RESULT_ENABLE);

        // Initialize activity
        addPreferencesFromResource(R.xml.preference);

        // Enable checkbox
        mRestrictionEnableCheckbox = (CheckBoxPreference) findPreference("restrict_enable");
        mRestrictionEnableCheckbox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                setRestrictEnabled(((Boolean) o).booleanValue());
                mDevicePolicyManager.lockNow();
                return true;
            }
        });

        // restriction time listbox
        mRestrictionTimeList = (ListPreference) findPreference("restrict_time");
        String entry = (String) mRestrictionTimeList.getEntries()[mRestrictionTimeList.findIndexOfValue(mRestrictionTimeList.getValue())];
        mRestrictionTimeList.setSummary(entry);

        mRestrictionTimeList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String entry = (String) mRestrictionTimeList.getEntries()[mRestrictionTimeList.findIndexOfValue((String) o)];
                //String summary = getString(R.string.pref_restrict_enable_summary, entry);
                mRestrictionTimeList.setSummary(entry);
                return true;
            }
        });
    }

    boolean setRestrictEnabled(boolean enable) {
        if (enable) {
            setRestrictionStartDate();
            return true;
        } else if (mAdminActivated) {
            Log.d(TAG, "UnLock restriction disabled");
            //mDevicePolicyManager.setMaximumTimeToLock(mDeviceAdmin, 10);	// 0 means no restriction(infinite)
            return true;
        }

        return false;
    }


    private void setRestrictionStartDate()
    {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Calendar calendar = Calendar.getInstance();
        editor.putInt("start_date", calendar.get(Calendar.DAY_OF_YEAR));
        editor.commit();
        Log.d(TAG, "Start Date:" + pref.getInt("start_date", 0));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_ENABLE) {
            if (resultCode == Activity.RESULT_OK && mDevicePolicyManager.isAdminActive(mDeviceAdmin)) {
                mAdminActivated = true;
                Log.d(TAG, "SUCCESSFUL: Administration activation");
            } else {
                Log.d(TAG, "FAILURE: Administration activation");
                mRestrictionEnableCheckbox.setChecked(false);
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
