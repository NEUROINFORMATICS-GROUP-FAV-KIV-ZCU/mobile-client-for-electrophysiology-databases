package cz.zcu.kiv.eeg.mobile.base2.ui.field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.ui.field.FieldAddFragment.FieldAddCallBack;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.ListAllFormsFragment;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.TabListener;
import cz.zcu.kiv.eeg.mobile.base2.ui.main.DashboardFragment;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FieldEditorAddActivity extends Activity implements FieldAddCallBack{

	private static final String TAG = FieldEditorAddActivity.class.getSimpleName();
	private static final String SELECTED_TAB = "selected_tab";	
	private DAOFactory daoFactory;
	private String formType;
	private String layoutName;
	private ArrayList<Integer> usedFieldsOnLayout;
	private ArrayList<Field> unusedFieldsOnLayout;
	private MenuItems menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		daoFactory = new DAOFactory(this);
		int menuItemID = 0;

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.ic_action_storage);
		actionBar.setDisplayHomeAsUpEnabled(true);	
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		ActionBar.Tab unused = actionBar
				.newTab()
				.setText("Unused field")
				.setTabListener(
						new TabListener<FieldSelectFragment>(this, FieldSelectFragment.class.getSimpleName(),
								FieldSelectFragment.class));
		ActionBar.Tab newField = actionBar
				.newTab()
				.setText("Create new field")
				.setTabListener(
						new TabListener<FieldAddFragment>(this, FieldAddFragment.class.getSimpleName(),
								FieldAddFragment.class));

		actionBar.addTab(unused);
		actionBar.addTab(newField);
				
		if (savedInstanceState != null) {
			formType = savedInstanceState.getString(Form.FORM_TYPE);
			layoutName = savedInstanceState.getString(Layout.LAYOUT_NAME);
			usedFieldsOnLayout = savedInstanceState.getIntegerArrayList(Values.USED_FIELD);
			menuItemID = savedInstanceState.getInt(Values.MENU_ITEM_ID);
			setUnusedFields();
			
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt(SELECTED_TAB, 1));
		} else {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				formType = extras.getString(Form.FORM_TYPE);
				layoutName = extras.getString(Layout.LAYOUT_NAME);
				usedFieldsOnLayout = extras.getIntegerArrayList(Values.USED_FIELD);
				menuItemID = extras.getInt(Values.MENU_ITEM_ID);
				setUnusedFields();
			}
			
			if (getUnusedCount() > 0) {
				actionBar.setSelectedNavigationItem(0);
			} else {
				actionBar.setSelectedNavigationItem(1);
			}
		}	
		menu = daoFactory.getMenuItemDAO().getMenu(menuItemID);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);		
		getMenuInflater().inflate(R.menu.field_add_menu, menu);		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;	
		case R.id.field_discard:
			return false;	
		case R.id.form_add_field:
			return false;		
		}		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(Form.FORM_TYPE, formType);
		outState.putString(Layout.LAYOUT_NAME, layoutName);
		outState.putIntegerArrayList(Values.USED_FIELD, usedFieldsOnLayout);
		outState.putInt(SELECTED_TAB, getActionBar().getSelectedNavigationIndex());	
		outState.putInt(Values.MENU_ITEM_ID, menu.getId());
	}

	public ArrayList<Field> getFields() {
		return unusedFieldsOnLayout;
	}

	public void setUnusedFields() {
		unusedFieldsOnLayout = (ArrayList<Field>) daoFactory.getFieldDAO().getFields(
				(Iterable<Integer>) usedFieldsOnLayout, formType);
	}

	public int getUnusedCount() {
		return unusedFieldsOnLayout.size();
	}

	public String getFormType() {
		return formType;
	}

	public String getLayoutName() {
		return layoutName;
	}
	
	public int getFieldId(){
		return 0;
	}
	
	public MenuItems getMenuItem(){
		return menu;
	}

	public void hideKeyboard(){
		InputMethodManager im = (InputMethodManager) this.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	@Override
	protected void onPause() {
		hideKeyboard();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		daoFactory.releaseHelper();
	}

	/**
	 * Method for showing alert dialog with option, whether activity should be finished after confirmation.
	 * 
	 * @param alert alert message
	 */
	public void showAlert(final String alert) {
		hideKeyboard();
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(alert).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}
}
