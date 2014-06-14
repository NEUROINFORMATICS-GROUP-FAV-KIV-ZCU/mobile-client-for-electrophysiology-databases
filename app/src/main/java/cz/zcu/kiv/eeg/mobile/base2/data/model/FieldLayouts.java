package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 * @author Rahul Kadyan, mail@rahulkadyan.com
 */
public class FieldLayouts extends NoSQLData {
    private int id;

    private Field field;

    private Layout layout;

    public FieldLayouts() {
        super();
    }

    public FieldLayouts(Field field, Layout layout) {
        super();
        this.field = field;
        this.layout = layout;
    }

    public int getId() {
        return id;
    }

    @Override
    public Map<String, Object> get() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("id", id);
        properties.put("field", field != null ? field.get() : null);
        properties.put("layout", layout != null ? layout.get() : null);
        return properties;
    }

    @Override
    public void set(Map<String, Object> properties) {
        Object object;
        id = (Integer) properties.get("id");
        field = (object = properties.get("field")) != null ? new Field((Map<String, Object>) object) : null;
        layout = (object = properties.get("layout")) != null ? new Layout((Map<String, Object>) object) : null;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }
}
