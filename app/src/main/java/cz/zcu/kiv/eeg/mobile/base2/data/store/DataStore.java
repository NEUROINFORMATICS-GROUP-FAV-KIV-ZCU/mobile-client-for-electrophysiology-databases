package cz.zcu.kiv.eeg.mobile.base2.data.store;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;

public class DataStore extends Store {
    private final static String TAG = DataStore.class.getName();

    private final static String VIEW_NAME = "datum";
    private final static String DOC_TYPE_VALUE = "data";

    public DataStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }

    public void update(final Dataset dataset, final Field field, final String data) {
        saveOrUpdate(new Data(dataset, field, data));
    }

    public void create(final Dataset dataset, final Field field, final String data) {
        saveOrUpdate(new Data(dataset, field, data), databaseHelper.getDocument());
    }

    public Data getData(final int dataSetId, final int fieldId) {
        Query query = getQuery();
        query.setDescending(true);
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                QueryRow row = it.next();
                Log.i("Dumping all data id", row.getDocumentId());
                Document document = databaseHelper.getDatabase().getExistingDocument(row.getDocumentId());
                Data data = new Data(document.getProperties());
                if (data.getDataset().getId() == dataSetId && data.getField().getId() == fieldId)
                    return data;
            }
        } catch (CouchbaseLiteException e) {
            Log.i(TAG, "query failed", e);
        }
        return null;
    }

    public Data getData(final Dataset dataset, final Field field) {
        return getData(dataset.getId(), field.getId());
    }

    public Data getData(Dataset dataset, Field field, String data1) {
        Query query = getQuery("data");
        List<Object> keys = new ArrayList<Object>();
        keys.add(data1);
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                Data data = new Data(it.next().getDocumentProperties());
                if(data.getDataset().getId() == dataset.getId() && field.getId() == data.getField().getId()) {
                    return data;
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Data> getAllData(final int dataSetId, final int fieldId) {
        Query query = getQuery();
        query.setDescending(true);
        List<Data> list = new ArrayList<Data>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                QueryRow row = it.next();
                Log.i("Dumping all data id", row.getDocumentId());
                Document document = databaseHelper.getDatabase().getExistingDocument(row.getDocumentId());
                Data data = new Data(document.getProperties());
                if (data.getDataset().getId() == dataSetId && data.getField().getId() == fieldId)
                    list.add(data);
            }
        } catch (CouchbaseLiteException e) {
            Log.i(TAG, "query failed", e);
        }
        return list;
    }

    public List<Data> getAllData(final Dataset dataset, final Field field) {
        return getAllData(dataset.getId(), field.getId());
    }

    public String[] getData() {
        Query query = getQuery();
        query.setDescending(true);
        List<String> names = new ArrayList<String>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                QueryRow row = it.next();
                Log.i("Dumping all data id", row.getDocumentId());
                Document document = databaseHelper.getDatabase().getExistingDocument(row.getDocumentId());
                Data data = new Data(document.getProperties());
                names.add(data.getData());
            }
        } catch (CouchbaseLiteException e) {
            Log.i(TAG, "query failed", e);
        }
        return (String[]) names.toArray();
    }

    public List<Data> getDataByDataset(final int dataSetId) {
        Query query = getQuery();
        query.setDescending(true);
        List<Data> list = new ArrayList<Data>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                QueryRow row = it.next();
                Log.i("Dumping all data id", row.getDocumentId());
                Data data = new Data(row.getDocumentProperties());
                if (data.getDataset().getId() == dataSetId)
                    list.add(data);
            }
        } catch (CouchbaseLiteException e) {
            Log.i(TAG, "query failed", e);
        }
        return list;
    }


    public void delete(Dataset dataset, Field field, String idForRemove) {
        Query query = getQuery("data");
        List<Object> keys = new ArrayList<Object>();
        keys.add(idForRemove);
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                Document document = it.next().getDocument();
                Data data = new Data(document.getProperties());
                if(data.getDataset().getId() == dataset.getId() && field.getId() == data.getField().getId()) {
                    document.delete();
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}
