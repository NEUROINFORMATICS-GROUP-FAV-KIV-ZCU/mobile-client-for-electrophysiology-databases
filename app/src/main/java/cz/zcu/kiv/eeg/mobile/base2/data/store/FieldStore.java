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

/**
 * Storage class for FieldStore.java
 *
 * @author Rahul Kadyan, mail@rahulkadyan.com
 * @version 1.0.0.
 */
public class FieldStore extends Store {
    private final static String TAG = FieldStore.class.getName();

    private final static String VIEW_NAME = "field-view";
    private final static String DOC_TYPE_VALUE = "field";

    /**
     * Instantiates a new Field store.
     *
     * @param databaseHelper the database helper
     */
    public FieldStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }


    /**
     * Gets field.
     *
     * @param id the id
     * @return the field
     */
    public Field getField(int id) {
        Document document = getDocument(id);
        if (null != document) return new Field(document.getProperties());
        return null;
    }

    /**
     * Gets field.
     *
     * @param name the name
     * @param formType the form type
     * @return the field
     */
    public Field getField(String name, String formType) {
        List<String> columnNames = new ArrayList<String>();
        columnNames.add("name");
        columnNames.add("form-type");
        Query query = getQuery(columnNames, "name-and-form-type");
        List<Object> keys = new ArrayList<Object>();
        List<Object> compoundKey = new ArrayList<Object>();
        compoundKey.add(name);
        compoundKey.add(formType);
        keys.add(compoundKey);
        query.setKeys(keys);
        query.setLimit(1);
        try {
            QueryEnumerator it = query.run();
            if (it.hasNext())
                return new Field(it.next().getDocument().getProperties());
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets fields.
     *
     * @return the fields
     */
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

    /**
     * Gets fields.
     *
     * @param formType the form type
     * @return the fields
     */
    public List<Field> getFields(String formType) {
        List<Field> list = new ArrayList<Field>();
        if (null == formType) return list;
        Query query = getQuery("form-type");
        List<Object> keys = new ArrayList<Object>();
        keys.add(formType);
        query.setKeys(keys);
        try {
            QueryEnumerator it = query.run();
            for (; it.hasNext(); ) {
                list.add(new Field(it.next().getDocument().getProperties()));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Gets fields.
     *
     * @param name the name
     * @param formType the form type
     * @return the fields
     */
    public List<Field> getFields(String name, String formType) {
        List<Field> list = new ArrayList<Field>();
        if (null == formType || null == name) return list;
        List<String> columnNames = new ArrayList<String>();
        columnNames.add("name");
        columnNames.add("form-type");
        Query query = getQuery(columnNames, "name-and-form-type");
        List<Object> keys = new ArrayList<Object>();
        List<Object> compoundKey = new ArrayList<Object>();
        compoundKey.add(name);
        compoundKey.add(formType);
        keys.add(compoundKey);
        query.setKeys(keys);
        try {
            QueryEnumerator it = query.run();
            for (; it.hasNext(); ) {
                list.add(new Field(it.next().getDocument().getProperties()));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Gets fields.
     *
     * @param notIn the not in
     * @param formType the form type
     * @return the fields
     */
    public List<Field> getFields(Iterable<Integer> notIn, String formType) {
        List<Field> list = new ArrayList<Field>();
        if (null == formType) return list;
        Query query = getQuery("form-type");
        List<Object> keys = new ArrayList<Object>();
        keys.add(formType);
        query.setKeys(keys);
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
                    list.add(new Field(document.getProperties()));
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * Save or update.
     *
     * @param name the name
     * @param type the type
     * @param form the form
     * @return the boolean
     */
    public Boolean saveOrUpdate(String name, String type, Form form) {
        Field field = new Field(name, type, form);
        return saveOrUpdate(field, getDocument(field.getId()));
    }

    /**
     * Create field.
     *
     * @param field the field
     * @return the field
     */
    public Field create(Field field) {
        return saveOrUpdate(field, null) ? field : null;

    }

    /**
     * Update boolean.
     *
     * @param field the field
     * @return the boolean
     */
    public Boolean update(Field field) {
        return saveOrUpdate(field);
    }
}
