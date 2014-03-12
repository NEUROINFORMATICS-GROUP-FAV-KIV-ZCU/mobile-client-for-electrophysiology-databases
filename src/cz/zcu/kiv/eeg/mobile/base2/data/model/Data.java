package cz.zcu.kiv.eeg.mobile.base2.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Data.TABLE_NAME)
public class Data {

    public static final String TABLE_NAME = "data";
    public static final String FK_ID_DataSet = "dataset_id";
    public static final String FK_ID_Field =   "field_id";

    @DatabaseField(generatedId = true)
    private int id;
    
    @DatabaseField
    private String data;

    @DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_DataSet)
    private DataSet dataset;
    
    @DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_Field)
    private DataSet field;

    public Data() {
	super();
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

    public DataSet getDataset() {
        return dataset;
    }

    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }

    public DataSet getField() {
        return field;
    }

    public void setField(DataSet field) {
        this.field = field;
    }
}
