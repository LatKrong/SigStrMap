package com.latkrong.sigstrmap.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.latkrong.sigstrmap.R;

public class SettingsFragment extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey)
    {
        addPreferencesFromResource(R.xml.settings);
    }
}
