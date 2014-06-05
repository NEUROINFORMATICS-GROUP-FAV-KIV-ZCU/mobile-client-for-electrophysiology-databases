package cz.zcu.kiv.eeg.mobile.base2.ui.field;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
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
	//private FieldEditorAddActivity activity;
	//private FieldAddActivity activity;
	private Activity activity;
	private DAOFactory daoFactory;

	private EditText name;
	private Spinner type;
	private EditText label;

	private ArrayList<Integer> newFields;

	FieldAddCallBack activityCallBack;
   
	interface FieldAddCallBack {
	    public String getFormType();
	    public String getLayoutName();
	    public void showAlert(final String alert);
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//this.activity = (FieldEditorAddActivity) activity;
		this.activity =  activity;
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
		return view;
	}

	private void initView(View view) {
		name = (EditText) view.findViewById(R.id.field_edit_name);
		label = (EditText) view.findViewById(R.id.field_edit_Label);

		type = (Spinner) view.findViewById(R.id.field_spinner_type);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.field_type,
				R.layout.spinner_row_simple); // android.R.layout.simple_spinner_item)
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		type.setAdapter(adapter);
		
		Button addButton = (Button) view.findViewById(R.id.field_add_button);
		addButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createField();			
			}
		});
		
	}

	private void createField() {
		String nameValue = name.getText().toString();
		String typeValue = (String) type.getSelectedItem();
		String labelValue = label.getText().toString();
		String formType = activityCallBack.getFormType();
		String layoutName = activityCallBack.getLayoutName();

		if (ValidationUtils.isEmpty(nameValue)) {
			activityCallBack.showAlert(activity.getString(R.string.field_empty_name));
		}
		else{
			Field field = daoFactory.getFieldDAO().getField(nameValue, formType);
			if (field != null) {
				activityCallBack.showAlert(activity.getString(R.string.field_exists));
			} else {
				Form form = new Form(formType);
				Field newField = new Field(nameValue, typeValue, form);
				newField = daoFactory.getFieldDAO().create(newField);
				Layout layout = new Layout(layoutName);

				LayoutProperty property = new LayoutProperty(newField, layout);
				property.setLabel(labelValue);
				daoFactory.getLayoutPropertyDAO().create(property);
				
				clearFields();
				Toast.makeText(activity, activity.getString(R.string.field_was_created), Toast.LENGTH_SHORT).show();
				newFields.add(newField.getId());									
			}
		}
	}

	private void clearFields(){
		name.setText("");
		label.setText("");
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:		
			return false;
		case R.id.field_discard:
			getActivity().finish();
			return true;
		case R.id.field_save:
			Intent data = new Intent();
			data.putExtra(Field.FIELD_ID, newFields);
			activity.setResult(Activity.RESULT_OK, data);
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
