package cz.zcu.kiv.eeg.mobile.base2.data.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Data container for user information.
 * 
 * @author Jaroslav Hošek
 * 
 */
@Root(name = User.XML_ROOT)
public class User  extends NoSQL{
	public static final String TABLE_NAME = "users";
	public static final String XML_ROOT = "user";

	private int id;

	@Element(name = "name")
	private String firstName;

	@Element
	private String surname;

	private String username;

	private String password;

	@Element
	private String rights;

	private String url;

	public User() {
	}

	public User(String firstName, String surname, String rights) {
		this.firstName = firstName;
		this.surname = surname;
		this.rights = rights;
	}
	
	public User(int id, String username, String password, String url) {
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}
}