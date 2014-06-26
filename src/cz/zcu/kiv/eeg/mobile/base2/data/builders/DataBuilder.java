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
import android.util.SparseArray;
import cz.zcu.kiv.eeg.mobile.base2.common.TaskFragmentActivity;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.DataDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.data.model.ViewNode;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class DataBuilder {
	private static final String TAG = DataBuilder.class.getSimpleName();
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
			Log.e(TAG, e.getMessage());
		}
	}

	public void getData() {
		// jedna sekce = jeden záznam
		Vector<Section> sections = odmlRoot.getSections();

		for (Section section : sections) {
			String formType = section.getType();
			String recordId = section.getName();

			Dataset dataset = daoFactory.getDataSetDAO().getDataSet(recordId);
			Form form = daoFactory.getFormDAO().getForm(formType);

			if (dataset == null) {
				dataset = new Dataset(form);
				dataset.setRecordId(recordId);
				dataset = daoFactory.getDataSetDAO().create(dataset);
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

	public static void createData(Form form, DAOFactory daoFactory) {
		List<Dataset> datasety = daoFactory.getDataSetDAO().getDataSet(form);

		for (Dataset dataset : datasety) {

			Section rootSection;
			try {
				String formType = form.getType();
				rootSection = new Section();
				rootSection.setType(formType);
				rootSection.setReference(formType);
				rootSection.setName(dataset.getRecordId());

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

		/*
		 * Vector<Section> sections = odmlRoot.getSections();
		 * 
		 * for (Section section : sections) { String formType = section.getType(); String recordId = section.getName();
		 * 
		 * Dataset dataset = daoFactory.getDataSetDAO().getDataSet(recordId); Form form =
		 * daoFactory.getFormDAO().getForm(formType);
		 * 
		 * if (dataset == null) { dataset = new Dataset(form); dataset.setRecordId(recordId); dataset =
		 * daoFactory.getDataSetDAO().create(dataset); Vector<Property> properties = section.getProperties();
		 * 
		 * 
		 * for (Property property : properties) { String name = property.getName(); String value =
		 * property.getValue().toString();
		 * 
		 * Field field = daoFactory.getFieldDAO().getField(name, formType); daoFactory.getDataDAO().create(dataset,
		 * field, value); } } }
		 */

		/*
		 * Section rootSection; try { String formType = layout.getRootForm().getType(); rootSection = new
		 * Section(formType, Values.ODML_FORM); rootSection.setReference(formType); Property property = new
		 * Property(Values.ODML_LAYOUT_ID, layout.getName()); property.setType(Values.ODML_STRING_TYPE);
		 * rootSection.add(property);
		 * 
		 * for (int i = 1; i <= nodes.size(); i++) { ViewNode node = nodes.get(i); //Field field =
		 * daoFactory.getFieldDAO().getField(node.getName(), formType); //LayoutProperty propertyDB =
		 * daoFactory.getLayoutPropertyDAO().getProperty(field.getId(), layout.getName()); Field field =
		 * node.getField(); LayoutProperty layoutProperty = node.getProperty();
		 * 
		 * Section section = new Section(field.getName(), field.getType());
		 * 
		 * Property id = new Property(Values.ODML_ID_NODE, i); property.setType(Values.ODML_INT_TYPE); section.add(id);
		 * //propertyDB.setIdNode(i);
		 * 
		 * 
		 * Property label = new Property(Values.ODML_LABEL, layoutProperty.getLabel());
		 * property.setType(Values.ODML_STRING_TYPE); section.add(label);
		 * 
		 * // TODO doplnit Required, datatype
		 * 
		 * if (node.getIdRight() != 0) { Property right = new Property(Values.ODML_ID_RIGHT, node.getIdRight());
		 * property.setType(Values.ODML_INT_TYPE); section.add(right); } else if (node.getIdBottom() != 0) { Property
		 * bottom = new Property(Values.ODML_ID_BOTTOM, node.getIdBottom()); property.setType(Values.ODML_INT_TYPE);
		 * section.add(bottom); } rootSection.add(section); //LayoutProperty test =
		 * daoFactory.getLayoutPropertyDAO().getProperty(layoutProperty.getId()); //if(test != null){
		 * daoFactory.getLayoutPropertyDAO().saveOrUpdate(layoutProperty); //}else{
		 * //daoFactory.getLayoutPropertyDAO().create(layoutProperty); //}
		 * 
		 * }
		 * 
		 * Writer writer = new Writer(rootSection); OutputStream stream = new ByteArrayOutputStream();
		 * writer.write(stream); String result = stream.toString(); layout.setXmlData(result);
		 * daoFactory.getLayoutDAO().saveOrUpdate(layout); System.out.println("result: " + result);
		 * 
		 * 
		 * 
		 * } catch (Exception e1) { Log.e(TAG, e1.getMessage()); }
		 */
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
