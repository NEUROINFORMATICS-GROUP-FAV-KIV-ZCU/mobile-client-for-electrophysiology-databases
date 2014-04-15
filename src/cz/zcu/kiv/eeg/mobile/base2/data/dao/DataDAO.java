package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class DataDAO {

	private DatabaseHelper databaseHelper;

	public DataDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	public Dao<Data, Integer> getDataDao() throws SQLException {
		return databaseHelper.getDataDao();
	}

	public CreateOrUpdateStatus saveOrUpdate(Data data) {
		try {
			return getDataDao().createOrUpdate(data);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Data saveOrUpdate(final Dataset dataset, final Field field, final String data) {
		try {
			Data tData = new Data(dataset, field, data);
			getDataDao().createOrUpdate(tData);
			return tData;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void update(final Dataset dataset, final Field fiel, final String data) {
		try {
			UpdateBuilder<Data, Integer> updateBuilder = getDataDao().updateBuilder();
			Where<Data, Integer> where = updateBuilder.where();
			where.eq("dataset_id", dataset);
			where.and();
			where.eq("field_id", fiel);

			updateBuilder.updateColumnValue("data" /* column */, data /* value */);
			updateBuilder.update();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void create(final Dataset dataset, Field field, String data) {
		try {
			Data tData = new Data(dataset, field, data);
			getDataDao().create(tData);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// todo odstranit
	public String[] getData() {
		try {

			List<Data> items = getDataDao().queryForAll();
			String[] names = new String[items.size()];
			int i = 0;
			for (Data item : items) {
				names[i] = item.getData();
				i++;
			}
			return names;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Data> getDataByDataset(final int datasetId) {
		try {
			QueryBuilder<Data, Integer> queryBuilder = getDataDao().queryBuilder();
			Where<Data, Integer> where = queryBuilder.where();
			where.eq("dataset_id", datasetId);

			PreparedQuery<Data> preparedQuery = queryBuilder.prepare();
			return getDataDao().query(preparedQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Data getDataByDatasetAndField(final int datasetId, final int fieldId) {
		try {
			QueryBuilder<Data, Integer> queryBuilder = getDataDao().queryBuilder();
			Where<Data, Integer> where = queryBuilder.where();
			where.eq("dataset_id", datasetId);
			where.and();
			where.eq("field_id", fieldId);
			PreparedQuery<Data> preparedQuery = queryBuilder.prepare();
			return getDataDao().queryForFirst(preparedQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
