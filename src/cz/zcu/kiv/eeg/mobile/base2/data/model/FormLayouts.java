package cz.zcu.kiv.eeg.mobile.base2.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = FormLayouts.TABLE_NAME)
public class FormLayouts {
    public static final String TABLE_NAME = "form_layouts";
    public static final String FK_ID_FORM = "form_id";
    public static final String FK_ID_LAYOUT = "layout_id";
    public static final String INDEX_NAME = "form_layouts_formlayout_idx";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FORM, uniqueIndexName = INDEX_NAME)
    private Form form;

    @DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_LAYOUT, uniqueIndexName = INDEX_NAME)
    private Layout layout;

    public FormLayouts() {
	super();
    }

    public FormLayouts(Form form, Layout layout) {
	super();
	this.form = form;
	this.layout = layout;
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

    public Layout getLayout() {
	return layout;
    }

    public void setLayout(Layout layout) {
	this.layout = layout;
    }
}
