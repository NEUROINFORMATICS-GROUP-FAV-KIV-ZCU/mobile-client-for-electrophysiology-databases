package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 * @author Rahul Kadyan, (mail@rahulkadyan.com)
 */
public class FormRow extends NoSQLData {

    private int id; // TODO
    // cz: ID datasetu (zatím))
    // en: ID Dataset (yet))
    private String name;
    private String description;
    private String mine;

    public FormRow(int id, String name, String description, String mine) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.mine = mine;
    }

    public FormRow(Map<String, Object> properties){
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMine() {
        return mine;
    }

    public void setMine(String mine) {
        this.mine = mine;
    }

    @Override
    public Map<String, Object> get() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", id);
        m.put("name", name);
        m.put("description", description);
        m.put("mine", mine);
        return m;
    }

    @Override
    public void set(Map<String, Object> properties) {
        id = (Integer) properties.get("id");
        name = (String) properties.get("name");
        description = (String) properties.get("description");
        mine = (String) properties.get("mine");
    }
}
