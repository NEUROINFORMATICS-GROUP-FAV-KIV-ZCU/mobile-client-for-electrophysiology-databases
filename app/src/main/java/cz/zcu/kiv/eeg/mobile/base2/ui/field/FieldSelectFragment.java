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
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FieldSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FieldSelectFragment extends Fragment {

	public final static String TAG = FieldSelectFragment.class.getSimpleName();

	private FieldAddActivity activity;
	private FieldSpinnerAdapter fieldAdapter;
	private DAOFactory daoFactory;
	private String layoutName;

	private Spinner fieldSpinner;
	private EditText label;
	private Field field;
	private LayoutProperty property;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (FieldAddActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		ArrayList<Field> fields = activity.getFields();
		fieldAdapter = new FieldSpinnerAdapter(activity, R.layout.spinner_row_simple, fields);
		fieldSpinner = (Spinner) view.findViewById(R.id.field_spinnerField);
		fieldSpinner.setAdapter(fieldAdapter);
		fieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				field = (Field) parent.getItemAtPosition(pos);
				property = daoFactory.getLayoutPropertyDAO().getProperty(field.getId(), layoutName);
				initFields();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// do nothing
			}
		});

		Button button = (Button) view.findViewById(R.id.field_buttonNewField);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.openSection(1);
			}
		});
	}

	private void initFields() {
		label.setText(property.getLabel());	
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem back = menu.findItem(R.id.field_back);
		back.setVisible(false);
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
			activity.finish();
			return true;
		case R.id.field_save:
			property.setLabel(label.getText().toString());
			daoFactory.getLayoutPropertyDAO().saveOrUpdate(property);
			Intent data = new Intent();
		    data.putExtra(Field.FIELD_ID, field.getId());
		    activity.setResult(Activity.RESULT_OK, data);
		    activity.finish();
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
