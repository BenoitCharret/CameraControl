package com.axioConsulting.cameracontrol.app;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by benoit on 19/09/15.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }


}
