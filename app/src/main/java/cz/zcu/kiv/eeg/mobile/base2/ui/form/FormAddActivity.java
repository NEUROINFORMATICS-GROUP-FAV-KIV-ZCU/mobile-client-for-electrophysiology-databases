package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.common.TaskFragmentActivity;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FieldSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.LayoutSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.ws.TaskFragment;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FormAddActivity extends TaskFragmentActivity {

	private static final String TAG = FormAddActivity.class.getSimpleName();
	private TaskFragment mTaskFragment;
	private DAOFactory daoFactory;
	private LayoutSpinnerAdapter layoutAdapter;
	private Spinner layoutSpinner;
	private Spinner fieldSpinner1;
	private Spinner fieldSpinner2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "FormAdd screen");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_add);
		daoFactory = new DAOFactory(this);

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.ic_action_event);
		actionBar.setDisplayHomeAsUpEnabled(true);

		FragmentManager fm = getFragmentManager();
		mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG + "Fragment");
		if (mTaskFragment == null) {
			mTaskFragment = new TaskFragment();
			fm.beginTransaction().add(mTaskFragment, "taskFragment").commit();
		}
		initLayoutSpinner();
	}

	private void initLayoutSpinner() {
		layoutAdapter = new LayoutSpinnerAdapter(this, R.layout.spinner_row_simple, (ArrayList<Layout>) daoFactory
				.getLayoutDAO().getLayouts());
		layoutSpinner = (Spinner) findViewById(R.id.form_spinnerLayout);
		layoutSpinner.setAdapter(layoutAdapter);
		layoutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				Layout layout = (Layout) parent.getItemAtPosition(pos);
				initFieldSpinner(daoFactory.getFieldDAO().getFields(layout.getRootForm().getType()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// do nothing
			}
		});
	}

	private void initFieldSpinner(List<Field> fields) {
		FieldSpinnerAdapter fieldAdapter = new FieldSpinnerAdapter(this, R.layout.spinner_row_simple,
				(ArrayList<Field>) fields);
		fieldSpinner1 = (Spinner) findViewById(R.id.form_spinnerDesc1);
		fieldSpinner2 = (Spinner) findViewById(R.id.form_spinnerLayoutDesc2);
		fieldSpinner1.setAdapter(fieldAdapter);
		fieldSpinner2.setAdapter(fieldAdapter);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void fetchLayouts() {
		mTaskFragment.startFetchLayouts(layoutAdapter);
	}

	private void createMenuItem() {
		Toast.makeText(this, "Created new form", Toast.LENGTH_SHORT).show();
		EditText formName = (EditText) findViewById(R.id.form_editName);
		Layout layout = (Layout) layoutSpinner.getSelectedItem();
		Field field1 = (Field) fieldSpinner1.getSelectedItem();
		Field field2 = (Field) fieldSpinner2.getSelectedItem();

		MenuItems menu = new MenuItems(formName.getText().toString(), layout, layout.getRootForm(), field1, field2);
		daoFactory.getMenuItemDAO().saveOrUpdate(menu);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.form_add_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.form_refresh_menu:
			fetchLayouts();
			break;
		case R.id.form_new_form:
			createMenuItem();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		daoFactory.releaseHelper();
	}
}
