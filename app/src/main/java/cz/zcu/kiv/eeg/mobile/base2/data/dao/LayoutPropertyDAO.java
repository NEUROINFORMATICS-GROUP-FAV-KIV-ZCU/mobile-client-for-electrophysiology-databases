package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

import java.sql.SQLException;
import java.util.List;

import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class LayoutPropertyDAO {

	private DatabaseHelper databaseHelper;

	public LayoutPropertyDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	private Dao<LayoutProperty, Integer> getLayoutPropertyDao() throws SQLException {
		return null;
	}

	public LayoutProperty create(final LayoutProperty property) {
		try {
			getLayoutPropertyDao().create(property);
			return property;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public CreateOrUpdateStatus saveOrUpdate(final LayoutProperty property) {
		try {
			return getLayoutPropertyDao().createOrUpdate(property);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public LayoutProperty getProperty(final int id) {
		try {
			return getLayoutPropertyDao().queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*public LayoutProperty getProperty(final int fieldId, final int layoutId) {
		try {
			QueryBuilder<LayoutProperty, Integer> queryBuilder = getLayoutPropertyDao().queryBuilder();
			Where<LayoutProperty, Integer> where = queryBuilder.where();
			where.eq(LayoutProperty.FK_ID_FIELD, fieldId);
			where.and();
			where.eq(LayoutProperty.FK_ID_LAYOUT, layoutId);
			PreparedQuery<LayoutProperty> preparedQuery = queryBuilder.prepare();
			return getLayoutPropertyDao().queryForFirst(preparedQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}*/
	
	public List<LayoutProperty> getProperties() {
		try {
			return getLayoutPropertyDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
