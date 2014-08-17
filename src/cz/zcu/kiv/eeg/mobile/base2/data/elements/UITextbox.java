package cz.zcu.kiv.eeg.mobile.base2.data.elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.ViewBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.DataDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormDetailsFragment;
import cz.zcu.kiv.eeg.mobile.base2.util.ValidationUtils;
import android.app.DatePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;

public class UITextbox extends UIElement {
	EditText editText;
	String data = "";
	String type;
	
	SimpleDateFormat dtFormat = new SimpleDateFormat("E MMM dd k:m:s z yyyy", Locale.US);
	SimpleDateFormat dtFormat2 = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
	Date date;

	public UITextbox(Field field, LayoutProperty property, DAOFactory daoFactory, Context ctx,
			FormDetailsFragment fragment, ViewBuilder vb) {
		super(field, property, ctx, daoFactory, fragment, vb);
		createEditText();
		createWrapLayout();
		setTag();
	}

	private void createEditText() {
		editText = new EditText(ctx);
		type = field.getDataType();

		if (type.equalsIgnoreCase(Values.DATE)) {
			editText.setInputType(InputType.TYPE_NULL);
			createDateDialog();
		} else if (type.equalsIgnoreCase(Values.STRING)) {
			editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		} else if (type.equalsIgnoreCase(Values.INTEGER)) {
			editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else if (type.equalsIgnoreCase(Values.EMAIL)) {
			editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		}
		
		element = editText;
	}

	@Override
	public void initData(Dataset dataset) {
		Data dataDB = daoFactory.getDataDAO().getData(dataset, field);

		if (dataDB != null && !dataDB.getData().equals("")) {
			data = dataDB.getData();
			if (field.getDataType().equalsIgnoreCase(Values.DATE)) {
				try {					
					date = dtFormat.parse(data);				
					data = dtFormat2.format(date);						
					createDateDialog();
				} catch (ParseException e) {					
					e.printStackTrace();
				}			
			}

		} else {
			data = field.getDefaultValue();
			if (field.getDataType().equalsIgnoreCase(Values.DATE)) {
				createDateDialog();
			}
		}
		editText.setText(data);
	}

	public String createOrUpdateData(Dataset dataset) {
		String error = "";
		boolean isError = false;
		data = editText.getText().toString();

		if (field.getDataType().equalsIgnoreCase(Values.STRING)) {
			if (data.length() > field.getMaxLength() && field.getMaxLength() != -1) {
				error += property.getLabel() + fragment.getString(R.string.error_field_max_length)
						+ field.getMaxLength() + "\n";
				isError = true;
			}
			if (data.length() < field.getMinLength() && field.getMinLength() != -1) {
				error += property.getLabel() + fragment.getString(R.string.error_field_min_length)
						+ field.getMinLength() + "\n";
				isError = true;
			}
		}

		else if (field.getDataType().equalsIgnoreCase(Values.INTEGER)) {
			data = data.equals("") ? "0" : data;
			if (Integer.parseInt(data) > field.getMaxValue() && field.getMaxValue() != -1) {
				error += property.getLabel() + fragment.getString(R.string.error_field_max_value) + field.getMaxValue()
						+ "\n";
				isError = true;
			}
			if (Integer.parseInt(data) < field.getMinValue() && field.getMinValue() != -1) {
				error += property.getLabel() + fragment.getString(R.string.error_field_min_value) + field.getMinValue()
						+ "\n";
				isError = true;
			}
		}

		else if (field.getDataType().equalsIgnoreCase(Values.EMAIL)) {
			if (!ValidationUtils.isEmailValid(data)) {
				error += property.getLabel() + fragment.getString(R.string.error_invalid_mail_format) + "\n";
				isError = true;
			}
		}

		/*else if (field.getDataType().equalsIgnoreCase(Values.DATE)) {
				if (!ValidationUtils.isDateValid(data, "dd/MM/yyyy")) {
					error += property.getLabel() + fragment.getString(R.string.error_invalid_date_format) + "\n";
					isError = true;
				}			
		}*/

		if (!isError) {
			if (data != null) {
				if (daoFactory.getDataDAO().getData(dataset, field) == null) {
					daoFactory.getDataDAO().create(dataset, field, data);
				} else {
					daoFactory.getDataDAO().update(dataset, field, data);
				}
			}
		}
		return error;
	}
	
	private void createDateDialog(){
		final Calendar myCalendar;
		if(date == null){
			 myCalendar = Calendar.getInstance();
		}else{		 
			myCalendar = Calendar.getInstance();
			myCalendar.setTime(date);					
		}
		
		editText.setFocusable(false);	
		
		final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);			
				editText.setText(dtFormat2.format(myCalendar.getTime()));
			}
		};
		if (vb.getFormMode() != Values.FORM_EDIT_LAYOUT) {
			editText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new DatePickerDialog(ctx, dateListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
							myCalendar.get(Calendar.DAY_OF_MONTH)).show();
				}
			});

			editText.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {				
							new DatePickerDialog(ctx, dateListener, myCalendar.get(Calendar.YEAR), myCalendar
									.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();								
					} else {
						// Hide your calender here
					}
				}
			});
		}
	}
}
