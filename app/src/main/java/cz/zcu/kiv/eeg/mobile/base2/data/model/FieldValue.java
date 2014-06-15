package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 * @author Rahul Kadyan, (mail@rahulkadyan.com)
 *         <p/>
 *         cz: zatím pouze pro uložení hodnot comboboxu
 *         en: not only to store values ​​combobox
 */
public class FieldValue extends NoSQLData {

    private int id;

    private String value;

    private Field field;

    public FieldValue() {
        super();
    }

    public FieldValue(String value, Field field) {
        super();
        this.value = value;
        this.field = field;
    }

    public FieldValue(Map<String, Object> properties) {
        set(properties);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    @Override
    public Map<String, Object> get() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", id);
        m.put("value", value);
        m.put("field", field != null ? field.get() : null);
        return m;
    }

    @Override
    public void set(Map<String, Object> properties) {
        if(properties == null) return;
        id = (Integer) properties.get("id");
        value = (String) properties.get("value");
        Object object = properties.get("field");
        if (null == object) field = null;
        else field = new Field((Map<String, Object>) object);
    }
}
