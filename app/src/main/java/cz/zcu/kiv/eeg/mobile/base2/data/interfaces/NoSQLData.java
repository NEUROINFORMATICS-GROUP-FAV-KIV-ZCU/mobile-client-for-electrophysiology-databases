package cz.zcu.kiv.eeg.mobile.base2.data.interfaces;

import java.util.Map;

/**
 * Abstract class for NoSQL data
 * <p>
 * Provides API for parsing object from properties map and converting object to properties map
 * </p>
 *
 * @author Rahul Kadyan, mail@rahulkadyan.com
 * @version 1.0.0
 */
public abstract class NoSQLData {
    /**
     * Unique id
     *
     * @param id the id
     */
    public abstract void setId(int id);

    /**
     * Unique id
     * <p/>
     * For auto increment column implementation in NoSQL data
     *
     * @return the id
     */
    public abstract int getId();

    /**
     * Converts object to properties map
     *
     * @return the map
     */
    public abstract Map<String, Object> get();

    /**
     * Parse object from properties map
     *
     * @param properties the properties
     */
    public abstract void set(Map<String, Object> properties);
}
