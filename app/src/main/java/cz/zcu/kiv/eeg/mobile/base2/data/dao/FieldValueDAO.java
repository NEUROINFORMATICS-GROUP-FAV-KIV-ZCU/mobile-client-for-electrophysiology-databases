package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import cz.zcu.kiv.eeg.mobile.base2.data.model.FieldValue;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FieldValueDAO {

	private DatabaseHelper databaseHelper;

	public FieldValueDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	private Dao<FieldValue, Integer> getFieldValueDao() throws SQLException {
		return null;
	}

	public FieldValue create (final FieldValue fieldValue) {
		try {
			getFieldValueDao().create(fieldValue);
			return fieldValue;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public CreateOrUpdateStatus saveOrUpdate(final FieldValue fieldValue) {
		try {
			return getFieldValueDao().createOrUpdate(fieldValue);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	public List<FieldValue> getFieldValue(final int fieldId) {
		try {
			QueryBuilder<FieldValue, Integer> queryBuilder = getFieldValueDao().queryBuilder();
			return queryBuilder.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<FieldValue> getFieldValues() {
		try {
			return getFieldValueDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
