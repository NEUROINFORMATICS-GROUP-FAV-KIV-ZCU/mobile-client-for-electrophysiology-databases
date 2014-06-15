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

public class FormStore extends Store {
    private final static String TAG = FormStore.class.getName();

    private final static String VIEW_NAME = "form-view";
    private final static String DOC_TYPE_VALUE = "form";

    public FormStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }

    public Boolean saveOrUpdate(Form data) {
        return saveOrUpdate(data, getDocument(data.getType()));
    }

    public Form saveOrUpdate(String type, Date date) {
        Form form = new Form(type, date);
        return saveOrUpdate(form, getDocument(type)) ? form : null;
    }

    @Override
    protected Query getQuery() {
        return getQuery("type");
    }

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

    public Form getForm(String type) {
        Document document = getDocument(type);
        return document != null ? new Form(document.getProperties()) : null;
    }

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

    public Form create(String type) {
        Form form = new Form(type);
        return saveOrUpdate(form, null) ? form : null;
    }
}
