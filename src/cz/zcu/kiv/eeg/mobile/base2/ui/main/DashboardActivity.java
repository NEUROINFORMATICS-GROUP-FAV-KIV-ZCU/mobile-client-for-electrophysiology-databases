package cz.zcu.kiv.eeg.mobile.base2.ui.main;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.DrawerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.ui.field.FieldEditorAddActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormAddActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.settings.LoginActivity;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class DashboardActivity extends Activity {
	private static final String TAG = DashboardActivity.class.getSimpleName();
	// public static final String MENU_ITEM_ID = "menuItem_id";
	// public static final String MENU_ITEM_NAME = "menuItem_name";

	private DAOFactory daoFactory;
	private int previousFragment = -10;
	private DrawerLayout drawerLayout;
	private LinearLayout leftDrawer;
	// private ListAdapter drawerAdapter;
	private DrawerAdapter drawerAdapter;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	private ActionBar actionBar;
	MenuItems selectedMenuItem;
	private MenuItems rootMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate(Bundle)");
	    rootMenu = null;
		daoFactory = new DAOFactory(this);

		setContentView(R.layout.dashboard_drawer);
		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		leftDrawer = (LinearLayout) findViewById(R.id.left_drawer);
		drawerList = (ListView) findViewById(R.id.drawer_list);

		// use getActionBar().getThemedContext() to ensure
		// that the text color is always appropriate for the action bar
		// background rather than the activity background.
		drawerList.setAdapter(getAdapter());
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
			selectMenuItem(-1);
		}else {
			previousFragment = savedInstanceState.getInt("previousFragment", -1);
		}
		
		setMenuButton();
	}

	protected void onResume() {
		super.onResume();
		updateAdapter();
	}

	public boolean selectMenuItem(int itemPosition) {
		
		switch (itemPosition) {
		// dashboard
		case -1:
			if (itemPosition != previousFragment) {
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				DashboardFragment dashboardFrag = new DashboardFragment();
				fragmentTransaction.replace(R.id.content_frame_drawer, dashboardFrag, DashboardFragment.TAG);
				fragmentTransaction.commit();
				previousFragment = itemPosition;
			}
			break;
		default:		
			selectedMenuItem = drawerAdapter.getItem(itemPosition);	
			
			// open submenu
			if (rootMenu == null) {
				rootMenu = selectedMenuItem;
				updateAdapter();
			} else {		
				if (selectedMenuItem.getId() == -1) {
					updateRootAdapter();
				} else {
					//selectedMenuItem.getID - podle toho budu zarazovat formulare
					Intent intent = new Intent(this, FormActivity.class);
					//intent.putExtra(Values.MENU_ITEM_ID, itemPosition + 1); // v db se indexue od 1
					intent.putExtra(Values.MENU_ITEM_ID, selectedMenuItem.getId()); // v db se indexue od 1
					startActivity(intent);
					previousFragment = itemPosition;
				}
			}
			break;
		}

		// drawerLayout.closeDrawer(drawerList);
		return true;
	}

	public void createFolder(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();	
		dialog.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		builder.setTitle(getString(R.string.drawer_createNewfolder));
   	
		final EditText input = new EditText(this);
		input.setPadding(25, 50, 0, 10);
		
		input.setText(getString(R.string.drawer_newfolder));
		input.setOnFocusChangeListener(new OnFocusChangeListener() {	
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus){
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
		        	((EditText)v).selectAll();		           
		        }
				
			}
		});
				
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {		
				String folderName = input.getText().toString();
				if (!folderName.equalsIgnoreCase("")) {
					MenuItems newMenu = new MenuItems(folderName, Values.ICON_FOLDER);
					daoFactory.getMenuItemDAO().saveOrUpdate(newMenu);
					drawerAdapter.add(newMenu);
					drawerAdapter.notifyDataSetChanged();
				}
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {				
			}
		});

		builder.show();
	}
	


	/**
	 * Scenario adapter getter. Instance is created in moment of first invocation.
	 * 
	 * @return scenario adapter
	 */
	private DrawerAdapter getAdapter() {
		if (drawerAdapter == null) {
			drawerAdapter = new DrawerAdapter(actionBar.getThemedContext(), R.layout.dashboard_drawer_item,
					(ArrayList<MenuItems>) getRootMenu());
		}
		return drawerAdapter;
	}

	private void updateAdapter() {
		if (rootMenu != null) {
			MenuItems up = new MenuItems("[ .. ]", Values.ICON_FOLDER_UP);
			up.setId(-1);

			drawerAdapter.clear();
			drawerAdapter.add(up);
			for (MenuItems menu : getMenuItems(rootMenu)) {
				drawerAdapter.add(menu);
			}
			drawerAdapter.notifyDataSetChanged();
			setMenuButton();
		}
	}

	private void updateRootAdapter() {
		drawerAdapter.clear();
		for (MenuItems menu : getRootMenu()) {
			if(menu.getIcon() != null){
				drawerAdapter.add(menu);
			}		
		}
		drawerAdapter.notifyDataSetChanged();
		rootMenu = null;
		setMenuButton();
	}
	
	private List<MenuItems> getMenuItems(MenuItems parent) {
		return daoFactory.getMenuItemDAO().getMenu(parent);
	}

	private List<MenuItems> getRootMenu() {
		return daoFactory.getMenuItemDAO().getRootMenu();
	}
	
	public void createForm(View view){
		Intent intent = new Intent(this, FormAddActivity.class);
		intent.putExtra(MenuItems.ROOT_MENU, rootMenu.getId());
		startActivityForResult(intent, Values.NEW_FORM_REQUEST);	
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == Values.NEW_FORM_REQUEST) {
				updateAdapter();
			}
		}
	}
	
	private void setMenuButton(){
		Button newFolder = (Button) findViewById(R.id.drawer_newFolder);
		Button newForm = (Button) findViewById(R.id.drawer_newForm);
		Button setServer = (Button) findViewById(R.id.drawer_setServer);
		
		if(rootMenu == null){
			newFolder.setVisibility(View.VISIBLE);
			newForm.setVisibility(View.GONE);
			setServer.setVisibility(View.GONE);
		}else{
			newFolder.setVisibility(View.GONE);
			newForm.setVisibility(View.VISIBLE);
			setServer.setVisibility(View.VISIBLE);
		}
	}
	
	public void openSettings(View view){
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
	}
		
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.dashboard_menu, menu);
		return true;
	}*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (drawerLayout.isDrawerOpen(leftDrawer)) {
				drawerLayout.closeDrawer(leftDrawer);
			} else {
				drawerLayout.openDrawer(leftDrawer);
			}
			break;
		/*case R.id.form_add:
			startActivity(new Intent(this, FormAddActivity.class));
			break;
		case R.id.settings:
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
			break;*/
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("previousFragment", previousFragment);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		daoFactory.releaseHelper();
	}

}
