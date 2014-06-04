package cz.zcu.kiv.eeg.mobile.base2.data.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
@Root(name = User.XML_ROOT)
public class RecordCount {
	public static final String XML_ROOT = "recordCount";

	@Element
	int myRecords;
	@Element
	int publicRecords;

	// TODO přidat proměnnou sum
	public RecordCount() {
		super();
	}

	public RecordCount(int myRecords, int publicRecords) {
		super();
		this.myRecords = myRecords;
		this.publicRecords = publicRecords;
	}

	public int getMyRecords() {
		return myRecords;
	}

	public void setMyRecords(int myRecords) {
		this.myRecords = myRecords;
	}

	public int getPublicRecords() {
		return publicRecords;
	}

	public void setPublicRecords(int publicRecords) {
		this.publicRecords = publicRecords;
	}
}
