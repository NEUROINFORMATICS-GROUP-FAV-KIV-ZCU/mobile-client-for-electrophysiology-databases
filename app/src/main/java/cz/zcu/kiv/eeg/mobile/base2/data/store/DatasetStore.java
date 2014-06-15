package cz.zcu.kiv.eeg.mobile.base2.data.store;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;

public class DatasetStore extends Store {
    private final static String TAG = DatasetStore.class.getName();

    private final static String VIEW_NAME = "datasets";
    private final static String DOC_TYPE_VALUE = "dataset";

    public DatasetStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }

    public Dataset create(final Form form) {
        Dataset dataset = new Dataset(form);
        saveOrUpdate(dataset, null);
        return dataset;
    }


    public Dataset getDataSet(int id) {
        Document document = getDocument(id);
        if (null != document) return new Dataset(document.getProperties());
        return null;
    }

    public Dataset getDataSet(String recordId) {
        Query query = getQuery("recordId");
        List<Object> keys = new ArrayList<Object>();
        keys.add(recordId);
        query.setKeys(keys);
        try {
            QueryEnumerator it = query.run();
            if (it.hasNext()) return new Dataset(it.next().getDocumentProperties());
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Dataset> getDataSet() {
        Query query = getQuery();
        List<Dataset> list = new ArrayList<Dataset>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                list.add(new Dataset(it.next().getDocumentProperties()));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Dataset> getDataSet(Form form) {
        Query query = getQuery();
        List<Dataset> list = new ArrayList<Dataset>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                Dataset dataset = new Dataset(it.next().getDocumentProperties());
                if (form.getId() == dataset.getForm().getId())
                    list.add(dataset);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }
}
