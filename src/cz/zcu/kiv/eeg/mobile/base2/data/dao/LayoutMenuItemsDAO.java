package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutMenuItems;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class LayoutMenuItemsDAO {

	private DatabaseHelper databaseHelper;

	public LayoutMenuItemsDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	private Dao<LayoutMenuItems, Integer> getLayoutMenuItemsDao() throws SQLException {
		return databaseHelper.getLayoutMenuItemsDao();
	}
	
	private Dao<Layout, String> getLayoutDao() throws SQLException {
		return databaseHelper.getLayoutDao();
	}

	public CreateOrUpdateStatus saveOrUpdate(final LayoutMenuItems LayoutMenuItems) {
		try {
			return getLayoutMenuItemsDao().createOrUpdate(LayoutMenuItems);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public CreateOrUpdateStatus saveOrUpdate(final Layout layout, final MenuItems root, final MenuItems menu) {
		try {
			LayoutMenuItems LayoutMenuItems = new LayoutMenuItems(layout,root, menu);
			return getLayoutMenuItemsDao().createOrUpdate(LayoutMenuItems);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public  LayoutMenuItems getLayoutMenuItems(MenuItems rootMenu, String layout) {
		try {
			QueryBuilder<LayoutMenuItems, Integer> LayoutMenuItemsQb = getLayoutMenuItemsDao().queryBuilder();
			LayoutMenuItemsQb.where()
			.eq(LayoutMenuItems.FK_ID_ROOT_MENUITEMS, rootMenu)
			.and()
			.eq(LayoutMenuItems.FK_ID_LAYOUT, layout);	
			return LayoutMenuItemsQb.queryForFirst();
									
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Layout> getLayout(final MenuItems menu) {
		try {
			QueryBuilder<LayoutMenuItems, Integer> LayoutMenuItemsQb = getLayoutMenuItemsDao().queryBuilder();
			LayoutMenuItemsQb.where().eq(LayoutMenuItems.FK_ID_MENUITEMS, menu);
			QueryBuilder<Layout, String> layoutQb = getLayoutDao().queryBuilder();
			// join with the order query
			List<Layout> results = layoutQb.join(LayoutMenuItemsQb).query();
						
			return results;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Layout> getLayoutRootMenu(final MenuItems root) {
		try {
			QueryBuilder<LayoutMenuItems, Integer> LayoutMenuItemsQb = getLayoutMenuItemsDao().queryBuilder();
			LayoutMenuItemsQb.where().eq(LayoutMenuItems.FK_ID_ROOT_MENUITEMS, root);
			QueryBuilder<Layout, String> layoutQb = getLayoutDao().queryBuilder();
			// join with the order query
			List<Layout> results = layoutQb.join(LayoutMenuItemsQb).query();
						
			return results;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<LayoutMenuItems> getLayoutMenuItems() {
		try {
			return getLayoutMenuItemsDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void delete(int id) {
		try {
			DeleteBuilder<LayoutMenuItems, Integer> deleteBuilder = getLayoutMenuItemsDao().deleteBuilder();
			Where<LayoutMenuItems, Integer> where = deleteBuilder.where();
			where.eq("id", id);
			deleteBuilder.delete();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
