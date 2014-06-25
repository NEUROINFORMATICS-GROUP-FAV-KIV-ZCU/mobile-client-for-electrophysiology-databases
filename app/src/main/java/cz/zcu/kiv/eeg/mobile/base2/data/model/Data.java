package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 * @author Rahul Kadyan, (mail@rahulkadyan.com)
 */
public class Data extends NoSQLData {
    private int id;

    private String data;

    private Dataset dataset;

    private Field field;

    public Data() {
        super();
    }

    public Data(Dataset dataset, Field field, String data) {
        super();
        this.dataset = dataset;
        this.field = field;
        this.data = data;
    }

    public Data(Map<String, Object> properties) {
        set(properties);
    }

    public Map<String, Object> get() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("id", id);
        properties.put("data", data);
        if (dataset != null) {
            properties.put("dataset", dataset.get());
            properties.put("dataset-id", dataset.getId());
        } else properties.put("dataset", null);
        if (field != null) {
            properties.put("field", field.get());
            properties.put("field-id", field.getId());
        }else properties.put("field", null);
        return properties;
    }

    @Override
    public void set(Map<String, Object> properties) {
        id = (Integer) properties.get("id");
        data = (String) properties.get("data");
        Object object;
        dataset = (object = properties.get("dataset")) != null ? new Dataset((Map<String, Object>) object) : null;
        field = (object = properties.get("field")) != null ? new Field((Map<String, Object>) object) : null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public Field getField() {
        return field;
    }

    public void Field(Field field) {
        this.field = field;
    }
}
