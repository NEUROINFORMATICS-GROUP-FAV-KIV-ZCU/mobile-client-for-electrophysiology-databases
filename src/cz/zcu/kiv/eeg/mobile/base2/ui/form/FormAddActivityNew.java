package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.app.ActionBar;
import android.app.Activity;
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
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.common.TaskFragmentActivity;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FieldSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormTypeSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.LayoutSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.OdmlBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.ViewBuilder;
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
import cz.zcu.kiv.eeg.mobile.base2.util.ValidationUtils;
import cz.zcu.kiv.eeg.mobile.base2.ws.TaskFragment;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FormAddActivityNew extends TaskFragmentActivity {

	private static final String TAG = FormAddActivityNew.class.getSimpleName();
	private DAOFactory daoFactory;

	private LayoutSpinnerAdapter layoutAdapter;
	private FormTypeSpinnerAdapter typeAdapter;

	private Spinner typeSpinner;
	private Spinner layoutSpinner;

	private MenuItems rootMenu;
	private boolean isSubform = false; // jestli je formulář volán při přidávání pole

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "FormAdd screen");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_add_dialog);
		daoFactory = new DAOFactory(this);

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.ic_action_event);
		actionBar.setDisplayHomeAsUpEnabled(true);

		int menuItemID = -1;

		if (savedInstanceState != null) {
			menuItemID = savedInstanceState.getInt(MenuItems.ROOT_MENU, -1);
			isSubform = savedInstanceState.getBoolean(Values.SUBFORM);
		} else {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				menuItemID = extras.getInt(MenuItems.ROOT_MENU, -1);
				isSubform = extras.getBoolean(Values.SUBFORM, false);
			}
		}

		rootMenu = daoFactory.getMenuItemDAO().getMenu(menuItemID);

		typeSpinner = (Spinner) findViewById(R.id.form_spinnerType);
		layoutSpinner = (Spinner) findViewById(R.id.form_spinnerLayout);

		ImageButton newForm = (ImageButton) findViewById(R.id.form_newForm);
		newForm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createDialog(getString(R.string.form_create_type_title), Values.TYPE_MODE);
			}
		});

		ImageButton newLayout = (ImageButton) findViewById(R.id.form_newLayout);
		newLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createDialog(getString(R.string.form_create_layout_title), Values.LAYOUT_MODE);
			}
		});

		Button createForm = (Button) findViewById(R.id.form_createForm);
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
					if (isSubform) {
						Intent data = new Intent();
						data.putExtra(Values.SUBFORM, form.getType());
						data.putExtra(Values.SUBLAYOUT, layout.getName());
						setResult(Activity.RESULT_OK, data);
						finish();
					} else {
						MenuItems menuItem = new MenuItems(layout.getName(), layout, layout.getRootForm(), layout
								.getPreviewMajor(), layout.getPreviewMinor(), rootMenu);
						menuItem.setIcon("");
						daoFactory.getMenuItemDAO().saveOrUpdate(menuItem);						
						close();
					}
				} else {
					showAlert(error.toString(), false);
				}
			}
		});

		initTypeSpinner();

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void close() {
		this.finish();
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
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// do nothing
			}
		});

	}

	private void initLayoutSpinner(Form form) {
		List<Layout> layouts = daoFactory.getFormLayoutsDAO().getLayout(form, daoFactory);
		layoutAdapter = new LayoutSpinnerAdapter(this, R.layout.spinner_row_simple, (ArrayList<Layout>) layouts);
		layoutSpinner.setAdapter(layoutAdapter);
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
					addItemToTypeSpinner(text.trim());
				} else if (mode == Values.LAYOUT_MODE) {
					addItemToLayoutSpinner(text.trim());
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

	private void addItemToTypeSpinner(String type) {
		if (daoFactory.getFormDAO().getForm(type) != null) {
			showAlert(getString(R.string.error_form_type_exists), false);
		} else if (ValidationUtils.isEmpty(type)) {
			showAlert(getString(R.string.error_empty_type), false);
		} else {
			Form form = daoFactory.getFormDAO().create(type);
			typeAdapter.add(form);
			typeSpinner.setSelection(typeAdapter.getPosition(form));

			addItemToLayoutSpinner(type + getString(R.string.form_layout_generated));

			/*
			 * Layout layout = addItemToLayoutSpinner(type + getString(R.string.form_layout_generated)); Field field =
			 * new Field(getString(R.string.field_description), "Textbox", form, Values.STRING); field =
			 * daoFactory.getFieldDAO().create(field);
			 * 
			 * LayoutProperty property = new LayoutProperty(field, layout);
			 * property.setLabel(getString(R.string.field_description));
			 * daoFactory.getLayoutPropertyDAO().create(property);
			 * 
			 * OdmlBuilder.createDefaultODML(field, layout, property, daoFactory, this);
			 */
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
			Field field = daoFactory.getFieldDAO().getField(getString(R.string.field_description), form.getType());
			Field field2 = daoFactory.getFieldDAO().getField(getString(R.string.field_description_u), form.getType());
			if(field == null && field2 == null){
				field = new Field(getString(R.string.field_description), Values.TEXTBOX, form, Values.STRING);
				field = daoFactory.getFieldDAO().create(field);
			}else{
				if(field == null){
					field = field2;
				}
			}
			
			Layout layout = daoFactory.getLayoutDAO().create(name, null, form, null, field);
			daoFactory.getFormLayoutsDAO().create(form, layout);

			LayoutProperty property = new LayoutProperty(field, layout);
			property.setIdNode(1);
			property.setLabel(getString(R.string.field_description));
			daoFactory.getLayoutPropertyDAO().create(property);

			OdmlBuilder.createDefaultODML(field, layout, property, daoFactory, this);

			if (layoutAdapter == null) {
				initLayoutSpinner(form);
			}
			layoutAdapter.add(layout);
			layoutSpinner.setSelection(layoutAdapter.getPosition(layout));
			return layout;
		}
		return null;
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(MenuItems.ROOT_MENU, rootMenu.getId());
		outState.putBoolean(Values.SUBFORM, isSubform);
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
