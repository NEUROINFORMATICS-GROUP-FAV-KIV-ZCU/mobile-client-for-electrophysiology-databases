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
	public static final String DATASET_RECORD_ID = "dataset_record_id";
	public static final String DATASET_PARENT_ID = "dataset_parent_id";
	public static final String FK_ID_ROOT_MENUITEMS = "root_menu_id";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FORM)
	private Form form;
	
	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_ROOT_MENUITEMS)
	private MenuItems rootMenu; //TODo rename to workspae
	
	@DatabaseField
	//private String recordId;
	private int recordId;
	
	@DatabaseField
	private int state;

	public Dataset() {
		super();
	}

	public Dataset(Form form, MenuItems workspace) {
		super();
		this.form = form;
		this.rootMenu = workspace;
	}
	
	public Dataset(Form form, MenuItems workspace, int state) {
		super();
		this.form = form;
		this.rootMenu = workspace;
		this.state = state;
	}
	
	public Dataset(Form form, int recordId, MenuItems workspace) {
		super();
		this.form = form;
		this.recordId = recordId;
		this.rootMenu = workspace;
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

	/*public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}*/

	public MenuItems getRootMenu() {
		return rootMenu;
	}

	public int getRecordId() {
		return recordId;
	}

	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}

	public void setRootMenu(MenuItems rootMenu) {
		this.rootMenu = rootMenu;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
