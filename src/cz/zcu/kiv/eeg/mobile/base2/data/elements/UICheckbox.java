package cz.zcu.kiv.eeg.mobile.base2.data.elements;

import cz.zcu.kiv.eeg.mobile.base2.data.builders.ViewBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormDetailsFragment;
import android.content.Context;
import android.widget.CheckBox;

public class UICheckbox extends UIElement {
	CheckBox checkbox;
	String data = "";

	public UICheckbox(Field field, LayoutProperty property, DAOFactory daoFactory, Context ctx,
			FormDetailsFragment fragment, ViewBuilder vb) {
		super(field, property, ctx, daoFactory, fragment, vb);
		createCheckbox();
		createWrapLayout();
		setTag();
	}

	private void createCheckbox() {
		checkbox = new CheckBox(ctx);
		element = checkbox;
	}

	@Override
	public void initData(Dataset dataset) {
		Data dataDB = daoFactory.getDataDAO().getData(dataset, field);
		if (dataDB != null) {
			data = dataDB.getData();
		}

		if (data.equalsIgnoreCase("1")) {
			checkbox.setChecked(true);
		}
	}

	public String createOrUpdateData(Dataset dataset) {
		String error = "";
		data = checkbox.isChecked() ? "1" : "0";

		if (daoFactory.getDataDAO().getData(dataset, field) == null) {
			daoFactory.getDataDAO().create(dataset, field, data);
		} else {
			daoFactory.getDataDAO().update(dataset, field, data);
		}
		return error;
	}
}
