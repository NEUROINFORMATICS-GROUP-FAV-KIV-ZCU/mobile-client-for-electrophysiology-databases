package cz.zcu.kiv.eeg.mobile.base2.data.elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.SpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.ViewBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FieldValue;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormDetailsFragment;
import android.app.DatePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

public class UICombobox extends UIElement{
	Spinner combobox;
	String data = "";
	
	public UICombobox(Field field, LayoutProperty property, DAOFactory daoFactory, Context ctx,
			FormDetailsFragment fragment, ViewBuilder vb) {
		super(field, property, ctx, daoFactory, fragment, vb);
		createEditText();
		createWrapLayout();
		setTag();
	}
	
	private void createEditText() {
		combobox = new Spinner(ctx);
		ArrayList<String> itemList = new ArrayList<String>();

		List<FieldValue> values = daoFactory.getFieldValueDAO().getFieldValue(field.getId());
		for (FieldValue fieldValue : values) {
			itemList.add(fieldValue.getValue());
		}
		
		SpinnerAdapter spinnerAdapter = new SpinnerAdapter(ctx, R.layout.spinner_row_simple, itemList);
		combobox.setAdapter(spinnerAdapter);	
		element = combobox;
	}
	
	@Override
	public void initData(Dataset dataset) {	
		Data dataDB = daoFactory.getDataDAO().getData(dataset, field);

		if (dataDB != null) {
			data = dataDB.getData();
		}	
		SpinnerAdapter spinnerAdapter = (SpinnerAdapter) combobox.getAdapter();
		for (int i = 0; i < spinnerAdapter.getCount(); i++) {
			if (data.equalsIgnoreCase(spinnerAdapter.getItem(i))) {
				combobox.setSelection(i);
			}
		}
	}
	
	public String createOrUpdateData(Dataset dataset) {
		String error = "";
		data = (String) combobox.getSelectedItem();
				
		if (data != null && !data.equals("")) {
			if (daoFactory.getDataDAO().getData(dataset, field) == null) {
				daoFactory.getDataDAO().create(dataset, field, data);
			} else {
				daoFactory.getDataDAO().update(dataset, field, data);
			}
		}
		return error;
	}
}
