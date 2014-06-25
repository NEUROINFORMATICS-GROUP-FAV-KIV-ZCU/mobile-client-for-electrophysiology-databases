package cz.zcu.kiv.eeg.mobile.base2.data.store;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;


/**
 * Storage class for FormStore.java
 *
 * @author Rahul Kadyan, mail@rahulkadyan.com
 * @version 1.0.0.
 */
public class FormStore extends Store {
    private final static String TAG = FormStore.class.getName();

    private final static String VIEW_NAME = "form-view";
    private final static String DOC_TYPE_VALUE = "form";

    /**
     * Instantiates a new Form store.
     *
     * @param databaseHelper the database helper
     */
    public FormStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }

    /**
     * Save or update.
     *
     * @param data the data
     * @return the boolean
     */
    public Boolean saveOrUpdate(Form data) {
        return saveOrUpdate(data, getDocument(data.getType()));
    }

    /**
     * Save or update.
     *
     * @param type the type
     * @param date the date
     * @return the form
     */
    public Form saveOrUpdate(String type, Date date) {
        Form form = new Form(type, date);
        return saveOrUpdate(form, getDocument(type)) ? form : null;
    }

    @Override
    protected Query getQuery() {
        return getQuery("type");
    }

    /**
     * Gets document.
     *
     * @param type the type
     * @return the document
     */
    protected Document getDocument(String type) {
        Query query = getQuery();
        List<Object> keys = new ArrayList<Object>();
        keys.add(type);
        query.setKeys(keys);
        try {
            QueryEnumerator it = query.run();
            if (it.hasNext())
                return it.next().getDocument();
        } catch (CouchbaseLiteException e) {
            Log.i(TAG, "Error running query", e);
        }
        return null;
    }

    /**
     * Gets form.
     *
     * @param type the type
     * @return the form
     */
    public Form getForm(String type) {
        Document document = getDocument(type);
        return document != null ? new Form(document.getProperties()) : null;
    }

    /**
     * Gets forms.
     *
     * @return the forms
     */
    public List<Form> getForms() {
        Query query = getQuery();
        List<Form> list = new ArrayList<Form>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                Document document = it.next().getDocument();
                list.add(new Form(document.getProperties()));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Create form.
     *
     * @param type the type
     * @return the form
     */
    public Form create(String type) {
        Form form = new Form(type);
        return saveOrUpdate(form, null) ? form : null;
    }
}
