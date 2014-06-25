package cz.zcu.kiv.eeg.mobile.base2.data.store;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.eeg.mobile.base2.data.model.FieldValue;

/**
 * Storage class for FieldValue.java
 *
 * @author Rahul Kadyan, mail@rahulkadyan.com
 * @version 1.0.0.
 */
public class FieldValueStore extends Store {
    private final static String TAG = FieldValueStore.class.getName();

    private final static String VIEW_NAME = "field-value-view";
    private final static String DOC_TYPE_VALUE = "field-value";

    /**
     * Instantiates a new Field value store.
     *
     * @param databaseHelper the database helper
     */
    public FieldValueStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }


    /**
     * Create field value.
     *
     * @param fieldValue the field value
     * @return the field value
     */
    public FieldValue create(FieldValue fieldValue) {
        if (saveOrUpdate(fieldValue, null)) return fieldValue;
        return null;
    }

    /**
     * Gets field value.
     *
     * @param fieldId the field id
     * @return the field value
     */
    public List<FieldValue> getFieldValue(int fieldId) {
        Query query = getQuery();
        List<Object> keys = new ArrayList<Object>();
        keys.add(fieldId);
        query.setKeys(keys);
        List<FieldValue> list = new ArrayList<FieldValue>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                Document document = it.next().getDocument();
                FieldValue fieldValue = new FieldValue(document.getProperties());
                list.add(fieldValue);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Gets field value.
     *
     * @return the field value
     */
    public List<FieldValue> getFieldValue() {
        Query query = getQuery();
        List<FieldValue> list = new ArrayList<FieldValue>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                list.add(new FieldValue(it.next().getDocumentProperties()));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }
}
