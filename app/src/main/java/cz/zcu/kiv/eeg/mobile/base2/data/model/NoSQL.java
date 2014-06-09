package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.HashMap;
import java.util.Map;

public class NoSQL {
    public final Map<String, Object> get() {
        Map<String, Object> m = new HashMap<String, Object>();
        for(java.lang.reflect.Field field: getClass().getFields()) {
            try {
                m.put(field.getName(), field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return m;
    }
}
