package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;
import java.util.List;

import android.view.MenuItem;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;

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

	public Dataset create (final Form form, final MenuItems workspace) {
		try {
			Dataset dataset = new Dataset(form, workspace);
			getDataSetDao().create(dataset);
			return dataset;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Dataset create (Dataset dataset) {
		try {
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
	
	/*public Dataset getDataSetByRecordId(final int recordId) {
		try {
			QueryBuilder<Dataset, Integer> queryBuilder = getDataSetDao().queryBuilder();
			queryBuilder.where().eq("recordId", recordId);
			return queryBuilder.queryForFirst();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}*/

	public List<Dataset> getDataSet() {
		try {
			return getDataSetDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Dataset> getDataSet(final Form form, final MenuItems workspace) {
		try {
			QueryBuilder<Dataset, Integer> queryBuilder = getDataSetDao().queryBuilder();
			queryBuilder.where().eq("form_id", form).and().eq("root_menu_id", workspace);
			return queryBuilder.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Dataset> getDataSet(final Form form, int state) {
		try {
			QueryBuilder<Dataset, Integer> queryBuilder = getDataSetDao().queryBuilder();
			queryBuilder.where().eq("form_id", form).and().eq("state", state);
			return queryBuilder.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Dataset getDataSet(final Form form, int recordId,  final MenuItems workspace) {
		try {
			QueryBuilder<Dataset, Integer> queryBuilder = getDataSetDao().queryBuilder();
			queryBuilder.where().eq("form_id", form).and().eq("recordId", recordId).and().eq("root_menu_id", workspace);
			return queryBuilder.queryForFirst();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void delete(final int dataset) {
		try {		
			DeleteBuilder<Dataset, Integer> deleteBuilder = getDataSetDao().deleteBuilder();
			Where<Dataset, Integer> where = deleteBuilder.where();
			where.eq("id", dataset);		
			deleteBuilder.delete();								
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
