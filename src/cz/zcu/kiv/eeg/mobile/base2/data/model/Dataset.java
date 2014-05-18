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
	public static final String DATASET_ID = "dataset_id";
	public static final String DATASET_ROOT_ID = "dataset_root_id";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FORM)
	private Form form;
	
	@DatabaseField
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
}
