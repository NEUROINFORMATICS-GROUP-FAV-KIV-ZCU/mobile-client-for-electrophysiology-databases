package cz.zcu.kiv.eeg.mobile.base2.data.store;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

import static cz.zcu.kiv.eeg.mobile.base2.data.store.DatabaseHelper.DOC_TYPE;

/**
 * Generic storage class
 *
 * @author Rahul Kadyan, mail@rahulkadyan.com
 * @version 1.0.0.
 */
public class Store {
    private final static String TAG = Store.class.getName();

    /**
     * The Database helper.
     */
    protected final DatabaseHelper databaseHelper;
    private final String viewName;
    private final String docType;

    /**
     * Instantiates a new Store.
     *
     * @param databaseHelper the database helper
     * @param viewName       the view name
     * @param docType        the doc type
     */
    public Store(DatabaseHelper databaseHelper, String viewName, String docType) {
        this.databaseHelper = databaseHelper;
        this.viewName = viewName;
        this.docType = docType;
    }

    /**
     * Save or update.
     *
     * @param data the data
     * @return the boolean
     */
    public Boolean saveOrUpdate(NoSQLData data) {
        Document document = getDocument(data.getId());
        return saveOrUpdate(data, document);
    }

    /**
     * Save or update.
     *
     * @param data     the data
     * @param document the document
     * @return the boolean
     */
    protected Boolean saveOrUpdate(NoSQLData data, Document document) {
        Map<String, Object> properties = new HashMap<String, Object>();
        if (data.getId() == 0) data.setId(getCount() + 1);
        if (null == document) {
            document = databaseHelper.getDocument();
            properties.put(DOC_TYPE, docType);
        } else {
            properties.putAll(document.getProperties());
        }
        properties.putAll(data.get());
        String id = databaseHelper.putProperties(document, properties);
        return !id.equals("");
    }

    /**
     * Gets query.
     *
     * @return the query
     */
    protected Query getQuery() {
        return getQuery("id");
    }

    /**
     * Gets query.
     *
     * @param fields   the fields
     * @param viewName the view name
     * @return the query
     */
    public Query getQuery(final List<String> fields, String viewName) {
        View view = databaseHelper.getDatabase().getView(viewName + "-by-" + viewName);
        if (view.getMap() == null) {
            Mapper mapper = new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    String type = (String) document.get(DOC_TYPE);
                    if (docType.equals(type)) {
                        List<Object> compoundKey = new ArrayList<Object>();
                        Log.i(TAG, "##########" + document);
                        for (String field : fields) compoundKey.add(document.get(field));
                        emitter.emit(compoundKey, document);
                    }
                }
            };
            view.setMap(mapper, null);
        }
        Query query = view.createQuery();
        query.setDescending(true);
        return query;
    }

    /**
     * Gets query.
     *
     * @param field the field
     * @return the query
     */
    public Query getQuery(final String field) {
        View view = databaseHelper.getDatabase().getView(viewName + "-by-" + field);
        if (view.getMap() == null) {
            Mapper mapper = new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    String type = (String) document.get(DOC_TYPE);
                    if (docType.equals(type)) {
                        emitter.emit(document.get(field), document);
                    }
                }
            };
            view.setMap(mapper, null);
        }
        Query query = view.createQuery();
        query.setDescending(true);
        return query;
    }

    /**
     * Gets document.
     *
     * @param id the id
     * @return the document
     */
    protected Document getDocument(int id) {
        if (0 == id) return null;
        Query query = getQuery();
        query.setDescending(true);
        List<Object> keys = new ArrayList<Object>();
        keys.add(id);
        query.setKeys(keys);
        try {
            QueryEnumerator it = query.run();
            if (it.hasNext())
                return it.next().getDocument();

        } catch (CouchbaseLiteException e) {
            Log.i(TAG, "query failed", e);
        }
        return null;
    }

    /**
     * Gets count.
     *
     * @return the count
     */
    public int getCount() {
        Query query = getQuery();
        try {
            QueryEnumerator it = query.run();
            return it.getCount();
        } catch (CouchbaseLiteException e) {
            Log.i(TAG, "query failed", e);
        }
        return -1;
    }
}
