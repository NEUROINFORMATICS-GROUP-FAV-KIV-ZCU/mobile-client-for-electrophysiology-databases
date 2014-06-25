package cz.zcu.kiv.eeg.mobile.base2.data.store;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;

/**
 * Storage class for Data.java
 *
 * @author Rahul Kadyan, mail@rahulkadyan.com
 * @version 1.0.0
 */
public class DataStore extends Store {
    private final static String TAG = DataStore.class.getName();

    private final static String VIEW_NAME = "data-view";
    private final static String DOC_TYPE_VALUE = "data";

    /**
     * Instantiates a new Data store.
     *
     * @param databaseHelper instance of DatabaseHelper
     */
    public DataStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }

    /**
     * Updates Data object in database
     *
     * @param dataset the dataset
     * @param field   the field
     * @param data    the data
     */
    public void update(final Dataset dataset, final Field field, final String data) {
        saveOrUpdate(new Data(dataset, field, data));
    }

    /**
     * Creates a Data object in given Dataset and store it
     *
     * @param dataset Dataset in which the object belongs
     * @param field   Type of field
     * @param data    Value for the given field
     */
    public void create(final Dataset dataset, final Field field, final String data) {
        saveOrUpdate(new Data(dataset, field, data), null);
    }

    /**
     * Gets data
     *
     * @param dataSetId Dataset id of the requested Data instance
     * @param fieldId   Field id of the requested Data instance
     * @return Requested instance of Data
     */
    public Data getData(final int dataSetId, final int fieldId) {
        List<String> columnList = new ArrayList<String>();
        columnList.add("dataset-id");
        columnList.add("field-id");
        Query query = getQuery(columnList, "dataset-and-field");
        List<Object> keys = new ArrayList<Object>();
        List<Object> compoundKeys = new ArrayList<Object>();
        keys.add(dataSetId);
        keys.add(fieldId);
        compoundKeys.add(keys);
        query.setKeys(compoundKeys);
        try {
            QueryEnumerator it = query.run();
            if (it.hasNext()) {
                Document document = it.next().getDocument();
                return new Data(document.getProperties());
            }
        } catch (CouchbaseLiteException e) {
            Log.i(TAG, "query failed", e);
        }
        return null;
    }

    /**
     * Gets Data object in given Dataset with given Field
     *
     * @param dataset the dataset
     * @param field   the field
     * @return requested instance of Data
     */
    public Data getData(final Dataset dataset, final Field field) {
        return getData(dataset.getId(), field.getId());
    }

    /**
     * Gets Data object in given Dataset with given Field and data value
     *
     * @param dataset the dataset
     * @param field   the field
     * @param data    the data
     * @return requested instance of Data
     */
    public Data getData(Dataset dataset, Field field, String data) {
        Document document = getDocument(dataset, field, data);
        return document != null ? new Data(document.getProperties()) : null;
    }

    /**
     * Gets Document from database of type Data belonging to given Dataset with given Field and data value
     *
     * @param dataset the dataset
     * @param field   the field
     * @param data    the data
     * @return requested document
     */
    public Document getDocument(Dataset dataset, Field field, String data) {
        List<String> columnList = new ArrayList<String>();
        columnList.add("dataset-id");
        columnList.add("field-id");
        columnList.add("data");
        Query query = getQuery(columnList, "dataser-and-field-and-data");
        List<Object> keys = new ArrayList<Object>();
        List<Object> compoundKeys = new ArrayList<Object>();
        keys.add(dataset.getId());
        keys.add(field.getId());
        keys.add(data);
        compoundKeys.add(keys);
        keys.add(compoundKeys);
        try {
            QueryEnumerator it = query.run();
            if (it.hasNext())
                return it.next().getDocument();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets all instances of Data belonging to given Dataset (dataset id) and with given Field (field id)
     *
     * @param dataSetId the dataset id
     * @param fieldId   the field id
     * @return list of all Data instances
     */
    public List<Data> getAllData(final int dataSetId, final int fieldId) {
        List<String> columnList = new ArrayList<String>();
        columnList.add("dataset-id");
        columnList.add("field-id");
        Query query = getQuery(columnList, "dataset-and-field");
        List<Object> keys = new ArrayList<Object>();
        List<Object> compoundKeys = new ArrayList<Object>();
        keys.add(dataSetId);
        keys.add(fieldId);
        compoundKeys.add(keys);
        query.setKeys(compoundKeys);
        List<Data> list = new ArrayList<Data>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                Document document = it.next().getDocument();
                list.add(new Data(document.getProperties()));
            }
        } catch (CouchbaseLiteException e) {
            Log.i(TAG, "query failed", e);
        }
        return list;
    }

    /**
     *  Gets all instances of Data belonging to given Dataset and with given Field
     *
     * @param dataset the dataset
     * @param field   the field
     * @return list of all Data instances
     */
    public List<Data> getAllData(final Dataset dataset, final Field field) {
        return getAllData(dataset.getId(), field.getId());
    }

    /**
     * Gets list of data field of Data objects stored in the database
     *
     * @return list of requested data values
     */
    public String[] getData() {
        Query query = getQuery();
        query.setDescending(true);
        List<String> names = new ArrayList<String>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                Document document = it.next().getDocument();
                Data data = new Data(document.getProperties());
                names.add(data.getData());
            }
        } catch (CouchbaseLiteException e) {
            Log.i(TAG, "query failed", e);
        }
        return (String[]) names.toArray();
    }

    /**
     * Gets data by dataset.
     *
     * @param dataSetId the data set id
     * @return the data by dataset
     */
    public List<Data> getDataByDataset(final int dataSetId) {
        Query query = getQuery("dataset-id");
        List<Object> keys = new ArrayList<Object>();
        keys.add(dataSetId);
        query.setKeys(keys);
        List<Data> list = new ArrayList<Data>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                list.add(new Data(it.next().getDocument().getProperties()));
            }
        } catch (CouchbaseLiteException e) {
            Log.i(TAG, "query failed", e);
        }
        return list;
    }


    /**
     * Delete void.
     *
     * @param dataset     the dataset
     * @param field       the field
     * @param idForRemove the id for remove
     */
    public void delete(Dataset dataset, Field field, String idForRemove) {
        Document document = getDocument(dataset, field, idForRemove);
        if (null != document) try {
            document.delete();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}
