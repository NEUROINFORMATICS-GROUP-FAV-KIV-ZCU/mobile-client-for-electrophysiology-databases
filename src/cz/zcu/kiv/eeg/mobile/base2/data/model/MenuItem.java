package cz.zcu.kiv.eeg.mobile.base2.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = MenuItem.TABLE_NAME)
public class MenuItem {

    public static final String TABLE_NAME = "menu_items";
    public static final String FK_ID_LAYOUT = "layout_id";
    public static final String INDEX_NAME = "menu_items_name_idx";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(uniqueIndexName = INDEX_NAME)
    private String name;

    @DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_LAYOUT)
    private Layout layout;

    public MenuItem() {
	super();
    }

    public MenuItem(String name, Layout layout) {
	super();
	this.name = name;
	this.layout = layout;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}
