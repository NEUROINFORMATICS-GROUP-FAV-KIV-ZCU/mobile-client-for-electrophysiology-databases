package cz.zcu.kiv.eeg.mobile.base2.ui.main;

import java.util.ArrayList;
import java.util.List;

import android.R.menu;
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
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.common.TaskFragmentActivity;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.DrawerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormTypeSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.LayoutDialogAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.LayoutSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.SpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormRow;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutMenuItems;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutRow;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;
import cz.zcu.kiv.eeg.mobile.base2.ui.field.FieldEditorAddActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormAddActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.settings.LoginActivity;
import cz.zcu.kiv.eeg.mobile.base2.util.ConnectionUtils;
import cz.zcu.kiv.eeg.mobile.base2.util.ValidationUtils;
import cz.zcu.kiv.eeg.mobile.base2.ws.TaskFragment;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class DashboardActivity extends TaskFragmentActivity {
	private static final String TAG = DashboardActivity.class.getSimpleName();
	// public static final String MENU_ITEM_ID = "menuItem_id";
	// public static final String MENU_ITEM_NAME = "menuItem_name";

	private DAOFactory daoFactory;
	private int previousFragment = -10;
	private DrawerLayout drawerLayout;
	private LinearLayout leftDrawer;
	// private ListAdapter drawerAdapter;
	private DrawerAdapter drawerAdapter;
	private LayoutSpinnerAdapter layoutAdapter;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	private ActionBar actionBar;
	private MenuItems selectedMenuItem;
	private MenuItems rootMenu;
	private MenuItems newMenu;
	private Dialog dialog;

	private TaskFragment mTaskFragment;

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
		}

		setMenuButton();

		FragmentManager fm = getFragmentManager();
		mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG + "Fragment");
		if (mTaskFragment == null) {
			mTaskFragment = new TaskFragment();
			fm.beginTransaction().add(mTaskFragment, "taskFragment").commit();
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

			// open submenu
			if (rootMenu == null) {
				rootMenu = selectedMenuItem;
				updateAdapter();
			} else {
				if (selectedMenuItem.getId() == -1) {
					updateRootAdapter();
				} else {
					// selectedMenuItem.getID - podle toho budu zarazovat formulare
					Intent intent = new Intent(this, FormActivity.class);
					// intent.putExtra(Values.MENU_ITEM_ID, itemPosition + 1); // v db se indexue od 1
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

	public void createWorkspace(View view) {
		createWorkspaceDialog(getString(R.string.drawer_new_workspace), Values.WORKSPACE_NEW,
				getString(R.string.drawer_new_workspace), 0);
	}

	private void editWorkspace(MenuItems menuItem) {
		createWorkspaceDialog(getString(R.string.edit_workspace), Values.WORKSPACE_EDIT, menuItem.getName(),
				menuItem.getId());
	}

	private void deleteFolder(MenuItems menuItem) {
		deleteAlertDialog(menuItem);
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

		if (mode == Values.WORKSPACE_EDIT) {
			MenuItems menuItem = daoFactory.getMenuItemDAO().getMenu(folderId);
			User user = daoFactory.getUserDAO().getUser(menuItem.getCredential().getId());

			workspaceNameField.setText(menuItem.getName());
			usernameField.setText(user.getUsername());
			passwordField.setText(user.getPassword());
			urlField.setText(user.getUrl());
		}

		Button cancelButton = (Button) dialog.findViewById(R.id.settings_button_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		Button addButton = (Button) dialog.findViewById(R.id.settings_button_add);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				newMenu = null;
				User user = null;
				boolean isEdit = false;

				if (mode == Values.WORKSPACE_EDIT) {
					newMenu = daoFactory.getMenuItemDAO().getMenu(folderId);
					user = daoFactory.getUserDAO().getUser(newMenu.getCredential().getId());
					isEdit = true;
				} else if (mode == Values.WORKSPACE_NEW) {
					user = new User();
					newMenu = new MenuItems();
					// todo odstranit testovaci
					urlField.setText(Values.URL_DEFAULT);// default
					// usernameField.setText("axim@students.zcu.cz");
					// passwordField.setText("19821983");
				}

				String workSpaceName = workspaceNameField.getText().toString();
				user.setUsername(usernameField.getText().toString());
				user.setPassword(passwordField.getText().toString());
				user.setUrl(urlField.getText().toString());
				newMenu.setName(workSpaceName);
				newMenu.setIcon(Values.ICON_FOLDER);

				String error = ValidationUtils.isWorkspaceValid(ctx, user, workSpaceName, daoFactory, isEdit);

				if (error.toString().isEmpty()) {
					daoFactory.getUserDAO().saveOrUpdate(user);
					newMenu.setCredential(user);

					if (!user.getUsername().equals("")) {
						// zkouška připojení
						mTaskFragment.startLogin(user);
					} else {
						saveNewWorkspace(true, false);
					}
				} else {
					showAlert(error.toString());
				}
			}
		});
		dialog.show();
	}

	public void saveNewWorkspace(boolean isNew, boolean isCredentials) {
		daoFactory.getMenuItemDAO().saveOrUpdate(newMenu);
		if (isNew) {
			drawerAdapter.add(newMenu);
			drawerAdapter.notifyDataSetChanged();

		} else {
			updateRootAdapter();
		}
		dialog.dismiss();

		if (isCredentials) {
			mTaskFragment.startFetchLayouts(newMenu);
		}
	}

	public void createSelectLayoutDialog(ArrayList<String> layouts) {
		final Context ctx = this;
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.workspace_select_layout);
		dialog.setTitle(getString(R.string.form_layouts));

		final LayoutDialogAdapter adapter = new LayoutDialogAdapter(this, R.layout.layout_row,
				new ArrayList<LayoutRow>());

		List<Layout> menuLayouts = daoFactory.getLayoutMenuItemsDAO().getLayout(newMenu);

		for (String layoutName : layouts) {
			LayoutMenuItems layoutMenuItems = daoFactory.getLayoutMenuItemsDAO()
					.getLayoutMenuItems(newMenu, layoutName);
			adapter.getCount();
			if (layoutMenuItems != null) {
				adapter.add(new LayoutRow(layoutName, true));
			} else {
				adapter.add(new LayoutRow(layoutName, false));
			}
		}
		ListView list = (ListView) dialog.findViewById(R.id.layoutList);
		list.setAdapter(adapter);

		Button cancelButton = (Button) dialog.findViewById(R.id.layout_button_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		Button addButton = (Button) dialog.findViewById(R.id.layout_button_add);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<LayoutRow> list = adapter.getList();

				for (LayoutRow row : list) {
					Layout layout = daoFactory.getLayoutDAO().getLayout(row.getName());
					LayoutMenuItems layoutMenuItems = daoFactory.getLayoutMenuItemsDAO().getLayoutMenuItems(newMenu,
							layout.getName());

					// přidání nového formuláře
					if (row.getChecked()) {
						if (layoutMenuItems == null) {
							// nový formulář (menu item)
							MenuItems menuItem = new MenuItems(layout.getName(), layout, layout.getRootForm(), layout
									.getPreviewMajor(), layout.getPreviewMinor(), newMenu);
							menuItem.setIcon("");
							daoFactory.getMenuItemDAO().saveOrUpdate(menuItem);

							layoutMenuItems = new LayoutMenuItems(layout, newMenu, menuItem);
							daoFactory.getLayoutMenuItemsDAO().saveOrUpdate(layoutMenuItems);

							rootMenu = newMenu;
							updateAdapter();
						}
					}
					// odškrtnutej vymazat
					else {
						if (layoutMenuItems != null) {
							daoFactory.getMenuItemDAO().delete(layoutMenuItems.getMenu().getId());
							daoFactory.getLayoutMenuItemsDAO().delete(layoutMenuItems.getId());
							rootMenu = newMenu;
							updateAdapter();
						}
					}
				}
				dialog.dismiss();
			}
		});

		Button newButton = (Button) dialog.findViewById(R.id.layout_button_new);
		newButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				createFormDialog();
			}
		});

		dialog.show();
	}

	public void createFormDialog() {
		final Context ctx = this;
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.form_add);
		dialog.setTitle(getString(R.string.form_manager));

		final Spinner typeSpinner = (Spinner) dialog.findViewById(R.id.form_spinnerType);
		final Spinner layoutSpinner = (Spinner) dialog.findViewById(R.id.form_spinnerLayout);

		FormTypeSpinnerAdapter typeAdapter = new FormTypeSpinnerAdapter(this, R.layout.spinner_row_simple,
				(ArrayList<Form>) daoFactory.getFormDAO().getForms());
		typeSpinner.setAdapter(typeAdapter);

		typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				Form form = (Form) parent.getItemAtPosition(pos);
				List<Layout> layouts = daoFactory.getFormLayoutsDAO().getLayout(form);
				layoutAdapter = new LayoutSpinnerAdapter(ctx, R.layout.spinner_row_simple, (ArrayList<Layout>) layouts);
				layoutSpinner.setAdapter(layoutAdapter);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// do nothing
			}
		});

		ImageButton newButton = (ImageButton) dialog.findViewById(R.id.form_newForm);
		newButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		Button createForm = (Button) dialog.findViewById(R.id.form_createForm);
		createForm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Form form = (Form) typeSpinner.getSelectedItem();
				Layout layout = null;

				if (layoutSpinner != null) {
					layout = (Layout) layoutSpinner.getSelectedItem();
				}

				StringBuilder error = new StringBuilder();

				if (form == null) {
					error.append(getString(R.string.error_empty_type)).append('\n');
				}

				if (layout == null) {
					error.append(getString(R.string.error_empty_layout)).append('\n');
				} else {
					if (layout.getXmlData() == null) {
						error.append(getString(R.string.error_layout_no_fields)).append('\n');
					}
				}
				if (error.toString().isEmpty()) {
					MenuItems menuItem = new MenuItems(layout.getName(), layout, layout.getRootForm(), layout
							.getPreviewMajor(), layout.getPreviewMinor(), newMenu);
					menuItem.setIcon("");
					daoFactory.getMenuItemDAO().saveOrUpdate(menuItem);
					LayoutMenuItems layoutMenuItems = new LayoutMenuItems(layout, newMenu, menuItem);
					daoFactory.getLayoutMenuItemsDAO().saveOrUpdate(layoutMenuItems);

					rootMenu = newMenu;
					updateAdapter();
					dialog.dismiss();
				} else {
					showAlert(error.toString(), false);
				}
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
						LayoutMenuItems lm = daoFactory.getLayoutMenuItemsDAO().getLayoutMenuItems(menuItem,
								item.getLayout().getName());
						daoFactory.getLayoutMenuItemsDAO().delete(lm.getId());
					}
					daoFactory.getMenuItemDAO().delete(menuItem.getId());
					updateRootAdapter();
				}
				// remove item
				else {
					LayoutMenuItems lm = daoFactory.getLayoutMenuItemsDAO().getLayoutMenuItems(menuItem.getParentId(),
							menuItem.getLayout().getName());
					daoFactory.getLayoutMenuItemsDAO().delete(lm.getId());

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

	/**
	 * Scenario adapter getter. Instance is created in moment of first invocation.
	 * 
	 * @return scenario adapter
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

	public void createForm(View view) {
		User credential = daoFactory.getUserDAO().getUser(rootMenu.getCredential().getId());
		rootMenu.setCredential(credential);
		newMenu = rootMenu;

		/*
		 * if (!ConnectionUtils.isOnline(this)) { showAlert(getString(R.string.error_offline)); return; }
		 */

		String error = ValidationUtils.isWorkspaceValid(this, credential, "test", daoFactory, true);
		if (error.toString().isEmpty() && ConnectionUtils.isOnline(this)) {
			mTaskFragment.startFetchLayouts(rootMenu);
		} else {
			List<Layout> list = daoFactory.getLayoutMenuItemsDAO().getLayoutRootMenu(rootMenu);
			ArrayList<String> layouts = new ArrayList<String>();
			for (Layout layout : list) {
				layouts.add(layout.getName());
			}
			createSelectLayoutDialog(layouts);
		}

	}

	public void editForm(int menuID) {
		Intent intent = new Intent(this, FormAddActivity.class);
		intent.putExtra(MenuItems.ROOT_MENU, menuID);
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

	public void openSettings(View view) {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { super.onCreateOptionsMenu(menu);
	 * getMenuInflater().inflate(R.menu.dashboard_menu, menu); return true; }
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
		inflater.inflate(R.menu.dashboard_menu, menu);

		/*
		 * ListView list = (ListView) v; AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
		 * MenuItems menuItem = (MenuItems) list.getItemAtPosition(acmi.position); menu.add(getString(R.string.edit));
		 * menu.add(getString(R.string.delete)); if (menuItem.getParentId() == null) { }else{
		 * 
		 * }
		 */
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
				editForm(menuItem.getId());
			}

			return true;
		case R.id.menu_delete:
			deleteFolder(menuItem);
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
