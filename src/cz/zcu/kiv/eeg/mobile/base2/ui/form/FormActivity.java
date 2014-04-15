package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.ui.main.DashboardActivity;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FormActivity extends Activity {

	private static final String SELECTED_TAB = "selected_tab";
	private MenuItems menu;
	private DAOFactory daoFactory;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		daoFactory = new DAOFactory(this);
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.ic_action_event);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);

		ActionBar.Tab mine = actionBar
				.newTab()
				.setText("Mine")
				.setTabListener(
						new TabListener<ListAllFormsFragment>(this, ListAllFormsFragment.class.getSimpleName(),
								ListAllFormsFragment.class));
		ActionBar.Tab all = actionBar
				.newTab()
				.setText("All")
				.setTabListener(
						new TabListener<ListAllFormsFragment>(this, ListAllFormsFragment.class.getSimpleName(),
								ListAllFormsFragment.class));

		actionBar.addTab(mine);
		actionBar.addTab(all);

		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt(SELECTED_TAB, 1));
		}
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int menuItemID = extras.getInt(DashboardActivity.MENU_ITEM_ID, -1);
			menu = daoFactory.getMenuItemDAO().getMenu(menuItemID);
			actionBar.setTitle(menu.getName());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	// voláno z fragmentu
	public MenuItems getMenuData() {
		return menu;
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
			Intent intent = new Intent(this, FormDetailsActivity.class);
			intent.putExtra(DashboardActivity.MENU_ITEM_ID, menu.getId());
			intent.putExtra(DashboardActivity.MENU_ITEM_NAME, menu.getName());
			startActivity(intent);
			break;
		case R.id.form_refresh:
			// TODO
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		daoFactory.releaseHelper();
	}
}
