package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 * @author Rahul Kadyan, (mail@rahulkadyan.com)
 */
public class Data implements NoSQLData {
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

    public Data(Map<String, Object> d) {
        id = (Integer) d.get("id");
        data = (String) d.get("data");
        dataset = (Dataset) d.get("dataset");
        field = (Field) d.get("field");
    }

    public Map<String, Object> get() {
        Map<String, Object> d = new HashMap<String, Object>();
        d.put("id", id);
        d.put("data", data);
        d.put("dataset", dataset.get());
        d.put("field", field.get());
        return d;
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
