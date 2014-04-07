package cz.zcu.kiv.eeg.mobile.base2.data.model;

public class FormRow {

	private int id;   // Todo ID datasetu (zatím))
	private String name;
	private String description;
	private String mine;
	
	public FormRow(int id, String name, String description, String mine) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.mine = mine;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMine() {
		return mine;
	}

	public void setMine(String mine) {
		this.mine = mine;
	}
}
