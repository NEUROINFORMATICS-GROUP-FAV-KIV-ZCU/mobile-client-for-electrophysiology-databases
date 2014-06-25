package cz.zcu.kiv.eeg.mobile.base2.data.store;

import com.couchbase.lite.Document;

import cz.zcu.kiv.eeg.mobile.base2.data.model.User;

/**
 * Storage class for User.java
 *
 * @author Rahul Kadyan, mail@rahulkadyan.com
 * @version 1.0.0.
 */
public class UserStore extends Store {
    private final static String TAG = DataStore.class.getName();

    private final static String VIEW_NAME = "user-view";
    private final static String DOC_TYPE_VALUE = "user";


    /**
     * Instantiates a new User store.
     *
     * @param databaseHelper the database helper
     */
    public UserStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }

    /**
     * Gets user.
     *
     * @param id the id
     * @return the user
     */
    public User getUser(int id) {
        Document document = getDocument(id);
        if (null != document) return new User(document.getProperties());
        return null;
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    public User getUser() {
        return getUser(2);
    }

    /**
     * Gets test user.
     *
     * @return the test user
     */
    public User getTestUser() {
        return getUser(1);
    }
}
