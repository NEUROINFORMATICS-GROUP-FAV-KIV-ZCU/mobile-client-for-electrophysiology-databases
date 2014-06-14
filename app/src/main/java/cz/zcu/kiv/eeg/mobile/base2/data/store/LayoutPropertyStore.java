package cz.zcu.kiv.eeg.mobile.base2.data.store;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;

public class LayoutPropertyStore extends Store {
    private final static String TAG = LayoutPropertyStore.class.getName();

    private final static String VIEW_NAME = "layouts";
    private final static String DOC_TYPE_VALUE = "layout";

    public LayoutPropertyStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }

    public LayoutProperty getProperty(int id) {
        Document document = getDocument(id);
        if (null != document) return new LayoutProperty(document.getProperties());
        return null;
    }

    public LayoutProperty getProperty(int fieldId, String layoutId) {
        if (fieldId < 1 || layoutId == null) return null;
        Query query = getQuery();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                LayoutProperty layoutProperty = new LayoutProperty(it.next().getDocumentProperties());
                if (layoutProperty.getField().getId() == fieldId && layoutId.equals(layoutProperty.getLayout().getName()))
                    return layoutProperty;
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<LayoutProperty> getProperties() {
        Query query = getQuery();
        List<LayoutProperty> list = new ArrayList<LayoutProperty>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                list.add(new LayoutProperty(it.next().getDocumentProperties()));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }

    public LayoutProperty create(LayoutProperty layoutProperty) {
        return saveOrUpdate(layoutProperty, databaseHelper.getDocument()) ? layoutProperty : null;
    }
}
