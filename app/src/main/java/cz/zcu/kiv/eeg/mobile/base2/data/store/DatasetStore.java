package cz.zcu.kiv.eeg.mobile.base2.data.store;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;

/**
 * Storage class for Dataset.java
 *
 * @author Rahul Kadyan, mail@rahulkadyan.com
 * @version 1.0.0
 */
public class DatasetStore extends Store {
    /**
     * Debug TAG
     */
    private final static String TAG = DatasetStore.class.getName();

    private final static String VIEW_NAME = "dataset-view";
    private final static String DOC_TYPE_VALUE = "dataset";

    /**
     * Instantiates a new Dataset store.
     *
     * @param databaseHelper instance of DatabaseHelper
     */
    public DatasetStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }

    /**
     * Creates Dataset object and stores it in database
     *
     * @param form the form
     * @return the dataset
     */
    public Dataset create(final Form form) {
        Dataset dataset = new Dataset(form);
        saveOrUpdate(dataset, null);
        return dataset;
    }


    /**
     * Gets Dataset by id
     *
     * @param id dataset id
     * @return Dataset
     */
    public Dataset getDataSet(int id) {
        Document document = getDocument(id);
        if (null != document) return new Dataset(document.getProperties());
        return null;
    }

    /**
     * Gets Dataset by record id
     *
     * @param recordId record id
     * @return Dataset|null
     */
    public Dataset getDataSet(String recordId) {
        Query query = getQuery("recordId");
        List<Object> keys = new ArrayList<Object>();
        keys.add(recordId);
        query.setKeys(keys);
        try {
            QueryEnumerator it = query.run();
            if (it.hasNext()) return new Dataset(it.next().getDocument().getProperties());
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Gets all Dataset objects in database
     *
     * @return  List of all Dataset objects
     */
    public List<Dataset> getDataSet() {
        Query query = getQuery();
        List<Dataset> list = new ArrayList<Dataset>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                list.add(new Dataset(it.next().getDocument().getProperties()));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Gets Dataset objects belonging to given form
     *
     * @param form
     * @return List of Dataset objects belonging to given form
     */
    public List<Dataset> getDataSet(Form form) {
        Query query = getQuery("form-type");
        List<Object> keys = new ArrayList<Object>();
        keys.add(form.getType());
        query.setKeys(keys);
        List<Dataset> list = new ArrayList<Dataset>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                Document document = it.next().getDocument();
                list.add(new Dataset(document.getProperties()));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }
}
