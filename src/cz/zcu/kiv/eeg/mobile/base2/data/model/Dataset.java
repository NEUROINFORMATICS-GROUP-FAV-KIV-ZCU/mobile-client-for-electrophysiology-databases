package cz.zcu.kiv.eeg.mobile.base2.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
@DatabaseTable(tableName = Dataset.TABLE_NAME)
public class Dataset {

	public static final String TABLE_NAME = "datasets";
	public static final String FK_ID_FORM = "form_id";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FORM)
	private Form form;

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

}
