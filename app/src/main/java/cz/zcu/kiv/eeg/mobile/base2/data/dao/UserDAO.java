package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

import cz.zcu.kiv.eeg.mobile.base2.data.model.User;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class UserDAO {

	private DatabaseHelper databaseHelper;

	public UserDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	private Dao<User, Integer> getUserDao() throws SQLException {
		return databaseHelper.getUserDao();
	}

	public CreateOrUpdateStatus saveOrUpdate(final User user) {
		try {
			return getUserDao().createOrUpdate(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public User getUser() {
		try {
			return getUserDao().queryForId(2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public User getTestUser() {
		try {
			return getUserDao().queryForId(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}	
}
