package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.QueryBuilder;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FieldLayouts;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FieldLayouts;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FieldLayoutsDAO {

	private DatabaseHelper databaseHelper;

	public FieldLayoutsDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	private Dao<FieldLayouts, Integer> getFieldLayoutDao() throws SQLException {
		return databaseHelper.getFieldLayoutsDao();
	}
	
	private Dao<Layout, String> getLayoutDao() throws SQLException {
		return databaseHelper.getLayoutDao();
	}
	
	private Dao<Field, Integer> getFieldDao() throws SQLException {
		return databaseHelper.getFieldDao();
	}

	public CreateOrUpdateStatus saveOrUpdate(final FieldLayouts fieldLayouts) {
		try {
			return getFieldLayoutDao().createOrUpdate(fieldLayouts);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public CreateOrUpdateStatus saveOrUpdate(final Field field, final Layout layout) {
		try {
			FieldLayouts fieldLayouts = new FieldLayouts(field, layout);
			return getFieldLayoutDao().createOrUpdate(fieldLayouts);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Layout> getLayout(final Field field) {
		try {
			QueryBuilder<FieldLayouts, Integer> fieldLayoutsQb = getFieldLayoutDao().queryBuilder();
			fieldLayoutsQb.where().eq(FieldLayouts.FK_ID_FIELD, field);
			QueryBuilder<Layout, String> layoutQb = getLayoutDao().queryBuilder();
			// join with the order query
			List<Layout> results = layoutQb.join(fieldLayoutsQb).query();
			
					
			return results;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Field> getFields(final Layout layout) {
		try {
			QueryBuilder<FieldLayouts, Integer> fieldLayoutsQb = getFieldLayoutDao().queryBuilder();
			fieldLayoutsQb.where().eq(FieldLayouts.FK_ID_LAYOUT, layout);
			QueryBuilder<Field, Integer> layoutQb = getFieldDao().queryBuilder();
			// join with the order query
			List<Field> results = layoutQb.join(fieldLayoutsQb).query();
							
			return results;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<FieldLayouts> getFieldLayouts() {
		try {
			return getFieldLayoutDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
