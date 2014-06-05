package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;
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

	public Layout saveOrUpdate(String layoutName, Layout rootLayout, String xmlData, Form rootForm) {
		try {
			Layout layout = new Layout(layoutName, xmlData, rootLayout, rootForm);
			getLayoutDao().createOrUpdate(layout);
			return layout;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Layout getLayout(final String name) {
		try {
			return getLayoutDao().queryForId(name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Layout> getLayouts() {
		try {
			return getLayoutDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
