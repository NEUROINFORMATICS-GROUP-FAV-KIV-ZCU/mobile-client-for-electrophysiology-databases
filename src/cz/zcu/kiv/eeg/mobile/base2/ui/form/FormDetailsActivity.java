package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.ui.main.DashboardActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.settings.LoginActivity;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FormDetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			FormDetailsFragment details = new FormDetailsFragment();
			details.setArguments(getIntent().getExtras());

			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(android.R.id.content, details);
			fragmentTransaction.commit();
		}
		
		// enables up button, sets section icon
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.ic_action_info);
		actionBar.setTitle(getIntent().getExtras().getString(DashboardActivity.MENU_ITEM_NAME));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.settings:
			startActivity(new Intent(this, LoginActivity.class));
			Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
