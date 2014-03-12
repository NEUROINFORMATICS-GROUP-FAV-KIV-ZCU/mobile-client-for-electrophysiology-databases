package cz.zcu.kiv.eeg.mobile.base2.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DataSet.TABLE_NAME)
public class DataSet {

    public static final String TABLE_NAME = "dataSets";
    public static final String FK_ID_FORM = "form_id";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FORM)
    private Form form;

    public DataSet() {
	super();
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
