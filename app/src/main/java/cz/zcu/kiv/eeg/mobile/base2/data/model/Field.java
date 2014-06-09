package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 *
 * @author Rahul Kadyan, (mail@rahulkadyan.com)
 */
public class Field implements NoSQLData {

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
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id",id);
        m.put("name", name);
        m.put("type", type);
        m.put("form", form.get());
        return m;
    }
}
