package cz.zcu.kiv.eeg.mobile.base2.data.store;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.eeg.mobile.base2.data.model.FieldValue;

public class FieldValueStore extends Store {
    private final static String TAG = FieldValueStore.class.getName();

    private final static String VIEW_NAME = "fields";
    private final static String DOC_TYPE_VALUE = "field";

    public FieldValueStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }


    public FieldValue create(FieldValue fieldValue) {
        if (saveOrUpdate(fieldValue, databaseHelper.getDocument())) return fieldValue;
        return null;
    }

    public List<FieldValue> getFieldValue(int fieldId) {
        Query query = getQuery();
        List<FieldValue> list = new ArrayList<FieldValue>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                FieldValue fieldValue = new FieldValue(it.next().getDocumentProperties());
                if (fieldValue.getField().getId() == fieldId) list.add(fieldValue);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }

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
