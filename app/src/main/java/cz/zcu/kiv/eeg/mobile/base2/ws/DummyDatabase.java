package cz.zcu.kiv.eeg.mobile.base2.ws;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class DummyDatabase{

    private static final String TAG = "NoSQL";

    public DummyDatabase(Context context){
        Manager manager = null;
        try {
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            Log.e(TAG, "Cannot create manager object");
//            e.printStackTrace();
        }
        try {
            assert manager != null;
            Database database = manager.getDatabase("temp");
            Document document = database.createDocument();

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Calendar calendar = GregorianCalendar.getInstance();
            String currentTimeString = dateFormatter.format(calendar.getTime());

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("type", "list");
            properties.put("title", "Hello");
            properties.put("created_at", currentTimeString);
            properties.put("owner", "profile:1");
            properties.put("members", new ArrayList<String>());
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot get database");
//            e.printStackTrace();
        }
    }
}