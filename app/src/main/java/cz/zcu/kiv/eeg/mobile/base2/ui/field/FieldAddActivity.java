package cz.zcu.kiv.eeg.mobile.base2.ui.field;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.StoreFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FieldAddActivity extends Activity {

	private static final String TAG = FieldAddActivity.class.getSimpleName();
	private StoreFactory store;
	private String formType;
	private String layoutName;
	private ArrayList<Integer> usedFieldsOnLayout;
	private ArrayList<Field> unusedFieldsOnLayout;
	private int previousFragment = -1;
	public int pokus = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		store = new StoreFactory(this);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.ic_action_storage);

		if (savedInstanceState != null) {
			previousFragment = savedInstanceState.getInt("previousFragment", 0);
//			formType = savedInstanceState.getString(Form.FORM_TYPE);
			layoutName = savedInstanceState.getString(Layout.LAYOUT_NAME);
			usedFieldsOnLayout = savedInstanceState.getIntegerArrayList(Values.USED_FIELD);
			setUnusedFields();
			openSection(previousFragment);
		} else {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
//				formType = extras.getString(Form.FORM_TYPE);
				layoutName = extras.getString(Layout.LAYOUT_NAME);
				usedFieldsOnLayout = extras.getIntegerArrayList(Values.USED_FIELD);
				setUnusedFields();
			}
			if (getUnusedCount() > 0) {
				openSection(0);
			} else {
				openSection(1);
			}
		}
	}

	public void openSection(int position) {
		if (position != previousFragment) {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			switch (position) {
			// select field
			case 0:
				FieldSelectFragment selectFrag = new FieldSelectFragment();
				fragmentTransaction.replace(android.R.id.content, selectFrag, FieldSelectFragment.TAG);
				fragmentTransaction.commit();
				previousFragment = position;
				break;
			case 1:
				FieldAddFragment addFrag = new FieldAddFragment();
				fragmentTransaction.replace(android.R.id.content, addFrag, FieldAddFragment.TAG);
				fragmentTransaction.commit();
				previousFragment = position;
				break;
			default:
				break;
			}
		}
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
			break;
		case R.id.field_discard:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
//		outState.putString(Form.FORM_TYPE, formType);
		outState.putString(Layout.LAYOUT_NAME, layoutName);
		outState.putIntegerArrayList(Values.USED_FIELD, usedFieldsOnLayout);
	}

	public ArrayList<Field> getFields() {
		return unusedFieldsOnLayout;
	}

	public void setUnusedFields() {
		unusedFieldsOnLayout = (ArrayList<Field>) store.getFieldStore().getFields(
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
		store.releaseHelper();
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
