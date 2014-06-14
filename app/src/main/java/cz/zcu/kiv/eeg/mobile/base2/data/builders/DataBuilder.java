package cz.zcu.kiv.eeg.mobile.base2.data.builders;

import android.util.Log;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import cz.zcu.kiv.eeg.mobile.base2.data.factories.StoreFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import odml.core.Property;
import odml.core.Reader;
import odml.core.Section;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class DataBuilder {
	private static final String TAG = DataBuilder.class.getSimpleName();
	private StoreFactory store;
	private String xmlData;
	// private Section odmlForm;
	private Section odmlRoot;

	public DataBuilder(StoreFactory store, ResponseEntity<Resource> data) {
		this.store = store;

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
		
		for (Section section : sections) {
			String formType = section.getType();
			String recordId = section.getName();
			
			Dataset dataset = store.getDatasetStore().getDataSet(recordId);
			Form form = store.getFormStore().getFormByType(formType);
			
			if (dataset == null) {
				dataset = store.getDatasetStore().create(form);
				Vector<Property> properties = section.getProperties();			
								
				
				for (Property property : properties) {
					String name = property.getName();
					String value = property.getValue().toString();
	
					Field field = store.getFieldStore().getField(name, formType);
					store.getDataStore().create(dataset, field, value);
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
