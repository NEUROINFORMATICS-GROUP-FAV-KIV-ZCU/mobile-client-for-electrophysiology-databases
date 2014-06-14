package cz.zcu.kiv.eeg.mobile.base2.data.store;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;

public class LayoutStore extends Store {
    private final static String TAG = LayoutStore.class.getName();

    private final static String VIEW_NAME = "layouts";
    private final static String DOC_TYPE_VALUE = "layout";

    public LayoutStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }

    public Layout saveOrUpdate(String layoutName, Layout rootLayout, String xmlData, Form rootForm) {
        Layout data = new Layout(layoutName, xmlData, rootLayout, rootForm);
        return saveOrUpdate(data) ? data : null;
    }

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

    public Layout getLayout(String name) {
        Query query = getQuery("name");
        List<Object> keys = new ArrayList<Object>();
        keys.add(name);
        query.setKeys(keys);
        try {
            QueryEnumerator it = query.run();
            if (it.hasNext()) {
                return new Layout(it.next().getDocumentProperties());
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
