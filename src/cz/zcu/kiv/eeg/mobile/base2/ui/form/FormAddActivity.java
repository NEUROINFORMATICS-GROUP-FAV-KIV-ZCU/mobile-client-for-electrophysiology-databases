package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.common.TaskFragmentActivity;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FieldSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormTypeSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.LayoutSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.OdmlBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormLayouts;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;
import cz.zcu.kiv.eeg.mobile.base2.ui.field.FieldAddActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.field.FieldEditorAddActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.settings.LoginActivity;
import cz.zcu.kiv.eeg.mobile.base2.util.ValidationUtils;
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
	private FormTypeSpinnerAdapter typeAdapter;
	private FieldSpinnerAdapter fieldAdapter;
	private Spinner typeSpinner;
	private Spinner layoutSpinner;
	private Spinner fieldSpinner;
	private Spinner previewMajor;
	private Spinner previewMinor;
	private MenuItems menu;

	private boolean isNewField = false;
	private boolean isEditLayout = false;
	private boolean isInitEditForm = false;
	private boolean isEditForm = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "FormAdd screen");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_add);
		daoFactory = new DAOFactory(this);

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.ic_action_event);
		actionBar.setDisplayHomeAsUpEnabled(true);

		int rootMenuId;
		if (savedInstanceState == null) {
			rootMenuId = getIntent().getExtras().getInt(MenuItems.ROOT_MENU);
		} else {
			rootMenuId = savedInstanceState.getInt(MenuItems.ROOT_MENU);
		}
		menu = daoFactory.getMenuItemDAO().getMenu(rootMenuId);

		FragmentManager fm = getFragmentManager();
		mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG + "Fragment");
		if (mTaskFragment == null) {
			mTaskFragment = new TaskFragment();
			fm.beginTransaction().add(mTaskFragment, "taskFragment").commit();
		}
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		typeSpinner = (Spinner) findViewById(R.id.form_spinnerType);
		layoutSpinner = (Spinner) findViewById(R.id.form_spinnerLayout);
		fieldSpinner = (Spinner) findViewById(R.id.form_spinnerFields);
		previewMajor = (Spinner) findViewById(R.id.form_spinnerPreviewMajor);
		previewMinor = (Spinner) findViewById(R.id.form_spinnerPreviewMinor);
		initTypeSpinner();
		editForm();
	}

	@Override
	protected void onResume() {
		if (isNewField) {
			Form form = (Form) typeSpinner.getSelectedItem();
			initFieldSpinner(daoFactory.getFieldDAO().getFields(form.getType()));
			isNewField = false;
		}
		if (isEditLayout) {
			Form form = (Form) typeSpinner.getSelectedItem();
			initLayoutSpinner(form);
			isEditLayout = false;
		}
		super.onResume();
	}

	private void editForm() {
		if (menu.getParentId() != null) {
			Form form = daoFactory.getFormDAO().getForm(menu.getRootForm().getType());
			int position = typeAdapter.getPosition(form);

			typeSpinner.setSelection(position);

			initLayoutSpinner(form);
			Layout layout = daoFactory.getLayoutDAO().getLayout(menu.getLayout().getName());
			position = layoutAdapter.getPosition(layout);
			layoutSpinner.setSelection(position);

			initFieldSpinner(daoFactory.getFieldDAO().getFields(form.getType()));
			FieldSpinnerAdapter adapter = (FieldSpinnerAdapter) previewMajor.getAdapter();
			position = adapter.getPosition(menu.getPreviewMajor());
			previewMajor.setSelection(position);

			FieldSpinnerAdapter adapter2 = (FieldSpinnerAdapter) previewMinor.getAdapter();
			position = adapter2.getPosition(menu.getPreviewMinor());
			previewMinor.setSelection(position);

			EditText formName = (EditText) findViewById(R.id.form_editName);
			formName.setText(menu.getName());
			isEditForm = true;
			isInitEditForm = true;
		}
	}

	private void initTypeSpinner() {
		typeAdapter = new FormTypeSpinnerAdapter(this, R.layout.spinner_row_simple, (ArrayList<Form>) daoFactory
				.getFormDAO().getForms());
		typeSpinner.setAdapter(typeAdapter);
		typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

				Form form = (Form) parent.getItemAtPosition(pos);
				initLayoutSpinner(form);
				if (!isInitEditForm) {
					initFieldSpinner(daoFactory.getFieldDAO().getFields(form.getType()));
				}
				isInitEditForm = false;
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// do nothing
			}
		});
		
	}

	private void initLayoutSpinner(Form form) {
		List<Layout> layouts = daoFactory.getFormLayoutsDAO().getLayout(form);

		layoutAdapter = new LayoutSpinnerAdapter(this, R.layout.spinner_row_simple, (ArrayList<Layout>) layouts);
		layoutSpinner.setAdapter(layoutAdapter);
		layoutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// Layout layout = (Layout) parent.getItemAtPosition(pos);
				// initFieldSpinner(daoFactory.getFieldDAO().getFields(layout.getRootForm().getType()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// do nothing
			}
		});
	}

	private void initFieldSpinner(List<Field> fields) {
		fieldAdapter = new FieldSpinnerAdapter(this, R.layout.spinner_row_simple, (ArrayList<Field>) fields);
		fieldSpinner.setAdapter(fieldAdapter);

		FieldSpinnerAdapter previewAdapter = new FieldSpinnerAdapter(this, R.layout.spinner_row_simple,
				(ArrayList<Field>) fields);
		previewMajor.setAdapter(previewAdapter);
		FieldSpinnerAdapter previewAdapter2 = new FieldSpinnerAdapter(this, R.layout.spinner_row_simple,
				(ArrayList<Field>) fields);
		previewMinor.setAdapter(previewAdapter2);
	}

	private void fetchLayouts() {
		User user = null;//daoFactory.getUserDAO().getUser();
		//User user = daoFactory.getUserDAO().getUser();  //TODO
		if (user == null || user.getFirstName() == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			Toast.makeText(this, "You must login first", Toast.LENGTH_SHORT).show();
		} else {
			///mTaskFragment.startFetchLayouts(typeAdapter); todo
		}
	}

	public void createType(View view) {
		createDialog(getString(R.string.form_create_type_title), Values.TYPE_MODE);
	}

	public void createLayout(View view) {
		createDialog(getString(R.string.form_create_layout_title), Values.LAYOUT_MODE);
	}

	public void createFields(View view) {
		Form form = (Form) typeSpinner.getSelectedItem();
		if (form == null || ValidationUtils.isEmpty(form.getType())) {
			showAlert(getString(R.string.error_empty_type), false);
		} else {
			isNewField = true;
			Intent intent = new Intent(this, FieldAddActivity.class);
			intent.putExtra(Form.FORM_TYPE, form.getType());
			startActivity(intent);
		}
	}

	public void editField(View view) {
		showAlert("Not implemented yet", false);
	}

	public void editLayout(View view) {
		Form form = (Form) typeSpinner.getSelectedItem();
		Layout layout = (Layout) layoutSpinner.getSelectedItem();

		if (form == null || ValidationUtils.isEmpty(form.getType())) {
			showAlert(getString(R.string.error_empty_type), false);
		} else if (layout == null || ValidationUtils.isEmpty(form.getType())) {
			showAlert(getString(R.string.error_empty_layout), false);
		} else {
			Intent intentEdit = new Intent(this, FormDetailsActivity.class);
			intentEdit.putExtra(Values.MENU_ITEM_ID, menu.getId());
			intentEdit.putExtra(Values.MENU_ITEM_NAME, menu.getName());
			intentEdit.putExtra(Layout.LAYOUT_ID, layout.getName());
			intentEdit.putExtra(Form.FORM_MODE, Values.FORM_EDIT_LAYOUT);
			startActivity(intentEdit);
			isEditLayout = true;
		}
	}

	public void duplicateLayout(View view) {
		// createDialog(getString(R.string.form_create_layout_title), Values.LAYOUT_MODE);
		showAlert("Not implemented yet", false);
	}

	private void addItemToTypeSpinner(String type) {
		if (daoFactory.getFormDAO().getForm(type) != null) {
			showAlert(getString(R.string.error_form_type_exists), false);
		} else if (ValidationUtils.isEmpty(type)) {
			showAlert(getString(R.string.error_empty_type), false);
		} else {
			Form form = daoFactory.getFormDAO().create(type);
			typeAdapter.add(form);
			typeSpinner.setSelection(typeAdapter.getPosition(form));

			Layout layout = addItemToLayoutSpinner(type + getString(R.string.form_layout_generated));
			Field field = new Field(getString(R.string.field_description), "Textbox", form);
			daoFactory.getFieldDAO().create(field);
			fieldAdapter.add(field);
			fieldSpinner.setSelection(fieldAdapter.getPosition(field));
			initFieldSpinner(daoFactory.getFieldDAO().getFields(form.getType()));

			LayoutProperty property = new LayoutProperty(field, layout);
			property.setLabel(getString(R.string.field_description));
			daoFactory.getLayoutPropertyDAO().create(property);

			EditText formName = (EditText) findViewById(R.id.form_editName);
			formName.setText(type);

			OdmlBuilder.createDefaultODML(field, layout, property, daoFactory);
		}
	}

	private Layout addItemToLayoutSpinner(String name) {
		Form form = (Form) typeSpinner.getSelectedItem();

		if (daoFactory.getLayoutDAO().getLayout(name) != null) {
			showAlert(getString(R.string.error_layout_name_exists), false);
		} else if (ValidationUtils.isEmpty(name)) {
			showAlert(getString(R.string.error_empty_layout), false);
		} else if (form == null || ValidationUtils.isEmpty(form.getType())) {
			showAlert(getString(R.string.error_empty_type), false);
		} else {
			Layout layout = daoFactory.getLayoutDAO().create(name, null, form, null, null);
			daoFactory.getFormLayoutsDAO().saveOrUpdate(form, layout);
			layoutAdapter.add(layout);
			layoutSpinner.setSelection(layoutAdapter.getPosition(layout));
			return layout;
		}
		return null;
	}

	public void createDialog(String title, final int mode) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		builder.setTitle(title);

		final EditText input = new EditText(this);
		input.setPadding(25, 50, 0, 10);
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String text = input.getText().toString();
				if (mode == Values.TYPE_MODE) {
					addItemToTypeSpinner(text);
				} else if (mode == Values.LAYOUT_MODE) {
					addItemToLayoutSpinner(text);
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
	 * Method for showing alert dialog with option, whether activity should be finished after confirmation.
	 * 
	 * @param alert alert message
	 * @param closeActivity close activity on confirmation
	 */
	public void showAlert(final String alert, final boolean closeActivity) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(alert).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				if (closeActivity) {
					finish();
				}
			}
		});
		builder.create().show();
	}

	private void createMenuItem() {
		EditText formName = (EditText) findViewById(R.id.form_editName);

		Form form = (Form) typeSpinner.getSelectedItem();
		Layout layout = null;
		Field prevMajor = null;
		Field prevMinor = null;

		if (layoutSpinner != null) {
			layout = (Layout) layoutSpinner.getSelectedItem();
		}
		if (previewMajor != null) {
			prevMajor = (Field) previewMajor.getSelectedItem();
		}
		if (previewMinor != null) {
			prevMinor = (Field) previewMinor.getSelectedItem();
		}

		StringBuilder error = new StringBuilder();

		if (form == null) {
			error.append(getString(R.string.error_empty_type)).append('\n');
		}
		if (ValidationUtils.isEmpty(formName.getText().toString())) {
			error.append(getString(R.string.error_empty_form_name)).append('\n');
		}
		if (layout == null) {
			error.append(getString(R.string.error_empty_layout)).append('\n');
		} else {
			if (layout.getXmlData() == null) {
				error.append(getString(R.string.error_layout_no_fields)).append('\n');
			}
		}
		if (error.toString().isEmpty()) {
			MenuItems menuItem;
			if (isEditForm) {
				menuItem = menu;
				menuItem.setName(formName.getText().toString());
				menuItem.setLayout(layout);
				menuItem.setRootForm(form);
				menuItem.setPreviewMajor(prevMajor);
				menuItem.setPreviewMinor(prevMinor);
			} else {
				menuItem = new MenuItems(formName.getText().toString(), layout, form, prevMajor, prevMinor, menu);
			}

			// menu.setIcon(Values.ICON_COPY);
			menuItem.setIcon("");
			daoFactory.getMenuItemDAO().saveOrUpdate(menuItem);
			finish();
		} else {
			showAlert(error.toString(), false);
		}

		/*
		 * Toast.makeText(this, "Created new form", Toast.LENGTH_SHORT).show(); EditText formName = (EditText)
		 * findViewById(R.id.form_editName); Layout layout = (Layout) layoutSpinner.getSelectedItem(); Field field1 =
		 * (Field) previewMajor.getSelectedItem(); Field field2 = (Field) previewMinor.getSelectedItem();
		 * 
		 * MenuItems menu = new MenuItems(formName.getText().toString(), layout, layout.getRootForm(), field1, field2);
		 * daoFactory.getMenuItemDAO().saveOrUpdate(menu);
		 */
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
		case R.id.form_save:
			createMenuItem();
			break;
		case R.id.form_discard:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(MenuItems.ROOT_MENU, menu.getId());
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		daoFactory.releaseHelper();
	}
}
