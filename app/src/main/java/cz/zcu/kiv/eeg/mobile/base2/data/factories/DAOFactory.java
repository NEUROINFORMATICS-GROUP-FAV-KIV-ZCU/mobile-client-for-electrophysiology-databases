package cz.zcu.kiv.eeg.mobile.base2.data.factories;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import cz.zcu.kiv.eeg.mobile.base2.data.dao.DataDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.DatabaseHelper;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.DatasetDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.FieldDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.FieldValueDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.FormDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.FormLayoutsDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.LayoutDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.LayoutPropertyDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.MenuItemsDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.UserDAO;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class DAOFactory {
	private final Context context;
	private FormDAO formDao;
	private FieldDAO fieldDao;
	private LayoutDAO layoutDao;
	private FormLayoutsDAO formLayoutsDao;
	private MenuItemsDAO menuItemDao;
	private DatasetDAO datasetDao;
	private DataDAO dataDao;
	private UserDAO userDao;
	private LayoutPropertyDAO layoutPropertyDao;
	private FieldValueDAO fieldValueDao;
	private DatabaseHelper databaseHelper = null;

	public DAOFactory(final Context context) {
		this.context = context;
	}

	public FormDAO getFormDAO() {
		if (formDao == null) {
			formDao = new FormDAO(databaseHelper);
		}
		return formDao;
	}

	public FieldDAO getFieldDAO() {
		if (fieldDao == null) {
			fieldDao = new FieldDAO(databaseHelper);
		}
		return fieldDao;
	}

	public LayoutDAO getLayoutDAO() {
		if (layoutDao == null) {
			layoutDao = new LayoutDAO(databaseHelper);
		}
		return layoutDao;
	}

	public FormLayoutsDAO getFormLayoutsDAO() {
		if (formLayoutsDao == null) {
			formLayoutsDao = new FormLayoutsDAO(databaseHelper);
		}
		return formLayoutsDao;
	}

	public MenuItemsDAO getMenuItemDAO() {
		if (menuItemDao == null) {
			menuItemDao = new MenuItemsDAO(databaseHelper);
		}
		return menuItemDao;
	}

	public DataDAO getDataDAO() {
		if (dataDao == null) {
			dataDao = new DataDAO(databaseHelper);
		}
		return dataDao;
	}

	public DatasetDAO getDataSetDAO() {
		if (datasetDao == null) {
			datasetDao = new DatasetDAO(databaseHelper);
		}
		return datasetDao;
	}

	public UserDAO getUserDAO() {
		if (userDao == null) {
			userDao = new UserDAO(databaseHelper);
		}
		return userDao;
	}
	
	public LayoutPropertyDAO getLayoutPropertyDAO() {
		if (layoutPropertyDao == null) {
			layoutPropertyDao = new LayoutPropertyDAO(databaseHelper);
		}
		return layoutPropertyDao;
	}
	
	public FieldValueDAO getFieldValueDAO() {
		if (fieldValueDao == null) {
			fieldValueDao = new FieldValueDAO(databaseHelper);
		}
		return fieldValueDao;
	}

	public DatabaseHelper getHelper() {
		return databaseHelper;
	}

	public Context getContext() {
		return context;
	}

	// todo potřeba volat při každém onDestroy aktivity/fragmentu, který pracuje s DB
	public void releaseHelper() {
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}
}
