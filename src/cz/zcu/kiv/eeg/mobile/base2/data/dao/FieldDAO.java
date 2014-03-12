package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;

public class FieldDAO {

	private DatabaseHelper databaseHelper;

	public FieldDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	private Dao<Field, Integer> getFieldDao() throws SQLException {
		return databaseHelper.getFieldDao();
	}

	public CreateOrUpdateStatus saveOrUpdate(final Field field) {
		try {
			return getFieldDao().createOrUpdate(field);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Field saveOrUpdate(final String name, final String type, final Form form) {
		try {
			Field field = new Field(name, type, form);			
			getFieldDao().createOrUpdate(field);
			return field;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
