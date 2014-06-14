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

    public FormLayouts(Map<String, Object> properties){
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
        if (null == form) properties.put("form", null);
        else
            properties.put("form", form.get());
        if (null == layout)
            properties.put("layout", null);
        else
            properties.put("layout", layout.get());
        return properties;
    }

    @Override
    public void set(Map<String, Object> properties) {
        id = (Integer)properties.get("id");
        Object object = properties.get("form");
        if (null == object) form = null;
        else form = new Form((Map<String, Object>) object);
        object = properties.get("layout");
        if (null == object) layout = null;
        else layout = new Layout((Map<String, Object>) object);
    }
}
