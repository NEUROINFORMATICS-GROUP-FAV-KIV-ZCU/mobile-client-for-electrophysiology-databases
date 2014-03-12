package cz.zcu.kiv.eeg.mobile.base2.data;

public class Data{
	private String name;
	private String data;
	private String id;

	/*public Data(String name, String data, String id) {
	    this.name = name;
	    this.data = data;
	    this.id = id;
	}*/

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getData() {
	    return data;
	}

	public void setData(String data) {
	    this.data = data;
	}

	public String getId() {
	    return id;
	}

	public void setId(String id) {
	    this.id = id;
	}	
}