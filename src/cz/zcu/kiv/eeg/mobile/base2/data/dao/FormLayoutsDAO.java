package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.QueryBuilder;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormLayouts;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FormLayoutsDAO {

	private DatabaseHelper databaseHelper;

	public FormLayoutsDAO(DatabaseHelper databaseHelper) {
		super();
		this.databaseHelper = databaseHelper;
	}

	private Dao<FormLayouts, Integer> getFormLayoutDao() throws SQLException {
		return databaseHelper.getFormLayoutsDao();
	}
	
	private Dao<Layout, String> getLayoutDao() throws SQLException {
		return databaseHelper.getLayoutDao();
	}
	
	private Dao<Form, String> getFormDao() throws SQLException {
		return databaseHelper.getFormDao();
	}

	public CreateOrUpdateStatus saveOrUpdate(final FormLayouts formLayouts) {
		try {
			return getFormLayoutDao().createOrUpdate(formLayouts);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public CreateOrUpdateStatus saveOrUpdate(final Form form, final Layout layout) {
		try {
			FormLayouts formLayouts = new FormLayouts(form, layout);
			return getFormLayoutDao().createOrUpdate(formLayouts);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public CreateOrUpdateStatus saveOrUpdate(final Form form, final Layout rootLayout, final Layout layout) {
		try {
			FormLayouts formLayouts = new FormLayouts(form, rootLayout, layout);
			return getFormLayoutDao().createOrUpdate(formLayouts);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Layout> getLayout(final Form form) {
		try {
			QueryBuilder<FormLayouts, Integer> formLayoutsQb = getFormLayoutDao().queryBuilder();
			formLayoutsQb.where().eq(FormLayouts.FK_ID_FORM, form);
			QueryBuilder<Layout, String> layoutQb = getLayoutDao().queryBuilder();
			// join with the order query
			List<Layout> results = layoutQb.join(formLayoutsQb).query();
			
					
			return results;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Form> getForm(final Layout rootLayout) {
		try {
			QueryBuilder<FormLayouts, Integer> formLayoutsQb = getFormLayoutDao().queryBuilder();
			formLayoutsQb.where().eq(FormLayouts.FK_ID_ROOT_LAYOUT, rootLayout);
			QueryBuilder<Form, String> formQb = getFormDao().queryBuilder();
			// join with the order query
			List<Form> results = formQb.join(formLayoutsQb).query();							
			return results;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<FormLayouts> getFormLayouts() {
		try {
			return getFormLayoutDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
