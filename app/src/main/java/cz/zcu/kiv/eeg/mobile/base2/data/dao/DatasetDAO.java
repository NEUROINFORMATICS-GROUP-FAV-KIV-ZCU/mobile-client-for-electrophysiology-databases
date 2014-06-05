package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.QueryBuilder;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class DatasetDAO {

	private DatabaseHelper databaseHelper;

	public DatasetDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	public Dao<Dataset, Integer> getDataSetDao() throws SQLException {
		return databaseHelper.getDataSetDao();
	}

	public CreateOrUpdateStatus saveOrUpdate(Dataset dataset) {
		try {
			return getDataSetDao().createOrUpdate(dataset);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Dataset create (final Form form) {
		try {
			Dataset dataset = new Dataset(form);
			getDataSetDao().create(dataset);
			return dataset;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Dataset getDataSet(final int id) {
		try {
			return getDataSetDao().queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Dataset getDataSet(final String recordId) {
		try {
			QueryBuilder<Dataset, Integer> queryBuilder = getDataSetDao().queryBuilder();
			queryBuilder.where().eq("recordId", recordId);
			return queryBuilder.queryForFirst();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Dataset> getDataSet() {
		try {
			return getDataSetDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Dataset> getDataSet(final Form form) {
		try {
			QueryBuilder<Dataset, Integer> queryBuilder = getDataSetDao().queryBuilder();
			queryBuilder.where().eq("form_id", form);
			return queryBuilder.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
