package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FieldValue;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormLayouts;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 * 
 * @author Jaroslav Hošek
 * 
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	public static final String TAG = "DatabaseHelper";
	private static final String DATABASE_NAME = "eegMobileDatabase.db";
	private static final int DATABASE_VERSION = 78;

	private static Dao<Form, String> formDao = null;
	private static Dao<Field, Integer> fieldDao = null;
	private static Dao<FieldValue, Integer> fieldValueDao = null;
	private static Dao<LayoutProperty, Integer> layoutPropertyDao = null;
	private static Dao<Layout, String> layoutDao = null;
	private static Dao<Dataset, Integer> datasetDao = null;
	private static Dao<Data, Integer> dataDao = null;
	private static Dao<MenuItems, Integer> menuItemDao = null;
	private static Dao<FormLayouts, Integer> formLayoutsDao = null;
	private static Dao<User, Integer> userDao = null;

	public DatabaseHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Tato metoda je volana pri prvnim vytvoreni databaze. Vytvari prislusne tabulky.
	 */
	@Override
	public void onCreate(final SQLiteDatabase db, final ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Form.class);
			TableUtils.createTable(connectionSource, Field.class);
			TableUtils.createTable(connectionSource, Layout.class);
			TableUtils.createTable(connectionSource, MenuItems.class);
			TableUtils.createTable(connectionSource, FormLayouts.class);
			TableUtils.createTable(connectionSource, Dataset.class);
			TableUtils.createTable(connectionSource, Data.class);
			TableUtils.createTable(connectionSource, User.class);
			TableUtils.createTable(connectionSource, LayoutProperty.class);
			TableUtils.createTable(connectionSource, FieldValue.class);

		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Tato metoda je volana kdyz je novejsi verze databaze. Podle verze pridava tabulku ci nove radky.
	 */
	@Override
	public void onUpgrade(final SQLiteDatabase db, final ConnectionSource connectionSource, final int oldVersion,
			final int newVersion) {

		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade, stara verze " + oldVersion + ", nova verze " + newVersion);

			TableUtils.dropTable(connectionSource, Form.class, false);
			TableUtils.dropTable(connectionSource, Field.class, false);
			TableUtils.dropTable(connectionSource, FormLayouts.class, false);
			TableUtils.dropTable(connectionSource, Layout.class, false);
			TableUtils.dropTable(connectionSource, MenuItems.class, false);
			TableUtils.dropTable(connectionSource, Dataset.class, false);
			TableUtils.dropTable(connectionSource, Data.class, false);
			TableUtils.dropTable(connectionSource, User.class, false);
			TableUtils.dropTable(connectionSource, LayoutProperty.class, false);
			TableUtils.dropTable(connectionSource, FieldValue.class, false);
			
			onCreate(db, connectionSource);

		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Chyba pri upgradu databaze.", e);
			throw new RuntimeException(e);
		}

	}

	public Dao<Form, String> getFormDao() throws SQLException {
		if (formDao == null) {
			formDao = getDao(Form.class);
		}
		return formDao;
	}

	public Dao<Field, Integer> getFieldDao() throws SQLException {
		if (fieldDao == null) {
			fieldDao = getDao(Field.class);
		}
		return fieldDao;
	}

	public Dao<Layout, String> getLayoutDao() throws SQLException {
		if (layoutDao == null) {
			layoutDao = getDao(Layout.class);
		}
		return layoutDao;
	}

	public Dao<MenuItems, Integer> getMenuItemDao() throws SQLException {
		if (menuItemDao == null) {
			menuItemDao = getDao(MenuItems.class);
		}
		return menuItemDao;
	}

	public Dao<FormLayouts, Integer> getFormLayoutsDao() throws SQLException {
		if (formLayoutsDao == null) {
			formLayoutsDao = getDao(FormLayouts.class);
		}
		return formLayoutsDao;
	}

	public Dao<Dataset, Integer> getDataSetDao() throws SQLException {
		if (datasetDao == null) {
			datasetDao = getDao(Dataset.class);
		}
		return datasetDao;
	}

	public Dao<Data, Integer> getDataDao() throws SQLException {
		if (dataDao == null) {
			dataDao = getDao(Data.class);
		}
		return dataDao;
	}

	public Dao<User, Integer> getUserDao() throws SQLException {
		if (userDao == null) {
			userDao = getDao(User.class);
		}
		return userDao;
	}
	
	public Dao<LayoutProperty, Integer> getLayoutPropertyDao() throws SQLException {
		if (layoutPropertyDao == null) {
			layoutPropertyDao = getDao(LayoutProperty.class);
		}
		return layoutPropertyDao;
	}
	
	public Dao<FieldValue, Integer> getFieldValueDao() throws SQLException {
		if (fieldValueDao == null) {
			fieldValueDao = getDao(FieldValue.class);
		}
		return fieldValueDao;
	}

	/**
	 * Uzavreni databaze a odstraneni nacachovanych DAO.
	 */
	@Override
	public void close() {
		super.close();
		formDao = null;
		fieldDao = null;
		layoutDao = null;
		menuItemDao = null;
		formLayoutsDao = null;
		datasetDao = null;
		dataDao = null;
		userDao = null;
		layoutPropertyDao = null;
		fieldValueDao = null;
	}
}