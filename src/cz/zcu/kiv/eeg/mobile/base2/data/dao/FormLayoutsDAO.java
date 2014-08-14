package cz.zcu.kiv.eeg.mobile.base2.data.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
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

	public CreateOrUpdateStatus create(final Form form, final Layout rootLayout) {
		try {
			if(getFormLayouts(form, rootLayout).size() == 0){
				FormLayouts formLayouts = new FormLayouts(form, rootLayout);
				return getFormLayoutDao().createOrUpdate(formLayouts);
			}		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public CreateOrUpdateStatus create(final Form form, final Layout rootLayout, final Layout sublayout) {
		try {
			if(getFormLayouts(form, rootLayout, sublayout).size() == 0){
				FormLayouts formLayouts = new FormLayouts(form, rootLayout, sublayout);
				return getFormLayoutDao().createOrUpdate(formLayouts);
			}						
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Layout> getLayout(final Form form, DAOFactory daoFactory) {
		try {
			QueryBuilder<FormLayouts, Integer> formLayoutsQb = getFormLayoutDao().queryBuilder();
			formLayoutsQb.where().eq(FormLayouts.FK_ID_FORM, form).and().isNull(FormLayouts.FK_ID_SUBLAYOUT);
			//QueryBuilder<Layout, String> layoutQb = getLayoutDao().queryBuilder();
			// join with the order query
			//List<Layout> results = layoutQb.join(formLayoutsQb).query();
			List<FormLayouts> formLayouts = formLayoutsQb.query();
			List<Layout> results = new ArrayList<Layout>();
			for(FormLayouts formLayout : formLayouts){
				if(formLayout.getRootLayout() != null){
					Layout layout = daoFactory.getLayoutDAO().getLayout(formLayout.getRootLayout().getName());
					results.add(layout);
				}
			}				
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
	
	public List<FormLayouts> getFormLayouts(final Layout rootLayout) {
		try {
			QueryBuilder<FormLayouts, Integer> formLayoutsQb = getFormLayoutDao().queryBuilder();
			formLayoutsQb.where().eq(FormLayouts.FK_ID_ROOT_LAYOUT, rootLayout);								
			return formLayoutsQb.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<FormLayouts> getFormLayouts(final Form form, final Layout rootLayout, final Layout sublayout) {
		try {
			QueryBuilder<FormLayouts, Integer> queryBuilder = getFormLayoutDao().queryBuilder();						
			Where<FormLayouts, Integer> where = queryBuilder.where();
			where.eq(FormLayouts.FK_ID_FORM, form);
			where.and();
			where.eq(FormLayouts.FK_ID_ROOT_LAYOUT, rootLayout);
			where.and();
			where.eq(FormLayouts.FK_ID_SUBLAYOUT, sublayout);		
			return queryBuilder.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<FormLayouts> getFormLayouts(final Form form, final Layout rootLayout) {
		try {
			QueryBuilder<FormLayouts, Integer> queryBuilder = getFormLayoutDao().queryBuilder();						
			Where<FormLayouts, Integer> where = queryBuilder.where();
			where.eq(FormLayouts.FK_ID_FORM, form);
			where.and();
			where.eq(FormLayouts.FK_ID_ROOT_LAYOUT, rootLayout);		
			return queryBuilder.query();
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
