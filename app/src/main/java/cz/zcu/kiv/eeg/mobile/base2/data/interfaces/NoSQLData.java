package cz.zcu.kiv.eeg.mobile.base2.data.interfaces;

import java.util.Map;

public abstract class  NoSQLData {
    public abstract void setId(int id);

    public abstract int getId();

    public abstract Map<String, Object> get();

    public abstract void set(Map<String, Object> properties);
}
