package cz.zcu.kiv.eeg.mobile.base2.data.builders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import odml.core.Property;
import odml.core.Reader;
import odml.core.Section;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class DataBuilder {
	private DAOFactory daoFactory;
	private String xmlData;
	// private Section odmlForm;
	private Section odmlRoot;

	public DataBuilder(DAOFactory daoFactory, ResponseEntity<Resource> data) {
		this.daoFactory = daoFactory;

		Reader reader;
		try {
			reader = new Reader();
			this.xmlData = getStringFromInputStream(data.getBody().getInputStream());
			odmlRoot = reader.load(data.getBody().getInputStream());			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getData() {
		// jedna sekce = jeden záznam
		Vector<Section> sections = odmlRoot.getSections();
		
		for (Section section : sections) {
			String formType = section.getType();
			String recordId = section.getName();
			
			Dataset dataset = daoFactory.getDataSetDAO().getDataSet(recordId);
			Form form = daoFactory.getFormDAO().getFormByType(formType);
			
			if (dataset == null) {
				dataset = daoFactory.getDataSetDAO().create(form);				
				Vector<Property> properties = section.getProperties();			
								
				
				for (Property property : properties) {
					String name = property.getName();
					String value = property.getValue().toString();
	
					Field field = daoFactory.getFieldDAO().getField(name, formType);				
					daoFactory.getDataDAO().create(dataset, field, value);
				}
			}
		}
	}
	
	private String getStringFromInputStream(InputStream is) {
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
}
