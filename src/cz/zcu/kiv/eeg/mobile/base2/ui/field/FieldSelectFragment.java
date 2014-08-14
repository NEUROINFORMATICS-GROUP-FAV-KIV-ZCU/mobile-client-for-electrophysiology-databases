package cz.zcu.kiv.eeg.mobile.base2.ui.field;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FieldSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FieldSelectFragment extends Fragment {

	public final static String TAG = FieldSelectFragment.class.getSimpleName();

	private FieldEditorAddActivity activity;
	private FieldSpinnerAdapter fieldAdapter;
	private DAOFactory daoFactory;
	private String layoutName;

	private Spinner fieldSpinner;
	private EditText label;
	private Field field;
	private LayoutProperty property;

	private ArrayList<Integer> newFields;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (FieldEditorAddActivity) activity;
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
		View view = inflater.inflate(R.layout.field_select, container, false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		layoutName = activity.getLayoutName();
		label = (EditText) view.findViewById(R.id.field_edit_Label);
		TextView noUnusedFields =(TextView) view.findViewById(R.id.field_noUnusedFields);
			
		ArrayList<Field> fields = activity.getFields();
		fieldAdapter = new FieldSpinnerAdapter(activity, R.layout.spinner_row_simple, fields);
		
		fieldSpinner = (Spinner) view.findViewById(R.id.field_spinnerField);
		fieldSpinner.setAdapter(fieldAdapter);
		fieldSpinner.setEmptyView(noUnusedFields);
		fieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				field = (Field) parent.getItemAtPosition(pos);
				property = daoFactory.getLayoutPropertyDAO().getProperty(field.getId(), layoutName);
				initFields(field.getName());
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// do nothing
			}
		});
	}

	private void initFields(String name) {
		if(property != null){
			label.setText(property.getLabel());
		}else{
			label.setText(name);
		}
	}

	private void addSelectField(int action) {
		if (!fieldAdapter.isEmpty()) {
			saveChanges();
			fieldAdapter.remove(field);

			newFields.add(field.getId());
			if(action == Values.ACTION_ADD){
				field.setAction(Values.ACTION_ADD);
			}else{
				field.setAction(Values.ACTION_REMOVE);
			}
			
			daoFactory.getFieldDAO().update(field);				

			if (!fieldAdapter.isEmpty()) {
				fieldSpinner.setSelection(0, true);
				field = (Field) fieldSpinner.getItemAtPosition(0);			
				property = daoFactory.getLayoutPropertyDAO().getProperty(field.getId(), layoutName);
				property.setLabel(field.getName());
				initFields(field.getName());								
			} else {
				removeFieldData();
			}
		}
	}

	private void saveChanges() {		
		if(property == null){
			Layout layout = new Layout(layoutName);
			property = new LayoutProperty(field, layout);
		}
		
		property.setLabel(label.getText().toString());
		daoFactory.getLayoutPropertyDAO().saveOrUpdate(property);
	}

	private void removeFieldData() {
		label.setText("");
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
		case R.id.field_discard:
			addSelectField(Values.ACTION_REMOVE);
			activity.finish();
			return true;
		case R.id.form_add_field:
			addSelectField(Values.ACTION_ADD);					
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
