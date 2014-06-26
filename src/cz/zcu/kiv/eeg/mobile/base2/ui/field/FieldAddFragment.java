package cz.zcu.kiv.eeg.mobile.base2.ui.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FieldSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FieldValue;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.util.ValidationUtils;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FieldAddFragment extends Fragment {

	public final static String TAG = FieldAddFragment.class.getSimpleName();
	// private FieldEditorAddActivity activity;
	// private FieldAddActivity activity;
	private Activity activity;
	private DAOFactory daoFactory;

	private EditText name;
	private Spinner type;
	private Spinner dataType;
	private EditText label;
	private EditText minLenght;
	private EditText maxLenght;
	private EditText minValue;
	private EditText maxValue;
	private EditText defaultValue;
	private ScrollView textboxScroll;
	private ScrollView comboboxScroll;
	private LinearLayout lengthLayout;
	private LinearLayout valueLayout;
	private LinearLayout comboboxLayout;

	private Field editField;
	private ArrayList<Integer> newFields;
	FieldAddCallBack activityCallBack;

	interface FieldAddCallBack {
		public String getFormType();

		public String getLayoutName();

		public int getFieldId();

		public void showAlert(final String alert);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// this.activity = (FieldEditorAddActivity) activity;
		this.activity = activity;
		try {
			activityCallBack = (FieldAddCallBack) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		newFields = new ArrayList<Integer>();
		daoFactory = new DAOFactory(getActivity());
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.field_add, container, false);
		initView(view);

		int editFieldId = activityCallBack.getFieldId();
		if (editFieldId > 0) {
			editField(editFieldId);
		}
		return view;
	}

	private void initView(View view) {
		label = (EditText) view.findViewById(R.id.field_edit_Label);
		name = (EditText) view.findViewById(R.id.field_edit_name);
		name.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				label.setText(name.getText().toString());
			}
		});

		type = (Spinner) view.findViewById(R.id.field_spinner_type);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.field_type,
				R.layout.spinner_row_simple);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		type.setAdapter(adapter);
		type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				initOptionMenu((String) parent.getItemAtPosition(pos));
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// do nothing
			}
		});

		// option section
		textboxScroll = (ScrollView) view.findViewById(R.id.field_scrollView_optionTextbox);
		comboboxScroll = (ScrollView) view.findViewById(R.id.field_scrollView_optionCombobox);
		lengthLayout = (LinearLayout) view.findViewById(R.id.field_layout_length);
		valueLayout = (LinearLayout) view.findViewById(R.id.field_layout_value);
		comboboxLayout = (LinearLayout) view.findViewById(R.id.field_combobox_layout);
		minLenght = (EditText) view.findViewById(R.id.field_edit_minLength);
		maxLenght = (EditText) view.findViewById(R.id.field_edit_maxLength);
		minValue = (EditText) view.findViewById(R.id.field_edit_minValue);
		maxValue = (EditText) view.findViewById(R.id.field_edit_maxValue);
		defaultValue = (EditText) view.findViewById(R.id.field_edit_defaultValue);
		Button newComboboxItem = (Button) view.findViewById(R.id.field_button_newItem);
		newComboboxItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				newComboboxItem(null);
			}
		});

		dataType = (Spinner) view.findViewById(R.id.field_spinner_datatype);
		ArrayAdapter<CharSequence> adapterDataType = ArrayAdapter.createFromResource(getActivity(),
				R.array.field_datatype, R.layout.spinner_row_simple);
		adapterDataType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dataType.setAdapter(adapterDataType);
		dataType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				initRestrictionProperties((String) parent.getItemAtPosition(pos));
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// do nothing
			}
		});
	}

	private void editField(int fieldId) {
		String layoutName = activityCallBack.getLayoutName();
		editField = daoFactory.getFieldDAO().getField(fieldId);
		LayoutProperty property = daoFactory.getLayoutPropertyDAO().getProperty(fieldId, layoutName);

		name.setText(editField.getName());
		//zakázání editace
		name.setFocusable(false);
		name.setFocusableInTouchMode(false);
		name.setClickable(false);
		name.setEnabled(false);
		label.setText(property.getLabel());
		;

		int position = 0;
		ArrayAdapter<CharSequence> typeAdapter = (ArrayAdapter<CharSequence>) type.getAdapter();
		for (int i = 0; i < typeAdapter.getCount(); i++) {
			if (typeAdapter.getItem(i).toString().equalsIgnoreCase(editField.getType())) {
				position = i;
			}
		}
		type.setSelection(position);

		if (editField.getType().equalsIgnoreCase(Values.TEXTBOX)) {
			ArrayAdapter<CharSequence> dataTypeAdapter = (ArrayAdapter<CharSequence>) dataType.getAdapter();
			for (int i = 0; i < dataTypeAdapter.getCount(); i++) {
				if (dataTypeAdapter.getItem(i).toString().equalsIgnoreCase(editField.getDataType())) {
					position = i;
				}
			}
			dataType.setSelection(position);
			minLenght.setText(String.valueOf(editField.getMinLength()));
			maxLenght.setText(String.valueOf(editField.getMaxLength()));
			minValue.setText(String.valueOf(editField.getMinValue()));
			maxValue.setText(String.valueOf(editField.getMaxValue()));
			defaultValue.setText(editField.getDefaultValue());

		} else if (editField.getType().equalsIgnoreCase(Values.COMBOBOX)) {
			List<FieldValue> values = daoFactory.getFieldValueDAO().getFieldValue(fieldId);
			for (FieldValue value : values) {
				newComboboxItem(value.getValue());
			}
		}
	}

	private void createField() {
		String nameValue = name.getText().toString();
		String typeValue = (String) type.getSelectedItem();
		String dataTypeValue = (String) dataType.getSelectedItem();
		String labelValue = label.getText().toString();
		String formType = activityCallBack.getFormType();
		String layoutName = activityCallBack.getLayoutName();
		String minLengthTmp = minLenght.getText().toString();
		String maxLengthTmp = maxLenght.getText().toString();
		String minValueTmp = minValue.getText().toString();
		String maxValueTmp = maxValue.getText().toString();
		
		int editFieldId = activityCallBack.getFieldId();

		if (ValidationUtils.isEmpty(nameValue)) {
			activityCallBack.showAlert(activity.getString(R.string.field_empty_name));
		} else {
			String message;
			Field field = daoFactory.getFieldDAO().getField(nameValue, formType);
			if (field != null && editFieldId <= 0) {
				activityCallBack.showAlert(activity.getString(R.string.field_exists));
			} else {
				Form form = new Form(formType);
				Field newField = new Field(nameValue, typeValue, form , dataTypeValue);
				
				if(editFieldId > 0){
					newField.setAction(Values.ACTION_EDIT);
				}else{
					newField.setAction(Values.ACTION_ADD);
				}
						
				//newField.setDataType(dataTypeValue);
				// textbox
				if (!minLengthTmp.equalsIgnoreCase("")) {
					newField.setMinLength(Integer.parseInt(minLengthTmp));
				}
				if (!maxLengthTmp.equalsIgnoreCase("")) {
					newField.setMaxLength(Integer.parseInt(maxLengthTmp));
				}
				if (!minValueTmp.equalsIgnoreCase("")) {
					newField.setMinValue(Integer.parseInt(minValueTmp));
				}
				if (!maxValueTmp.equalsIgnoreCase("")) {
					newField.setMaxValue(Integer.parseInt(maxValueTmp));
				}
				newField.setDefaultValue(defaultValue.getText().toString());
				
					
				//editace pole
				if (editFieldId > 0) {		
					newField.setId(editField.getId());
					daoFactory.getFieldDAO().update(newField);
					
					Layout layout = new Layout(layoutName);
					LayoutProperty property = daoFactory.getLayoutPropertyDAO().getProperty(newField.getId(), layout.getName());
					property.setLabel(labelValue);
					daoFactory.getLayoutPropertyDAO().saveOrUpdate(property);
					message  = activity.getString(R.string.field_was_edited);
				}
				//uložení nového pole
				else {	
					newField = daoFactory.getFieldDAO().create(newField);
					Layout layout = new Layout(layoutName);

					LayoutProperty property = new LayoutProperty(newField, layout);
					property.setLabel(labelValue);
					daoFactory.getLayoutPropertyDAO().create(property);
					message  = activity.getString(R.string.field_was_created);
				}

				// combobox
				if (typeValue.equals(Values.COMBOBOX)) {
					int count = comboboxLayout.getChildCount();
					if (count > 1) {
						for (int i = 1; i < count; i++) {
							TextView item = (TextView) comboboxLayout.getChildAt(i);
							daoFactory.getFieldValueDAO().saveOrUpdate(
									new FieldValue(item.getText().toString(), newField));
						}
					}
				}

				clearFields();
				Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
				newFields.add(newField.getId());
			}
		}
	}

	private void initOptionMenu(String typeValue) {
		textboxScroll.setVisibility(View.GONE);
		comboboxScroll.setVisibility(View.GONE);

		if (typeValue.equalsIgnoreCase(Values.TEXTBOX)) {
			textboxScroll.setVisibility(View.VISIBLE);

		} else if (typeValue.equalsIgnoreCase(Values.COMBOBOX)) {
			comboboxScroll.setVisibility(View.VISIBLE);
			initCombobox();
		}
	}

	private void initRestrictionProperties(String dataTypeValue) {
		lengthLayout.setVisibility(View.GONE);
		valueLayout.setVisibility(View.GONE);

		if (dataTypeValue.equalsIgnoreCase(Values.STRING)) {
			lengthLayout.setVisibility(View.VISIBLE);
			defaultValue.setInputType(InputType.TYPE_CLASS_TEXT);

		} else if (dataTypeValue.equalsIgnoreCase(Values.INTEGER)) {
			valueLayout.setVisibility(View.VISIBLE);
			defaultValue.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
	}

	private void initCombobox() {

	}

	private void newComboboxItem(String value) {
		TextView newItem = new TextView(activity);
		if (value == null) {
			newItem.setText(activity.getString(R.string.field_fill_item_name) + value);
		} else {
			newItem.setText(value);
		}
		newItem.setTextAppearance(activity, android.R.style.TextAppearance_Large);
		newItem.setPadding(25, 10, 0, 10);
		newItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				comboboxItemAlertDialog((TextView) v);
			}
		});

		comboboxLayout.addView(newItem);
	}

	public void comboboxItemAlertDialog(final TextView item) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getString(R.string.field_fill_item_name));

		final EditText input = new EditText(activity);
		input.setPadding(25, 50, 0, 10);
		if (!item.getText().toString().equalsIgnoreCase(activity.getString(R.string.field_fill_item_name))) {
			input.setText(item.getText().toString());
		}
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				item.setText(input.getText().toString());
			}
		});
		builder.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setNeutralButton(R.string.dialog_button_remove_item, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				comboboxLayout.removeView(item);
			}
		});

		builder.show();
	}

	private void clearFields() {
		name.setText("");
		label.setText("");
		defaultValue.setText("");
		comboboxLayout.removeAllViews();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem move = menu.findItem(R.id.field_discard);
		if(move != null){
			move.setVisible(false);
		}	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			return false;
		case R.id.field_discard:
			//newField.setAction(Values.ACTION_ADD);
			getActivity().finish();
			return true;
		case R.id.form_add_field:
			createField();
			/*Intent data = new Intent();
			data.putExtra(Field.FIELD_ID, newFields);
			activity.setResult(Activity.RESULT_OK, data);
			getActivity().finish();*/
			return true;
		case R.id.save_changes:
			createField();
			getActivity().finish();			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		daoFactory.releaseHelper();
		super.onDestroy();

	}
}
