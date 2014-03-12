package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;
import java.util.Date;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;

public class FormDAO {

	private DatabaseHelper databaseHelper;

	public FormDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	private Dao<Form, String> getFormDao() throws SQLException {
		return databaseHelper.getFormDao();
	}

	public CreateOrUpdateStatus saveOrUpdate(final Form form) {
		try {
			return getFormDao().createOrUpdate(form);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Form saveOrUpdate(final String type, final Date date) {
		try {
			Form form = new Form(type, date);			
			getFormDao().createOrUpdate(form);
			return form;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
