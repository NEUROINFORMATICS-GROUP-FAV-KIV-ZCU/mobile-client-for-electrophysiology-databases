package cz.zcu.kiv.eeg.mobile.base2.data.model;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class LayoutRow {

	private String name;
	private Boolean checked;
	

	public LayoutRow(String name, Boolean checked) {
		super();
		this.name = name;
		this.checked = checked;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Boolean isChecked() {
		return checked;
	}


	public void setChecked(Boolean checked) {
		this.checked = checked;
	}	
}
