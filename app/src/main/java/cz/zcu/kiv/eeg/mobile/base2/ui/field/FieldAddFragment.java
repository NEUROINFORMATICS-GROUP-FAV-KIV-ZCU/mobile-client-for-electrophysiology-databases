package cz.zcu.kiv.eeg.mobile.base2.ui.field;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.StoreFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.util.ValidationUtils;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FieldAddFragment extends Fragment {

	public final static String TAG = FieldAddFragment.class.getSimpleName();
	private FieldAddActivity activity;
	private StoreFactory store;

	private EditText name;
	private Spinner type;
	private EditText label;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (FieldAddActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		store = new StoreFactory(getActivity());
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.field_add, container, false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		name = (EditText) view.findViewById(R.id.field_edit_name);
		label = (EditText) view.findViewById(R.id.field_edit_Label);

		type = (Spinner) view.findViewById(R.id.field_spinner_type);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity, R.array.field_type,
				R.layout.spinner_row_simple); // android.R.layout.simple_spinner_item)
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		type.setAdapter(adapter);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem back = menu.findItem(R.id.field_back);
		if (activity.getUnusedCount() > 0) {
			back.setVisible(true);
		} else {
			back.setVisible(false);
		}
	}

	private void save() {
		String nameValue = name.getText().toString();
		String typeValue = (String) type.getSelectedItem();
		String labelValue = label.getText().toString();
		String formType = activity.getFormType();
		String layoutName = activity.getLayoutName();

		if (ValidationUtils.isEmpty(nameValue)) {
			activity.showAlert(activity.getString(R.string.field_empty_name));
		}
		else{
			Field field = store.getFieldStore().getField(nameValue, formType);
			if (field != null) {
				activity.showAlert(activity.getString(R.string.field_exists));
			} else {
				Form form = new Form(formType);
				Field newField = new Field(nameValue, typeValue, form);
				newField = store.getFieldStore().create(newField);
				Layout layout = new Layout(layoutName);

				LayoutProperty property = new LayoutProperty(newField, layout);
				property.setLabel(labelValue);
				store.getLayoutPropertyStore().create(property);

				Intent data = new Intent();
				data.putExtra(Field.FIELD_ID, newField.getId());
				activity.setResult(Activity.RESULT_OK, data);
				activity.finish();
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			return false;
		case R.id.field_back:
			activity.openSection(0);
			return true;
		case R.id.field_discard:
			activity.finish();
			return true;
		case R.id.field_save:
			save();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		store.releaseHelper();
		super.onDestroy();

	}
}
