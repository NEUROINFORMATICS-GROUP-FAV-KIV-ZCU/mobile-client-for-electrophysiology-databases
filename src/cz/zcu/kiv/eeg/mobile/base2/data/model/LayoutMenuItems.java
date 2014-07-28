package cz.zcu.kiv.eeg.mobile.base2.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Jaroslav Hošek
 *	Slouží k určení jaký layout je použit 
 * 
 */
@DatabaseTable(tableName = LayoutMenuItems.TABLE_NAME)
public class LayoutMenuItems {
	public static final String TABLE_NAME = "layout_menuitems";
	public static final String FK_ID_LAYOUT = "layout_id";
	public static final String FK_ID_MENUITEMS = "menu_id";
	public static final String FK_ID_ROOT_MENUITEMS = "root_menu_id";
	public static final String INDEX_NAME = "layout_menus_layoutmenu_idx";

	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_LAYOUT, uniqueIndexName = INDEX_NAME)
	private Layout layout;
	
	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_ROOT_MENUITEMS, uniqueIndexName = INDEX_NAME)
	private MenuItems rootMenu;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_MENUITEMS, uniqueIndexName = INDEX_NAME)
	private MenuItems menu;
	
	

	public LayoutMenuItems() {
		super();
	}

	public LayoutMenuItems(Layout layout,MenuItems rootMenu, MenuItems menu) {
		super();	
		this.layout = layout;
		this.menu = menu;
		this.rootMenu = rootMenu;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public MenuItems getMenu() {
		return menu;
	}

	public void setMenu(MenuItems menu) {
		this.menu = menu;
	}

	public MenuItems getRootMenu() {
		return rootMenu;
	}

	public void setRootMenu(MenuItems rootMenu) {
		this.rootMenu = rootMenu;
	}
}
