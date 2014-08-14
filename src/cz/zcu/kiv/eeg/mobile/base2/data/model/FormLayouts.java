package cz.zcu.kiv.eeg.mobile.base2.data.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
@DatabaseTable(tableName = FormLayouts.TABLE_NAME)
@Root(name = "form_layout")
public class FormLayouts {
	public static final String TABLE_NAME = "form_layouts";
	public static final String FK_ID_FORM = "form_id";
	public static final String FK_ID_ROOT_LAYOUT = "layout_root_id";
	public static final String FK_ID_SUBLAYOUT = "layout_id";
	public static final String INDEX_NAME = "form_layouts_formlayout_idx";

	@DatabaseField(generatedId = true)
	private int id;

	@Element(name = "form_name")
	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FORM, uniqueIndexName = INDEX_NAME)
	private Form form;
	
	@Element(name = "layout_name")
	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_ROOT_LAYOUT, uniqueIndexName = INDEX_NAME)
	private Layout rootLayout;

	@DatabaseField(foreign = true, canBeNull = true, columnName = FK_ID_SUBLAYOUT, uniqueIndexName = INDEX_NAME)
	private Layout sublayout;
	
	@Element()
	private String content;

	public FormLayouts() {
		super();
	}

	public FormLayouts(Form form, Layout rootLayout) {
		super();
		this.form = form;
		this.rootLayout = rootLayout;
	}
	
	public FormLayouts(Form form, Layout rootLayout, Layout sublayout) {
		super();
		this.form = form;
		this.rootLayout = rootLayout;
		this.sublayout = sublayout;
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

	public Layout getSublayout() {
		return sublayout;
	}

	public void setSublayout(Layout sublayout) {
		this.sublayout = sublayout;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Layout getRootLayout() {
		return rootLayout;
	}

	public void setRootLayout(Layout rootLayout) {
		this.rootLayout = rootLayout;
	}
}
