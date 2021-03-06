package cz.zcu.kiv.eeg.mobile.base2.ui.main;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.common.TaskFragmentActivity;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.DrawerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormTypeSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.LayoutDialogAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutRow;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormAddActivityNew;
import cz.zcu.kiv.eeg.mobile.base2.util.ValidationUtils;
import cz.zcu.kiv.eeg.mobile.base2.ws.TaskFragment;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class DashboardActivity extends TaskFragmentActivity {
	private static final String TAG = DashboardActivity.class.getSimpleName();
	private DAOFactory daoFactory;
	private int previousFragment = -10;
	private DrawerLayout drawerLayout;
	private LinearLayout leftDrawer;
	private DrawerAdapter drawerAdapter;

	FormTypeSpinnerAdapter typeAdapter;

	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	private ActionBar actionBar;

	private MenuItems rootMenu; // rodič položky
	private MenuItems selectedMenuItem;
	private User rollbackUser = null;

	private Dialog dialog;

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

		// getActionBar().getThemedContext() to ensure
		// that the text color is always appropriate for the action bar
		// background rather than the activity background.
		drawerList.setAdapter(getAdapter());
		registerForContextMenu(drawerList);
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
		} else {
			previousFragment = savedInstanceState.getInt("previousFragment", -1);
			int id = savedInstanceState.getInt("rootMenu");
			rootMenu = daoFactory.getMenuItemDAO().getMenu(id);

			id = savedInstanceState.getInt("selectedMenuItem");
			selectedMenuItem = daoFactory.getMenuItemDAO().getMenu(id);
		}

		setMenuButton();

		FragmentManager fm = getFragmentManager();
		mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG + "taskFragment");
		if (mTaskFragment == null) {
			mTaskFragment = new TaskFragment();
			fm.beginTransaction().add(mTaskFragment, TAG + "taskFragment").commit();
		}
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

			//nacházím se v seznamu workspaců
			if (selectedMenuItem.getParentId() == null && selectedMenuItem.getId() != Values.BACK_FOLDER_BUTTON) {
				// open submenu
				rootMenu = selectedMenuItem;
				updateAdapter();
			} else {
				// nacházím v seznamu podformulářů
				if (selectedMenuItem.getId() == Values.BACK_FOLDER_BUTTON) {
					// o úroveň výše (přechod na seznam workspaců)
					updateRootAdapter();
				} else {
					// otevření formuláře
					Intent intent = new Intent(this, FormActivity.class);
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

	/*
	 * 
	 * Drawer section
	 */

	private DrawerAdapter getAdapter() {
		if (drawerAdapter == null) {
			drawerAdapter = new DrawerAdapter(actionBar.getThemedContext(), R.layout.dashboard_drawer_item,
					(ArrayList<MenuItems>) getRootMenu());
			updateRootAdapter();
		}
		return drawerAdapter;
	}

	private void updateAdapter() {
		if (rootMenu != null) {
			MenuItems up = new MenuItems("[ .. ]", Values.ICON_FOLDER_UP);
			up.setId(Values.BACK_FOLDER_BUTTON);

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
			if (menu.getIcon() != null) {
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

	/**
	 * nastavuji viditelnost tlačítek (New workspace a Create form)
	 */
	private void setMenuButton() {
		Button newFolder = (Button) findViewById(R.id.drawer_newFolder);
		Button newForm = (Button) findViewById(R.id.drawer_newForm);
		Button setServer = (Button) findViewById(R.id.drawer_setServer);

		if (rootMenu == null) {
			newFolder.setVisibility(View.VISIBLE);
			newForm.setVisibility(View.GONE);
			setServer.setVisibility(View.GONE);
		} else {
			newFolder.setVisibility(View.GONE);
			newForm.setVisibility(View.VISIBLE);
			setServer.setVisibility(View.GONE);
		}
	}

	/*
	 * 
	 * Workspace section
	 */

	public void createWorkspace(View view) {
		createWorkspaceDialog(getString(R.string.drawer_new_workspace), Values.WORKSPACE_NEW,
				getString(R.string.drawer_new_workspace), 0);
	}

	private void editWorkspace(MenuItems menuItem) {
		createWorkspaceDialog(getString(R.string.edit_workspace), Values.WORKSPACE_EDIT, menuItem.getName(),
				menuItem.getId());
	}

	private void deleteFolderOrWorkspace(MenuItems menuItem) {
		deleteAlertDialog(menuItem);
	}

	// kliknutím na tlačítko CreateForm uvnitř workspacu
	public void createForm(View view) {
		User user = daoFactory.getUserDAO().getUser(rootMenu.getCredential().getId());
		rootMenu.setCredential(user);
		createSelectLayoutDialog();

	}

	public void createWorkspaceDialog(String title, final int mode, final String folderName, final int folderId) {
		final Context ctx = this;
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.workspace_add);
		dialog.setTitle(title);
		final EditText workspaceNameField = (EditText) dialog.findViewById(R.id.settings_workspace_name);
		final TextView usernameField = (TextView) dialog.findViewById(R.id.settings_username);
		final TextView passwordField = (TextView) dialog.findViewById(R.id.settings_password);
		final TextView urlField = (TextView) dialog.findViewById(R.id.settings_url);
		urlField.setText("https://");

		// inicializace údajů
		if (mode == Values.WORKSPACE_EDIT) {
			MenuItems menuItem = daoFactory.getMenuItemDAO().getMenu(folderId);
			workspaceNameField.setText(menuItem.getName());

			if (menuItem.getCredential() == null) {
				usernameField.setText("");
				passwordField.setText("");

			} else {
				User user = daoFactory.getUserDAO().getUser(menuItem.getCredential().getId());
				usernameField.setText(user.getUsername());
				passwordField.setText(user.getPassword());
				urlField.setText(user.getUrl());			
				rollbackUser = new User();
				rollbackUser.setFirstName(user.getFirstName());
				rollbackUser.setId(user.getId());
				rollbackUser.setPassword(user.getPassword());
				rollbackUser.setRights(user.getRights());
				rollbackUser.setSurname(user.getSurname());
				rollbackUser.setUrl(user.getUrl());
				rollbackUser.setUsername(user.getUsername());
			}
		}

		Button cancelButton = (Button) dialog.findViewById(R.id.settings_button_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (rollbackUser != null) {
					daoFactory.getUserDAO().update(rollbackUser);
				}
				dialog.dismiss();
			}
		});

		Button addButton = (Button) dialog.findViewById(R.id.settings_button_add);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				User user = null;

				if (mode == Values.WORKSPACE_EDIT) {
					selectedMenuItem = daoFactory.getMenuItemDAO().getMenu(folderId);
					if (selectedMenuItem.getCredential() == null) {
						user = new User();
					} else {
						user = daoFactory.getUserDAO().getUser(selectedMenuItem.getCredential().getId());						
					}
				} else if (mode == Values.WORKSPACE_NEW) {
					user = new User();
					selectedMenuItem = new MenuItems();
				}

				rootMenu = selectedMenuItem;

				String workSpaceName = workspaceNameField.getText().toString();
				user.setUsername(usernameField.getText().toString());
				user.setPassword(passwordField.getText().toString());
				user.setUrl(urlField.getText().toString());
				if (mode == Values.WORKSPACE_NEW) {				
					//user.setUrl("https://10.0.0.40:8443");
				}

				selectedMenuItem.setName(workSpaceName);
				selectedMenuItem.setIcon(Values.ICON_FOLDER);
				String error = "";

				// pouze vytořit workspace, bez ověření serverem
				if (ValidationUtils.isEmptyUsername(user)) {
					error = ValidationUtils.isWorkspaceValid(ctx, workSpaceName);
					if (error.toString().isEmpty()) {
						daoFactory.getUserDAO().create(user);
						selectedMenuItem.setCredential(user);
						saveNewWorkspace();
					}
				} else {
					error = ValidationUtils.isWorkspaceValid(ctx, user, workSpaceName, daoFactory);
					if (error.toString().isEmpty()) {
						if (mode == Values.WORKSPACE_NEW) {
							daoFactory.getUserDAO().create(user);
						} else {
							daoFactory.getUserDAO().update(user);
						}
						selectedMenuItem.setCredential(user);
						// zkouška uživatele a adresy serveru
						mTaskFragment.startLogin(user); // předávat workspace edit
					}
				}
				if (!error.toString().isEmpty()) {
					showAlert(error.toString());
				}				 							
			}
		});
		dialog.show();
	}

	// voláno i z TestCreditials
	public void saveNewWorkspace() {
		daoFactory.getMenuItemDAO().saveOrUpdate(selectedMenuItem);
				
		updateRootAdapter();
		rootMenu = selectedMenuItem;

		dialog.dismiss();		
		createSelectLayoutDialog();
		
	}

	public void createSelectLayoutDialog() {
		final Context ctx = this;
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.workspace_select_layout);
		dialog.setTitle(getString(R.string.form_layouts));

		final LayoutDialogAdapter layoutAdapter = new LayoutDialogAdapter(this, R.layout.layout_row,
				new ArrayList<LayoutRow>());
		
		List<Layout> layoutsTmp = daoFactory.getLayoutDAO().getLayouts();
		final List<MenuItems> menuItems = daoFactory.getMenuItemDAO().getMenu(rootMenu);	//seznam formulřů daného workspacu	
		
		// inicializace seznamu layoutů (zaškrtnut/ nezaškrtnut)
		if(layoutsTmp != null){
			for(Layout layout : layoutsTmp){
				boolean used = false;
				for(MenuItems menu : menuItems){
					if(menu.getLayout().getName().equalsIgnoreCase(layout.getName())){
						layoutAdapter.add(new LayoutRow(layout.getName(), true));
						used = true;
					}
				}
				if(!used){
					layoutAdapter.add(new LayoutRow(layout.getName(), false));
				}
			}
		}			

		ListView list = (ListView) dialog.findViewById(R.id.layoutList);
		list.setAdapter(layoutAdapter);

		//stáhnutí seznamu layoutů
		ImageButton refreshButton = (ImageButton) dialog.findViewById(R.id.form_button_refresh);
		User credential = daoFactory.getUserDAO().getUser(rootMenu.getCredential().getId());
		// bez přihlášeného uživatele (nezobrazovat stahování layoutů ze serveru)
		if (rootMenu.getCredential() == null  || ValidationUtils.isEmptyUsername(credential)) {
			refreshButton.setVisibility(View.GONE);
		} else {
			refreshButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTaskFragment.startFetchLayouts(layoutAdapter, rootMenu);
					dialog.dismiss();
				}
			});
		}

		//OK tlačítko - přidá/odebere vybrané layouty
		Button addButton = (Button) dialog.findViewById(R.id.layout_button_add);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<LayoutRow> list = layoutAdapter.getList();

				for (LayoutRow row : list) {
					Layout layout = daoFactory.getLayoutDAO().getLayout(row.getName());
					
					//nalezení menuItem				
					int menuItemID = 0;
					for(MenuItems menu : menuItems){
						if(menu.getLayout().getName().equalsIgnoreCase(row.getName())){						
							menuItemID = menu.getId();						
							break;
						}
					}
					
					//MenuItem neexistuje a je zaškrtnuto => přidat
					if(menuItemID == 0){
						if (row.isChecked()) {
							MenuItems menuItem = new MenuItems(layout.getName(), layout, layout.getRootForm(), layout
									.getPreviewMajor(), layout.getPreviewMinor(), rootMenu);
							menuItem.setIcon("");
							daoFactory.getMenuItemDAO().saveOrUpdate(menuItem);
							updateAdapter();
						}
					//MenuItem existuje a je nezaškrnuto => odebrat
					}else if (menuItemID > 0){
						if (!row.isChecked()) {
							daoFactory.getMenuItemDAO().delete(menuItemID);											
							updateAdapter();
						}
					}					
				}												
				dialog.dismiss();
			}
		});

		// otevření aktivity na přidání formuláře
		Button newButton = (Button) dialog.findViewById(R.id.layout_button_new);
		newButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				// createFormDialog();
				Intent intent = new Intent(ctx, FormAddActivityNew.class);
				intent.putExtra(MenuItems.ROOT_MENU, rootMenu.getId());
				// startActivityForResult(intent, Values.NEW_FORM_REQUEST);
				startActivity(intent);
			}

		});

		dialog.show();
	}

	public void deleteAlertDialog(final MenuItems menuItem) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		if (menuItem.getParentId() == null) {
			builder.setTitle(getString(R.string.delete_folder));
		} else {
			builder.setTitle(getString(R.string.delete_formr));
		}
		builder.setMessage(menuItem.getName());

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// remove workspace
				if (menuItem.getParentId() == null) {
					List<MenuItems> items = daoFactory.getMenuItemDAO().getMenu(menuItem);
					for (MenuItems item : items) {
						daoFactory.getMenuItemDAO().delete(item.getId());					
					}
					daoFactory.getMenuItemDAO().delete(menuItem.getId());
					updateRootAdapter();
				}
				// remove item
				else {			
					daoFactory.getMenuItemDAO().delete(menuItem.getId());
					updateAdapter();
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == Values.NEW_FORM_REQUEST) {
				updateAdapter();
			}
		}
	}

	/*
	 * 
	 * MENU
	 */

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
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		
		if(rootMenu == null){
			inflater.inflate(R.menu.dashboard_menu, menu);		
		}
		else{
			inflater.inflate(R.menu.dashboard_submenu, menu);
		}			
	}
	
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		MenuItems menuItem = drawerAdapter.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.menu_edit:
			if (menuItem.getParentId() == null) {
				editWorkspace(menuItem);
			} else {
				 //editForm(menuItem.getId());
			}

			return true;
		case R.id.menu_delete:
			deleteFolderOrWorkspace(menuItem);
			return true;

		default:
			return super.onContextItemSelected(item);
		}
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
		if (rootMenu != null) {
			outState.putInt("rootMenu", rootMenu.getId());
		}
		if (selectedMenuItem != null) {
			outState.putInt("selectedMenuItem", selectedMenuItem.getId());
		}
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
