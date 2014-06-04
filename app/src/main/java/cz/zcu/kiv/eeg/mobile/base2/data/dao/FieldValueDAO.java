package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FieldValue;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;

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
		return databaseHelper.getFieldValueDao();
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
			queryBuilder.where().eq(FieldValue.FK_ID_FIELD, fieldId);
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
