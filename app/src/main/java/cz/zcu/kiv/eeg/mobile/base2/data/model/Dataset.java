package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 * @author Rahul Kadyan, (mail@rahulkadyan.com)
 */
public class Dataset extends NoSQLData {

    public static final String DATASET_ID = "dataset_id";
    public static final String DATASET_ROOT_ID = "dataset_root_id";

    private int id;

    private Form form;

    private String recordId;

    public Dataset() {
        super();
    }

    public Dataset(Form form) {
        super();
        this.form = form;
    }

    public Dataset(Map<String, Object> properties) {
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

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    @Override
    public Map<String, Object> get() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("id", id);
        if (null == form)
            properties.put("form", null);
        else properties.put("form", form.get());
        properties.put("recordId", recordId);
        return properties;
    }

    @Override
    public void set(Map<String, Object> properties) {
        id = (Integer) properties.get("id");
        Object object = properties.get("form");
        if (null == object) form = null;
        else form = new Form((Map<String, Object>) object);
        recordId = (String) properties.get("recordId");
    }
}
