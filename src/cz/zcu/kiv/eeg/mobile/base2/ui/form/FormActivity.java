package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.ui.settings.SettingsActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class FormActivity extends Activity {

	private static final String SELECTED_TAB = "selected_tab";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.ic_action_event);
		actionBar.setTitle("Persons");

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);

		ActionBar.Tab mine = actionBar.newTab().setText("Mine")
				.setTabListener(new TabListener<ListAllFormsFragment>(this, ListAllFormsFragment.class.getSimpleName(), ListAllFormsFragment.class));
		ActionBar.Tab all = actionBar.newTab().setText("All")
				.setTabListener(new TabListener<ListAllFormsFragment>(this, ListAllFormsFragment.class.getSimpleName(), ListAllFormsFragment.class));

		actionBar.addTab(mine);
		actionBar.addTab(all);

		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt(SELECTED_TAB, 1));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.form_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.form_new_data:
			Toast.makeText(this, "New data", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.setClass(this, FormDetailsActivity.class);
			intent.putExtra("index", -1);
			// intent.putExtra("data", dataAdapter.getItem(index));
			startActivity(intent);
			break;
		case R.id.form_refresh:
			//startActivity(new Intent(this, SettingsActivity.class));
			Toast.makeText(this, "Fetch data", Toast.LENGTH_SHORT).show();
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(SELECTED_TAB, getActionBar().getSelectedNavigationIndex());
	}
}
