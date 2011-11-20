package com.tkraus.greenball;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class JenkinsPreferencesActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
