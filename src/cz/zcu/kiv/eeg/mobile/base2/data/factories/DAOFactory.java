package cz.zcu.kiv.eeg.mobile.base2.data.factories;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import cz.zcu.kiv.eeg.mobile.base2.data.dao.DatabaseHelper;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.FieldDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.FormDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.FormLayoutsDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.LayoutDAO;
import android.content.Context;

public class DAOFactory {
	private final Context context; // todo asi odstranit, pokud to nebudu potrebovat v nejake podtride ktere predam tuto tovarnu

	private FormDAO formDao;
	private FieldDAO fieldDao;
	private LayoutDAO layoutDao;
	private FormLayoutsDAO formLayoutsDao;
	private DatabaseHelper databaseHelper = null;

	public DAOFactory(final Context context) {
		this.context = context;
		databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
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
