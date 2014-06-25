package cz.zcu.kiv.eeg.mobile.base2.data.store;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;

/**
 * Storage class for LayoutStore.java
 *
 * @author Rahul Kadyan, mail@rahulkadyan.com
 * @version 1.0.0.
 */
public class LayoutStore extends Store {
    private final static String TAG = LayoutStore.class.getName();

    private final static String VIEW_NAME = "layout-view";
    private final static String DOC_TYPE_VALUE = "layout";

    /**
     * Instantiates a new Layout store.
     *
     * @param databaseHelper the database helper
     */
    public LayoutStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }

    /**
     * Save or update.
     *
     * @param layoutName the layout name
     * @param xmlData the xml data
     * @param rootForm the root form
     * @return the layout
     */
    public Layout saveOrUpdate(String layoutName, String xmlData, Form rootForm) {
        Layout data = new Layout(layoutName, xmlData, rootForm);
        return saveOrUpdate(data, getDocument(layoutName)) ? data : null;
    }

    /**
     * Save or update.
     *
     * @param data the data
     * @return the boolean
     */
    public Boolean saveOrUpdate(Layout data) {
        return saveOrUpdate(data, getDocument(data.getName()));
    }

    /**
     * Gets document.
     *
     * @param name the name
     * @return the document
     */
    protected Document getDocument(String name) {
        Query query = getQuery("name");
        List<Object> keys = new ArrayList<Object>();
        keys.add(name);
        query.setKeys(keys);
        try {
            QueryEnumerator it = query.run();
            if (it.hasNext()) {
                return it.next().getDocument();
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Query getQuery() {
        return getQuery("name");
    }

    /**
     * Gets layouts.
     *
     * @return the layouts
     */
    public List<Layout> getLayouts() {
        Query query = getQuery();
        List<Layout> list = new ArrayList<Layout>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                QueryRow row = it.next();
                list.add(new Layout(row.getDocumentProperties()));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Gets layout.
     *
     * @param name the name
     * @return the layout
     */
    public Layout getLayout(String name) {
        Query query = getQuery();
        List<Object> keys = new ArrayList<Object>();
        keys.add(name);
        query.setKeys(keys);
        try {
            QueryEnumerator it = query.run();
            if (it.hasNext()) {
                Document document = it.next().getDocument();
                return new Layout(document.getProperties());
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create layout.
     *
     * @param name the name
     * @param xmlData the xml data
     * @param form the form
     * @return the layout
     */
    public Layout create(String name, String xmlData, Form form) {
        Layout layout = new Layout(name, xmlData, form);
        return saveOrUpdate(layout, null) ? layout : null;
    }
}
