package cz.zcu.kiv.eeg.mobile.base2.data.store;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormLayouts;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;

/**
 * Storage class for FormLayoutsStore.java
 *
 * @author Rahul Kadyan, mail@rahulkadyan.com
 * @version 1.0.0.
 */
public class FormLayoutsStore extends Store {
    private final static String TAG = FormLayoutsStore.class.getName();

    private final static String VIEW_NAME = "form-layout-view";
    private final static String DOC_TYPE_LAYOUT = "form-layout";

    /**
     * Instantiates a new Form layouts store.
     *
     * @param databaseHelper the database helper
     */
    public FormLayoutsStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_LAYOUT);
    }

    /**
     * Save or update.
     *
     * @param form the form
     * @param layout the layout
     * @return the boolean
     */
    public Boolean saveOrUpdate(Form form, Layout layout) {
        FormLayouts data = new FormLayouts(form, layout);
        return saveOrUpdate(data);
    }

    /**
     * Gets layout.
     *
     * @param form the form
     * @return the layout
     */
    public List<Layout> getLayout(Form form) {
        Query query = getQuery("rootForm-id");
        List<Object> keys = new ArrayList<Object>();
        keys.add(form.getId());
        query.setKeys(keys);
        List<Layout> list = new ArrayList<Layout>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                Document document = it.next().getDocument();
                FormLayouts layout = new FormLayouts(document.getProperties());
                list.add(layout.getLayout());
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }
}
