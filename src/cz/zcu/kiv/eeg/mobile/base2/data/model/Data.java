package cz.zcu.kiv.eeg.mobile.base2.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
@DatabaseTable(tableName = Data.TABLE_NAME)
public class Data {

	public static final String TABLE_NAME = "data";
	public static final String FK_ID_DataSet = "dataset_id";
	public static final String FK_ID_Field = "field_id";

	@DatabaseField(generatedId = true)
	private int id;
	
	//TODO
	//doplním record ID, které naplním ID ze serveru při stažení dat (Person88) a pak všechny co budou vyplněný projdu a vyhledám
	// dataset s daným recordID a do data uložím ID datasetu

	@DatabaseField
	private String data;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_DataSet)
	private Dataset dataset;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_Field)
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
