package cz.zcu.kiv.eeg.mobile.base2.ui.main;

import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.DrawerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.FormLayoutsDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.settings.SettingsActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DashboardActivity extends Activity {
	private static final String TAG = DashboardActivity.class.getSimpleName();
	private int previousFragment = -1;

	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate(Bundle)");

		setContentView(R.layout.dashboard_drawer);
		final ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);

		// use getActionBar().getThemedContext() to ensure
		// that the text color is always appropriate for the action bar
		// background rather than the activity background.
		ListAdapter drawerAdapter = new DrawerAdapter(actionBar.getThemedContext(), R.layout.dashboard_drawer_item, getMenuItems());
		drawerList.setAdapter(drawerAdapter);
		drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectMenuItem(position);
			}
		});

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, // todo
				R.string.nav_open, R.string.nav_close) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}
		};

		drawerLayout.setDrawerListener(drawerToggle);

		if (savedInstanceState == null) {
			selectMenuItem(0);
		}
		if (savedInstanceState != null) {
			previousFragment = savedInstanceState.getInt("previousFragment", 0);
		}
	}

	public boolean selectMenuItem(int itemPosition) {
		Intent intent;

		if (itemPosition != previousFragment) {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			switch (itemPosition) {
			// dashboard
			case 0:
				DashboardFragment dashboardFrag = new DashboardFragment();
				fragmentTransaction.replace(R.id.content_frame_drawer, dashboardFrag, DashboardFragment.TAG);
				fragmentTransaction.commit();
				previousFragment = itemPosition;
				break;
			case 1:
				intent = new Intent(this, FormActivity.class);
				intent.putExtra("layout", "Person-generated"); //todo
				startActivity(new Intent(this, FormActivity.class));
				Toast.makeText(this, "Persons selected", Toast.LENGTH_SHORT).show();
				//previousFragment = itemPosition;
				break;
			default:
				return false;
			}
		}
		drawerLayout.closeDrawer(drawerList);
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("previousFragment", previousFragment);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.dashboard_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (drawerLayout.isDrawerOpen(drawerList)) {
				drawerLayout.closeDrawer(drawerList);
			} else {
				drawerLayout.openDrawer(drawerList);
			}
			break;
		case R.id.form_add:
			Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT).show();
			break;
		case R.id.settings:
			startActivity(new Intent(this, SettingsActivity.class));
			Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private String[] getMenuItems() {
		DAOFactory daoFactory = new DAOFactory(this);
		return daoFactory.getMenuItemDAO().getMenu();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

}
