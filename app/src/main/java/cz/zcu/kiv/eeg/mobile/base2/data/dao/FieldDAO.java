package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FieldDAO {

	private DatabaseHelper databaseHelper;

	public FieldDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	private Dao<Field, Integer> getFieldDao() throws SQLException {
		return databaseHelper.getFieldDao();
	}

	public Field create(final Field field) {
		try {
			getFieldDao().create(field);
			return field;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void update(final Field field) {
		try {
			getFieldDao().update(field);	
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	public Field getField(final int id) {
		try {
			return getFieldDao().queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Field getField(final String name, final String formId) {
		try {
			QueryBuilder<Field, Integer> queryBuilder = getFieldDao().queryBuilder();
			Where<Field, Integer> where = queryBuilder.where();
			where.eq("form_id", formId);
			where.and();
			where.eq("name", name);
			PreparedQuery<Field> preparedQuery = queryBuilder.prepare();
			return getFieldDao().queryForFirst(preparedQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Field> getFields(final String rootForm) {
		try {
			QueryBuilder<Field, Integer> queryBuilder = getFieldDao().queryBuilder();
			queryBuilder.where().eq("form_id", rootForm);
			return queryBuilder.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public List<Field> getFields(final Iterable<Integer> notIn, final String formType) {
		try {
			QueryBuilder<Field, Integer> queryBuilder = getFieldDao().queryBuilder();
			queryBuilder.where().notIn("id", notIn).and().eq("form_id", formType);
			return queryBuilder.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Field> getFields() {
		try {
			return getFieldDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
