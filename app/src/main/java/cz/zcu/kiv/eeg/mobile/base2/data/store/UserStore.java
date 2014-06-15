package cz.zcu.kiv.eeg.mobile.base2.data.store;

import com.couchbase.lite.Document;

import cz.zcu.kiv.eeg.mobile.base2.data.model.User;

public class UserStore extends Store {
    private final static String TAG = DataStore.class.getName();

    private final static String VIEW_NAME = "user-view";
    private final static String DOC_TYPE_VALUE = "user";


    public UserStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }

    public User getUser(int id) {
        Document document = getDocument(id);
        if (null != document) return new User(document.getProperties());
        return null;
    }

    public User getUser() {
        return getUser(2);
    }

    public User getTestUser() {
        return getUser(1);
    }
}
