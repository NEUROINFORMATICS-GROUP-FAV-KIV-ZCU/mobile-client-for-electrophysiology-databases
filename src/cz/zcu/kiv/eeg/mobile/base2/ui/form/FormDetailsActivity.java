package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FormDetailsActivity extends Activity {

	private int formMode = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			FormDetailsFragment details = new FormDetailsFragment();
			details.setArguments(getIntent().getExtras());

			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(android.R.id.content, details);
			fragmentTransaction.commit();
		}

		// enables up button, sets section icon
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.ic_action_info);
		actionBar.setTitle(getIntent().getExtras().getString(Layout.LAYOUT_ID));
		
		formMode = getIntent().getExtras().getInt(Form.FORM_MODE, Values.FORM_EDIT_DATA);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		if(formMode == Values.FORM_EDIT_LAYOUT){
			getMenuInflater().inflate(R.menu.form_edit_menu, menu);
		}else{
			getMenuInflater().inflate(R.menu.form_detail_menu, menu);
		}
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.form_save_data:
			return false;
		case R.id.form_save_layout:
			return false;
		case R.id.form_move_field:
			return false;
		case R.id.form_add_field:
			return false;
		}
		return false;
	}
}
