package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.QueryBuilder;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class MenuItemsDAO {

	private DatabaseHelper databaseHelper;

	public MenuItemsDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	private Dao<MenuItems, Integer> getMenuItemDao() throws SQLException {
		return databaseHelper.getMenuItemDao();
	}
	
	public CreateOrUpdateStatus saveOrUpdate(final MenuItems item) {
		try {
			return getMenuItemDao().createOrUpdate(item);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] getMenuNames() {
		try {

			List<MenuItems> items = getMenuItemDao().queryForAll();
			String[] names = new String[items.size()];
			int i = 0;
			for (MenuItems item : items) {
				names[i] = item.getName();
				i++;
			}
			return names;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<MenuItems> getMenu(){
		try {
			return getMenuItemDao().queryForAll();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public MenuItems getMenu(int id){
		try {
			return getMenuItemDao().queryForId(id);		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public MenuItems getMenu(String name){
		try {
			QueryBuilder<MenuItems, Integer> queryBuilder = getMenuItemDao().queryBuilder();
			queryBuilder.where().eq("name", name);
			return queryBuilder.queryForFirst();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
