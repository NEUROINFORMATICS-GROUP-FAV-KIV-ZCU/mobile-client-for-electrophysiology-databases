package cz.zcu.kiv.eeg.mobile.base2.data.store;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;

public class FieldStore extends Store {
    private final static String TAG = FieldStore.class.getName();

    private final static String VIEW_NAME = "fields";
    private final static String DOC_TYPE_VALUE = "field";

    public FieldStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }


    public Field getField(int id) {
        Document document = getDocument(id);
        if (null != document) return new Field(document.getProperties());
        return null;
    }

    @Override
    protected Query getQuery() {
        return getQuery("name");
    }

    public Field getField(String name, String formId) {
        Query query = getQuery();
        List<Object> key = new ArrayList<Object>();
        key.add(name);
        query.setKeys(key);
        try {
            QueryEnumerator it = query.run();
            for (; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                Field field = new Field(document.getProperties());
                if (field.getForm() != null && formId.equals(field.getForm().getType())) {
                    return field;
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Field> getFields() {
        Query query = getQuery();
        List<Field> list = new ArrayList<Field>();
        try {
            QueryEnumerator it = query.run();
            for (; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                list.add(new Field(document.getProperties()));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Field> getFields(String rootForm) {
        Query query = getQuery();
        List<Field> list = new ArrayList<Field>();
        if (null == rootForm) return list;
        try {
            QueryEnumerator it = query.run();
            for (; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                Field field = new Field(document.getProperties());
                if (field.getForm() != null && rootForm.equals(field.getForm().getType())) {
                    list.add(field);
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Field> getFields(String name, String formId) {
        Query query = getQuery("name");
        List<Object> keys = new ArrayList<Object>();
        keys.add(name);
        query.setKeys(keys);
        List<Field> list = new ArrayList<Field>();
        if (null == formId || null == name) return list;
        try {
            QueryEnumerator it = query.run();
            for (; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                Field field = new Field(document.getProperties());
                if (field.getForm() != null && formId.equals(field.getForm().getType())) {
                    list.add(field);
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Field> getFields(Iterable<Integer> notIn, String formType) {
        Query query = getQuery();
        List<Field> list = new ArrayList<Field>();
        if (null == formType) return list;
        Set<Integer> notInSet = new HashSet<Integer>();
        for (int i : notIn) {
            notInSet.add(i);
        }
        try {
            QueryEnumerator it = query.run();
            for (; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                int id = (Integer) document.getProperty("id");
                if (!notInSet.contains(id)) {
                    Field field = new Field(document.getProperties());
                    if (field.getForm() != null && formType.equals(field.getForm().getType())) {
                        list.add(field);
                    }
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }


    public Boolean saveOrUpdate(String name, String type, Form form) {
        Field field = new Field(name, type, form);
        return saveOrUpdate(field, getDocument(field.getId()));
    }

    public Field create(Field field) {
        return saveOrUpdate(field, null) ? field : null;

    }

    public Boolean update(Field field) {
        return saveOrUpdate(field);
    }
}
