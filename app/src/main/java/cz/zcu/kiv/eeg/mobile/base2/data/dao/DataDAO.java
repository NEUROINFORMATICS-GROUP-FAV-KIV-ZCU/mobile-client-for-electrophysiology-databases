package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.util.List;

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
		this.databaseHelper = databaseHelper;
	}

	public String saveOrUpdate(Data data) {

		return null;
	}

	public Data saveOrUpdate(final Dataset dataset, final Field field, final String data) {
        return null;
	}

	public void update(final Dataset dataset, final Field field, final String data) {
	}

	public String create(final Dataset dataset, final Field field, final String data) {
		Data tData = new Data(dataset, field, data);
        return databaseHelper.create(tData.get());
	}

	public Data getData(final Dataset dataset, final Field field) {
		return null;
	}
	
	public Data getData(final int datasetId, final int fieldId) {
		return null;
	}

	public List<Data> getAllData(final int datasetId, final int fieldId) {
		return null;
	}
	
	// todo odstranit
	public String[] getData() {
		return null;
	}

	public List<Data> getDataByDataset(final int datasetId) {
		return null;
	}
}
