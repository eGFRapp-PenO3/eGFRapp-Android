package be.kulak.peo.egfr;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by elias on 07/12/16.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
