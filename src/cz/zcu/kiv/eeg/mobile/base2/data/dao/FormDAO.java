package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
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
	
	public Form create(final String type) {
		try {
			Form form = new Form(type);
			getFormDao().create(form);
			return form;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Form getForm(final String type) {
		try {
			return getFormDao().queryForId(type);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Form> getForms() {
		try {
			return getFormDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
