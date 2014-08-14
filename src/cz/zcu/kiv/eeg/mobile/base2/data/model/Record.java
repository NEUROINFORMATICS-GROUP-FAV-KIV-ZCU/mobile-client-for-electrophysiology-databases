package cz.zcu.kiv.eeg.mobile.base2.data.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
@Root(name = "record")
public class Record {
	public static final String XML_ROOT = "record";

	@Element
	int id;
	
	public Record() {
		super();
	}

	public Record(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
