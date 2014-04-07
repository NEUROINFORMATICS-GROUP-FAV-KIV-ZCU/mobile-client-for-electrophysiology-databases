package cz.zcu.kiv.eeg.mobile.base2.ui.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import cz.zcu.kiv.eeg.mobile.base2.R;

public class SettingsActivity extends Activity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    TextView username;
    TextView password;
    TextView url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	Log.d(TAG, "Settings screen");
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_settings);

	ActionBar actionBar = getActionBar();
	actionBar.setIcon(R.drawable.ic_action_settings);
	actionBar.setDisplayHomeAsUpEnabled(true);

	SharedPreferences credentials = PreferenceManager.getDefaultSharedPreferences(this);
	CharSequence usernameText = credentials.getString("username", null);
	CharSequence passwordText = credentials.getString("password", null);
	CharSequence urlText = credentials.getString("url", "https://");

	username = (TextView) findViewById(R.id.settings_username);
	password = (TextView) findViewById(R.id.settings_password);
	url = (TextView) findViewById(R.id.settings_url);

	username.setText(usernameText);
	password.setText(passwordText);
	url.setText(urlText);
    }

    @Override
    protected void onPause() {
	// todo volat pouze při změně hodnoty
	super.onPause();

	// testCredentials(usernameField.getText().toString(),

	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	SharedPreferences.Editor editor = preferences.edit();

	editor.putString("username", username.getText().toString());
	editor.putString("password", password.getText().toString());
	editor.putString("url", url.getText().toString());
	editor.commit();

	Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();

    }

    // todo asych task
    private void testCredentials(String username, String password, String url) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    finish();
	    break;	   
	}
	return super.onOptionsItemSelected(item);
    }
}
