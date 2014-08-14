package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.common.TaskFragmentActivity;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.ws.TaskFragment;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FormActivity extends TaskFragmentActivity implements FormActivityCallBack{

	private static final String TAG = FormActivity.class.getSimpleName();
	private static final String SELECTED_TAB = "selected_tab";
	private MenuItems menu;
	private Layout layout;
	private int menuItemID = -1;
	private DAOFactory daoFactory;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		daoFactory = new DAOFactory(this);
		
		FragmentManager fm = getFragmentManager();
		mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG + "taskFragment");
		if (mTaskFragment == null) {
			mTaskFragment = new TaskFragment();
			fm.beginTransaction().add(mTaskFragment, TAG + "taskFragment").commit();
		}
		
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.ic_action_event);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.Tab mine = actionBar
				.newTab()
				.setText(" ")
				.setTabListener(
						new TabListener<ListAllFormsFragment>(this, ListAllFormsFragment.class.getSimpleName(),
								ListAllFormsFragment.class));
		
		actionBar.addTab(mine);
		

		// menu
		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt(SELECTED_TAB, 1));
			menuItemID = savedInstanceState.getInt(Values.MENU_ITEM_ID, -1);
		} else {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				menuItemID = extras.getInt(Values.MENU_ITEM_ID, -1);
			}
		}
		menu = daoFactory.getMenuItemDAO().getMenu(menuItemID);
		layout = daoFactory.getLayoutDAO().getLayout(menu.getLayout().getName());
		actionBar.setTitle(layout.getName());	
	}

	public void newData() {
		Intent intent = new Intent(this, FormDetailsActivity.class);
		intent.putExtra(Values.MENU_ITEM_ID, menu.getId());
		intent.putExtra(Layout.LAYOUT_ID, layout.getName());	
		intent.putExtra(Form.FORM_MODE, Values.FORM_NEW_DATA);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	// voláno z fragmentu
	public MenuItems getMenuData() {
		return menu;
	}

	// voláno z fragmentu
	public Layout getLayout() {
		return layout;
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
			newData();
			break;

		case R.id.form_edit_layout:
			Intent intentEdit = new Intent(this, FormDetailsActivity.class);
			intentEdit.putExtra(Values.MENU_ITEM_ID, menu.getId());
			intentEdit.putExtra(Layout.LAYOUT_ID, layout.getName());	
			intentEdit.putExtra(Form.FORM_MODE, Values.FORM_EDIT_LAYOUT);
			startActivity(intentEdit);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(SELECTED_TAB, getActionBar().getSelectedNavigationIndex());
		outState.putInt(Values.MENU_ITEM_ID, menuItemID);
	}

	public void restartAdapter() {
		this.finish();
	}
	
	@Override
	public TaskFragment getTaskFragment() {
		return mTaskFragment;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		daoFactory.releaseHelper();
	}
}
