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

    private final static String VIEW_NAME = "layout-property-view";
    private final static String DOC_TYPE_VALUE = "layout-property";

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
                Document document = it.next().getDocument();
                LayoutProperty layoutProperty = new LayoutProperty(document.getProperties());
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
        return saveOrUpdate(layoutProperty, null) ? layoutProperty : null;
    }
}
