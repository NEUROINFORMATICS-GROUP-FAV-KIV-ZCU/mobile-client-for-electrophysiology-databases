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

public class FormLayoutsStore extends Store {
    private final static String TAG = FormLayoutsStore.class.getName();

    private final static String VIEW_NAME = "form-layouts";
    private final static String DOC_TYPE_LAYOUT = "form-layout";

    public FormLayoutsStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_LAYOUT);
    }

    public Boolean saveOrUpdate(Form form, Layout layout) {
        FormLayouts data = new FormLayouts(form, layout);
        return saveOrUpdate(data);
    }

    public List<Layout> getLayout(Form form) {
        Query query = getQuery();
        List<Layout> list = new ArrayList<Layout>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                Document document = it.next().getDocument();
                FormLayouts layout = new FormLayouts(document.getProperties());
                if (layout.getForm() != null && form.getId() == layout.getForm().getId()) {
                    list.add(layout.getLayout());
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }
}
