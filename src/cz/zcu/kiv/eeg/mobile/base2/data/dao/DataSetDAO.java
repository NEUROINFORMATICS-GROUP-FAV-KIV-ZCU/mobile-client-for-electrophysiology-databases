package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

import cz.zcu.kiv.eeg.mobile.base2.data.model.DataSet;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;

public class DataSetDAO {

	private DatabaseHelper databaseHelper;

	public DataSetDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	public Dao<DataSet, Integer> getDataSetDao() throws SQLException {
		return databaseHelper.getDataSetDao();
	}

	public CreateOrUpdateStatus saveOrUpdate(DataSet dataset) {
		try {
			return getDataSetDao().createOrUpdate(dataset);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public DataSet saveOrUpdate(final Form form) {
		try {
			DataSet dataset = new DataSet(form);						
			getDataSetDao().createOrUpdate(dataset);
			return dataset;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public DataSet getDataSet(final int id) {
		try {
			return getDataSetDao().queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
