package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;

public class LayoutDAO {

	private DatabaseHelper databaseHelper;

	public LayoutDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	public Dao<Layout, String> getLayoutDao() throws SQLException {
		return databaseHelper.getLayoutDao();
	}

	public CreateOrUpdateStatus saveOrUpdate(Layout layout) {
		try {
			return getLayoutDao().createOrUpdate(layout);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Layout saveOrUpdate(String layoutName, String xmlData) {
		try {
			Layout layout = new Layout(layoutName, xmlData);						
			getLayoutDao().createOrUpdate(layout);
			return layout;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Layout getLayoutByName(final String name) {
		try {
			return getLayoutDao().queryForId(name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
