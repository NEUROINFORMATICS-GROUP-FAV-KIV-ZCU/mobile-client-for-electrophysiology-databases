package cz.zcu.kiv.eeg.mobile.base2.data.model;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FormRow {

	private int recordId; //                  TODO ID datasetu (zatím)) - předělám to na String -> Person35
	private String name;
	private String description;
	private String mine;

	public FormRow(int id, String name, String description, String mine) {
		super();
		this.recordId = id;
		this.name = name;
		this.description = description;
		this.mine = mine;
	}

	public int getId() {
		return recordId;
	}

	public void setId(int id) {
		this.recordId = id;
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
