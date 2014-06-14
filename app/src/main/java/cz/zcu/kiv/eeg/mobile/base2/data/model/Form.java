package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 * @author Jaroslav Hošek
 * @author Rahul Kadyan, (mail@rahulkadyan.com)
 */
public class Form extends NoSQLData {

    public static final String FORM_MODE = "formMode";

    private String type;

    private Date date;

    public Form() {
        super();
    }

    public Form(String type, Date date) {
        super();
        this.type = type;
        this.date = date;
    }

    public Form(Map<String, Object> properties) {
        set(properties);
    }

    public Form(String type) {
        super();
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public void setId(int id) {
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public Map<String, Object> get() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("type", type);
        properties.put("date", date);
        return properties;
    }

    @Override
    public void set(Map<String, Object> properties) {
        if (null == properties) return;
        type = (String) properties.get("type");
        date = (Date) properties.get("date");
    }
}
