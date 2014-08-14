package cz.zcu.kiv.eeg.mobile.base2.data.builders;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;

import odml.core.Property;
import odml.core.Reader;
import odml.core.Section;
import odml.core.Writer;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import android.util.Log;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class DataBuilder {
	private static final String TAG = DataBuilder.class.getSimpleName();
	private DAOFactory daoFactory;
	private String xmlData;
	private Section odmlRoot;
	private MenuItems workspace;

	public DataBuilder(DAOFactory daoFactory, MenuItems workspace, ResponseEntity<Resource> data) {
		this.daoFactory = daoFactory;
		this.workspace = workspace;

		Reader reader;
		try {
			reader = new Reader();
			this.xmlData = getStringFromInputStream(data.getBody().getInputStream());
			odmlRoot = reader.load(data.getBody().getInputStream());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void getData() {
		// jedna sekce = jeden záznam
		Vector<Section> sections = odmlRoot.getSections();
		try {

			for (Section section : sections) {
				String formType = section.getType();
				Form form = daoFactory.getFormDAO().getForm(formType);

				// id v databázi na serveru
				Property propertyID = section.getProperty("id");
				int recordId = Integer.parseInt(propertyID.getValue().toString());
				Dataset dataset = daoFactory.getDataSetDAO().getDataSet(form, recordId, workspace);

				if (dataset == null) {
					dataset = new Dataset(form, recordId, workspace);
					dataset = daoFactory.getDataSetDAO().create(dataset);
					Vector<Property> properties = section.getProperties();

					for (Property property : properties) {
						String name = property.getName();
						String value = property.getValue().toString();				

						Field field = daoFactory.getFieldDAO().getField(name, formType);
						if (field != null) {
							daoFactory.getDataDAO().create(dataset, field, value);						
						}
					}

					Vector<Section> subformSections = section.getSections();
					if (subformSections != null) {
						for (Section subformSection : subformSections) {
							String subformName = subformSection.getName();
							Property property = subformSection.getProperty(0);
							String value = property.getValue().toString();
							Field field = daoFactory.getFieldDAO().getField(subformName, formType);
							if (field != null) {
								daoFactory.getDataDAO().create(dataset, field, value);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);			
		}
	}
	
	public static void createData(Form form, MenuItems workspace, DAOFactory daoFactory) {
		List<Dataset> datasety = daoFactory.getDataSetDAO().getDataSet(form, workspace);
		for (Dataset dataset : datasety) {

			Section rootSection;
			try {
				String formType = form.getType();
				rootSection = new Section();
				rootSection.setType(formType);
				rootSection.setReference(formType);				

				Property property = null;
				List<Data> dataTmp = daoFactory.getDataDAO().getDataByDataset(dataset.getId());
				for (Data data : dataTmp) {
					Field field = daoFactory.getFieldDAO().getField(data.getId());

					property = new Property(field.getName());
					property.setValue(data.getData());
					property.setType(field.getDataType());
				}

				rootSection.add(property);

				Writer writer = new Writer(rootSection);
				OutputStream stream = new ByteArrayOutputStream();
				writer.write(stream);
				String result = stream.toString();

				System.out.println("result: " + result);

			} catch (Exception e1) {
				Log.e(TAG, e1.getMessage());
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
			Log.e(TAG, e.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
		return sb.toString();
	}
}
