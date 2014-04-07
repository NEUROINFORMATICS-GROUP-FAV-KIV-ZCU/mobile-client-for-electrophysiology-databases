package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItem;

public class MenuItemDAO {

	private DatabaseHelper databaseHelper;

	public MenuItemDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	private Dao<MenuItem, Integer> getMenuItemDao() throws SQLException {
		return databaseHelper.getMenuItemDao();
	}

	public CreateOrUpdateStatus saveOrUpdate(final MenuItem item) {
		try {
			return getMenuItemDao().createOrUpdate(item);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] getMenu() {
		try {

			List<MenuItem> items = getMenuItemDao().queryForAll();
			String[] names = new String[items.size()];
			int i = 0;
			for (MenuItem item : items) {
				names[i] = item.getName();
				i++;
			}
			return names;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
