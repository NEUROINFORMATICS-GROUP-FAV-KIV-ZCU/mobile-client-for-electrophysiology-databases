package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 * @author Rahul Kadyan, (mail@rahulkadyan.com)
 */
public class Field extends NoSQLData {

    public static final String FIELD_ID = "field_id";
    private int id;

    private String name;

    private String type;

    private Form form;

    public Field() {
        super();
    }

    public Field(String name, String type, Form form) {
        super();
        this.name = name;
        this.type = type;
        this.form = form;
    }

    public Field(Map<String, Object> properties) {
        set(properties);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    @Override
    public Map<String, Object> get() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("id", id);
        properties.put("name", name);
        properties.put("type", type);
        if (null != form)
            properties.put("form", form.get());
        else properties.put("form", null);
        return properties;
    }

    @Override
    public void set(Map<String, Object> properties) {
        id = (Integer) properties.get("id");
        name = (String) properties.get("name");
        type = (String) properties.get("type");
        Object object = properties.get("form");
        if (null == object) form = null;
        else form = new Form((Map<String, Object>) object);
    }
}
