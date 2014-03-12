package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormLayouts;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;

public class FormLayoutsDAO {

	private DatabaseHelper databaseHelper;

	public FormLayoutsDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	private Dao<FormLayouts, Integer> getFormDao() throws SQLException {
		return databaseHelper.getFormLayoutsDao();
	}

	public CreateOrUpdateStatus saveOrUpdate(final FormLayouts formLayouts) {
		try {
			return getFormDao().createOrUpdate(formLayouts);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	public CreateOrUpdateStatus saveOrUpdate(final Form form, final Layout layout) {
		try {
			FormLayouts formLayouts = new FormLayouts(form, layout);
			return getFormDao().createOrUpdate(formLayouts);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}	
}
