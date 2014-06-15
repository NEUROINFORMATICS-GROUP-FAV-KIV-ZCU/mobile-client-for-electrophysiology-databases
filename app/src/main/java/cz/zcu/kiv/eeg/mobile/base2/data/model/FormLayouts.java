package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 * @author Rahul Kadyan, (mail@rahulkadyan.com)
 */
public class FormLayouts extends NoSQLData {

    private int id;

    private Form form;

    private Layout layout;

    public FormLayouts() {
        super();
    }

    public FormLayouts(Form form, Layout layout) {
        super();
        this.form = form;
        this.layout = layout;
    }

    public FormLayouts(Map<String, Object> properties) {
        set(properties);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    @Override
    public Map<String, Object> get() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("id", id);
        properties.put("form", form != null ? form.get() : null);
        properties.put("layout", layout != null ? layout.get() : null);
        return properties;
    }

    @Override
    public void set(Map<String, Object> properties) {
        if (properties == null) return;
        id = (Integer) properties.get("id");
        Object object;
        form = (object = properties.get("form")) != null ? new Form((Map<String, Object>) object) : null;
        layout = (object = properties.get("layout")) != null ? new Layout((Map<String, Object>) object) : null;
    }
}
