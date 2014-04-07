package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.ui.main.DashboardFragment;
import cz.zcu.kiv.eeg.mobile.base2.ui.settings.SettingsActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class FormDetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// enables up button, sets section icon
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.ic_action_info);
		actionBar.setTitle("Person");

		if (savedInstanceState == null) {
			FormDetailsFragment details = new FormDetailsFragment();		
			details.setArguments(getIntent().getExtras());

			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(android.R.id.content, details);
			fragmentTransaction.commit();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.settings:
			startActivity(new Intent(this, SettingsActivity.class));
			Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
			break;		
		}
		return super.onOptionsItemSelected(item);
	}	
}
