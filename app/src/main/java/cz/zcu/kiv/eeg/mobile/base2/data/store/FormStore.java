package cz.zcu.kiv.eeg.mobile.base2.data.store;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;

import static cz.zcu.kiv.eeg.mobile.base2.data.store.DatabaseHelper.DOC_TYPE;

public class FormStore extends Store {
    private final static String TAG = FormStore.class.getName();

    private final static String VIEW_NAME = "forms";
    private final static String DOC_TYPE_VALUE = "form";

    public FormStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE);
    }

    public Form saveOrUpdate(String type, Date date) {
        Form form = new Form(type, date);
        return saveOrUpdate(form, getDocument(form.getId())) ? form : null;
    }

    public Form getFormByType(String type) {
        Query query = getQuery("type");
        List<Object> keys = new ArrayList<Object>();
        keys.add(type);
        query.setKeys(keys);
        try {
            QueryEnumerator it = query.run();
            QueryRow row = it.next();
            if (null != row) return new Form(row.getDocumentProperties());
        } catch (CouchbaseLiteException e) {
            Log.i(TAG, "Error running query", e);
        }
        return null;
    }
}
