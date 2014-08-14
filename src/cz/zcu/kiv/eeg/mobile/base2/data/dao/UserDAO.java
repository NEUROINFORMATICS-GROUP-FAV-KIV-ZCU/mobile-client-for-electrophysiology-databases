package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
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

	public void create(final User user) {
		try {
			getUserDao().create(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void update(final User user) {
		try {			
			getUserDao().update(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void delete(final int id) {
		try {			
			getUserDao().deleteById(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public User getUser(int id){
		try {
			return getUserDao().queryForId(id);		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<User> getUser(){
		try {
			return getUserDao().queryForAll();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
