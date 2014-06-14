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
import cz.zcu.kiv.eeg.mobile.base2.ui.field.FieldAddFragment.FieldAddCallBack;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormDetailsFragment;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.ListAllFormsFragment;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.TabListener;
import cz.zcu.kiv.eeg.mobile.base2.ui.main.DashboardFragment;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FieldAddActivity extends Activity implements FieldAddCallBack{

	private static final String TAG = FieldAddActivity.class.getSimpleName();
	private DAOFactory daoFactory;
	private String formType;
	private String layoutName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		daoFactory = new DAOFactory(this);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);	
		actionBar.setIcon(R.drawable.ic_action_storage);			
						
		if (savedInstanceState != null) {
			formType = savedInstanceState.getString(Form.FORM_TYPE);
			layoutName = savedInstanceState.getString(Layout.LAYOUT_NAME, null);
			
			
		} else {
			FieldAddFragment addFragment = new FieldAddFragment();
			addFragment.setArguments(getIntent().getExtras());

			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(android.R.id.content, addFragment);
			fragmentTransaction.commit();
					
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				formType = extras.getString(Form.FORM_TYPE);
				layoutName = extras.getString(Layout.LAYOUT_NAME);
			}					
		}
	}
	
	public String getFormType() {
		return formType;
	}

	public String getLayoutName() {
		return layoutName;
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);		
		getMenuInflater().inflate(R.menu.field_add_menu, menu);		
		return true;
	}*/
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;				
		}		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(Form.FORM_TYPE, formType);
		outState.putString(Layout.LAYOUT_NAME, layoutName);
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
