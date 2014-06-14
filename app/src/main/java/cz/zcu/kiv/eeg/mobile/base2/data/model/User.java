package cz.zcu.kiv.eeg.mobile.base2.data.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 * Data container for user information.
 *
 * @author Jaroslav Hošek
 */
@Root(name = User.XML_ROOT)
public class User extends NoSQLData {
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

    public User(Map<String, Object> properties) {
        set(properties);
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

    @Override
    public Map<String, Object> get() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", id);
        m.put("firstName", firstName);
        m.put("surname", surname);
        m.put("username", username);
        m.put("password", password);
        m.put("rights", rights);
        m.put("url", url);
        return m;
    }

    @Override
    public void set(Map<String, Object> properties) {
        id = (Integer)properties.get("id");
        firstName = (String)properties.get("firstName");
        surname = (String)properties.get("surname");
        username = (String)properties.get("username");
        password = (String)properties.get("password");
        rights = (String)properties.get("rights");
        url = (String)properties.get("url");
    }
}