package org.haldean.gesturecontrol;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class ServerPrefsFragment extends PreferenceFragment {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
