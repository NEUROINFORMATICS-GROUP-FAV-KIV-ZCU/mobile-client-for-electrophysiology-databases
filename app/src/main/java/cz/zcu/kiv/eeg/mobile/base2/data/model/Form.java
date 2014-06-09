package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 *
 * @author Jaroslav Hošek
 * @author Rahul Kadyan, (mail@rahulkadyan.com)
 */
public class Form implements NoSQLData{
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
    public Map<String, Object> get() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", type);
        m.put("date", date);
        return m;
    }
}
