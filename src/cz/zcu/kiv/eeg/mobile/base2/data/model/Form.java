package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
@DatabaseTable(tableName = Form.TABLE_NAME)
public class Form {
	public static final String TABLE_NAME = "forms";
	public static final String FORM_TYPE = "formType";
	public static final String FORM_MODE = "formMode";

	@DatabaseField(id = true, columnName = "type_id")
	private String type;

	@DatabaseField(dataType = DataType.DATE_LONG)
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
}
