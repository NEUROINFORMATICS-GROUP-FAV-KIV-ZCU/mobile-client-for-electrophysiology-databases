package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 *
 * @author Rahul Kadyan, (mail@rahulkadyan.com)
 */
public class Dataset implements NoSQLData {

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
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", id);
        m.put("form", form);
        m.put("recordId", recordId);
        return m;
    }
}
