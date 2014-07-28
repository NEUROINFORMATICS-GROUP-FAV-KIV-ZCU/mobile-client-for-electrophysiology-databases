package cz.zcu.kiv.eeg.mobile.base2.data.elements;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import android.app.DatePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;

public class UITextbox extends UIElement {
	EditText editText;
	String data = "";
	String type; // nové asi použiji kvůli validaci

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
			final Calendar myCalendar = Calendar.getInstance();
			final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					myCalendar.set(Calendar.YEAR, year);
					myCalendar.set(Calendar.MONTH, monthOfYear);
					myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

					String myFormat = "dd/MM/yyyy";
					SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
					editText.setText(sdf.format(myCalendar.getTime()));
				}

			};
			editText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new DatePickerDialog(ctx, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
							myCalendar.get(Calendar.DAY_OF_MONTH)).show();
				}
			});
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
		//TODO defaultni hodnoty
		/*if (data.equalsIgnoreCase("") && field.getDefaultValue() != null) {
			editText.setText(field.getDefaultValue());
		}*/

		Data dataDB = daoFactory.getDataDAO().getData(dataset, field);

		if (dataDB != null) {
			data = dataDB.getData();
		}
		editText.setText(data);
	}

	public String createOrUpdateData(Dataset dataset) {
		String error = "";
		data = editText.getText().toString();
		
		if(field.getDataType().equalsIgnoreCase(Values.STRING)){
			if(data.length() > field.getMaxLength() && field.getMaxLength() != -1){
				error += property.getLabel() + ": obsahuje moc znaků, maximum je " + field.getMaxLength() + "\n";
			}
			if(data.length() < field.getMinLength() && field.getMinLength() != -1){
				error += property.getLabel() + ": obsahuje málo znaků, minimum je " + field.getMaxLength() + "\n";
			}
		}

		if(error.equalsIgnoreCase("")){
			if (data != null && !data.equals("")) {
				if (daoFactory.getDataDAO().getData(dataset, field) == null) {
					daoFactory.getDataDAO().create(dataset, field, data);
				} else {
					daoFactory.getDataDAO().update(dataset, field, data);
				}
			}
		}
		return error;
	}
}
