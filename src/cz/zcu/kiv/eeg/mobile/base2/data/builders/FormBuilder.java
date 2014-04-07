package cz.zcu.kiv.eeg.mobile.base2.data.builders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import odml.core.Reader;
import odml.core.Section;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import android.content.Context;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItem;

public class FormBuilder {
	private DAOFactory daoFactory;
	private String xmlData;
	private Section odmlForm;
	private Section odmlRoot;	

	// todo přesunout
	private static final String LAYOUT_NAME = "layoutName";
	private static final String ELEMENT_FORM = "form";
	private static final String ELEMENT_SET = "set";

	public FormBuilder(DAOFactory daoFactory, ResponseEntity<Resource> data) {
		this.daoFactory = daoFactory;

		Reader reader;
		try {
			reader = new Reader();
			//this.xmlData = getStringFromInputStream(data.getBody().getInputStream());//live
			this.xmlData = getStringFromInputStream(readOdmlFromFile());  //todo testovaci
			
			//odmlRoot = reader.loadAxim(data.getBody().getInputStream()); todo live
			odmlRoot = reader.load(readOdmlFromFile()); // todo testovaci
			odmlForm = odmlRoot.getSection(0);							
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() {
		if (odmlForm.getType().equalsIgnoreCase(ELEMENT_FORM)) {
			Form form = saveForm(odmlForm);
			Layout layout = saveLayout();
			saveFormLayouts(form, layout);
			createFields(odmlForm.getSections(), form, layout);
			
			MenuItem item0 = new MenuItem("Dashboard", layout);
			daoFactory.getMenuItemDAO().saveOrUpdate(item0);				
			MenuItem item1 = new MenuItem("Testovaci formulář", layout);
			daoFactory.getMenuItemDAO().saveOrUpdate(item1);					
			
		} else {
			// TODO error pokud neexistuje
		}
	}

	public void createFields(Vector<Section> sections, Form form, Layout layout) {
		for (Section section : sections) {
			if (section.getType().equalsIgnoreCase(ELEMENT_SET)) {
				//todo upravit na form
				createFields(section.getSections(), form, layout);
			} else if (section.getType().equalsIgnoreCase(ELEMENT_FORM)) {
				 form = saveForm(section);
				 saveFormLayouts(form, layout);
				 createFields(section.getSections(), form, layout);
			}else{
				saveField(section, form);
			}
		}
	}

	// //////////////////////
	private Form saveForm(Section formSection) {
		return daoFactory.getFormDAO().saveOrUpdate(formSection.getName(),
				odmlRoot.getDocumentDate());
	}

	private Layout saveLayout() {
		return daoFactory.getLayoutDAO().saveOrUpdate(getLayoutName(), xmlData);
	}

	private void saveFormLayouts(Form form, Layout layout) {
		daoFactory.getFormLayoutsDAO().saveOrUpdate(form, layout);
	}
	
	private void saveField(Section field, Form form) {
		Field mField = new Field(field.getName(), field.getType(), form);
		if(field.getProperty("label") != null){
			mField.setLabel(field.getProperty("label").getText());
		}	
		daoFactory.getFieldDAO().saveOrUpdate(mField);	
	}

	// ////////////////////////

	private String getLayoutName() {
		return odmlForm.getProperty(LAYOUT_NAME).getText();
	}

	private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}

	//todo tohle odstranit a nebo uzavirat stream
	private InputStream readOdmlFromFile() {
		InputStream is = null;
		String filename = "PersonOdml.xml";
		Context context = daoFactory.getContext();

		try {
			is = context.getResources().getAssets().open(filename);
		} catch (Exception e) {
			e.getMessage();
		} /*
		 * finally { try { if (is != null) is.close(); } catch (Exception e2) {
		 * e2.getMessage(); } }
		 */
		return is;
	}

}
