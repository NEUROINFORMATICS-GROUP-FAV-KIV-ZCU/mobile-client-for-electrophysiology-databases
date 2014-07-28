package cz.zcu.kiv.eeg.mobile.base2.data.builders;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

//import odml.core.Reader;
//import odml.core.Section;


import odml.core.Reader;
import odml.core.Section;
import odml.core.Writer;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import android.content.Context;
import android.util.Log;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FieldValue;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormLayouts;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FormBuilder {
	private static final String TAG = FormBuilder.class.getSimpleName();
	private DAOFactory daoFactory;
	private String xmlData;
	private Section odmlForm;
	private Section odmlRoot;
	private String layoutName;

	public FormBuilder(DAOFactory daoFactory, ResponseEntity<Resource> data) {
		this.daoFactory = daoFactory;

		Reader reader;
		try {
			reader = new Reader();
			this.xmlData = getStringFromInputStream(data.getBody().getInputStream());
			 //this.xmlData = getStringFromInputStream(readOdmlFromFile()); // todo testovaci odstranit tadz to nebude
			// treba

			odmlRoot = reader.load(data.getBody().getInputStream());
			 //odmlRoot = reader.load(readOdmlFromFile());// todo testovaci
			odmlForm = odmlRoot.getSection(0);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public String start() {
		if (odmlForm.getType().equalsIgnoreCase(Values.FORM)) {
			Form rootForm = saveForm(odmlForm);
			Layout layout = saveLayout(odmlForm, rootForm);
			saveFormLayouts(rootForm, layout);
			createFields(odmlForm.getSections(), rootForm, layout);
			return layout.getName();
		}
		return "";
	}

	public void createFields(Vector<Section> sections, Form form, Layout layout) {
		if (sections != null) {
			for (Section section : sections) {
				if (section.getType().equalsIgnoreCase(Values.FORM)) {
					Form subform = saveForm(section);

					Writer writer = new Writer(section);
					OutputStream stream = new ByteArrayOutputStream();
					writer.write(stream);
					xmlData = stream.toString();

					Layout subLayout = saveLayout(section, subform);
					saveFormLayouts(subform, layout, subLayout);
					saveField(section, form, subform, layout, subLayout);				
					createFields(section.getSections(), subform, subLayout);
				} else {
					saveField(section, form, null, layout, null);
				}
			}
		}
	}

	private Form saveForm(Section formSection) {
		return daoFactory.getFormDAO().saveOrUpdate(formSection.getReference(), odmlRoot.getDocumentDate());
	}

	private Layout saveLayout(Section formSection, Form form) {
		String name;
		Field major = null;
		Field minor = null;
		
		if (formSection.getProperty(Layout.LAYOUT_NAME) != null) {
			name =  formSection.getProperty(Layout.LAYOUT_NAME).getText();
		}
		// na serveru generatoru to chce upravit - neuklada se layout pro podformulář
		name = formSection.getReference() + "-generated";
		
		if (formSection.getProperty("previewMajor") != null) {
			String fieldName = formSection.getProperty("previewMajor").getText();
			major = createPrevField(fieldName, form);
		}
			
		if (formSection.getProperty("previewMinor") != null) {
			String fieldName = formSection.getProperty("previewMinor").getText();
			minor = createPrevField(fieldName, form);
		}
		
		Layout layout = daoFactory.getLayoutDAO().saveOrUpdate(name, xmlData, form, major, minor);		
		if (layout == null) {
			layout = daoFactory.getLayoutDAO().getLayout(name);
		}
		return layout;
	}

	private void saveFormLayouts(Form form, Layout layout) {
		daoFactory.getFormLayoutsDAO().saveOrUpdate(form, layout);
	}
	
	private void saveFormLayouts(Form form, Layout rootLayout, Layout layout) {
		daoFactory.getFormLayoutsDAO().saveOrUpdate(form, rootLayout, layout);
	}

	private void saveField(Section fieldSection, Form form, Form subForm, Layout layout, Layout subLayout) {
		Field field = daoFactory.getFieldDAO().getField(fieldSection.getName(), form.getType());

		if (field == null) {
			field = new Field(fieldSection.getName(), fieldSection.getType(), form, Values.STRING);
			setTextboxOptions(fieldSection, field);
			daoFactory.getFieldDAO().create(field);
		} else if (field.getType() == null) {
			field.setType(fieldSection.getType());
			setTextboxOptions(fieldSection, field);
			daoFactory.getFieldDAO().update(field);
		}

		LayoutProperty property = new LayoutProperty(field, layout);

		if (fieldSection.getProperty("label") != null) {
			property.setLabel(fieldSection.getProperty("label").getText());
		}
		if (fieldSection.getProperty("id") != null) {
			property.setIdNode(Integer.parseInt(fieldSection.getProperty("id").getText()));
		}
		if (fieldSection.getProperty("idTop") != null) {
			property.setIdTop(Integer.parseInt(fieldSection.getProperty("idTop").getText()));
		}
		if (fieldSection.getProperty("idBottom") != null) {
			property.setIdTop(Integer.parseInt(fieldSection.getProperty("idBottom").getText()));
		}
		if (fieldSection.getProperty("idLeft") != null) {
			property.setIdLeft(Integer.parseInt(fieldSection.getProperty("idLeft").getText()));
		}
		if (fieldSection.getProperty("idRight") != null) {
			property.setIdLeft(Integer.parseInt(fieldSection.getProperty("idRight").getText()));
		}
		if (fieldSection.getProperty("weight") != null) {
			property.setWeight(Integer.parseInt(fieldSection.getProperty("weight").getText()));
		} else {
			property.setWeight(100);
		}
		// combobox
		if (fieldSection.getProperty("values") != null) {
			for (Object value : fieldSection.getProperty("values").getValues()) {
				daoFactory.getFieldValueDAO().saveOrUpdate(new FieldValue((String) value, field));
			}
		}
		if (fieldSection.getProperty("previewMajor") != null) {
			String fieldName = fieldSection.getProperty("previewMajor").getText();
			property.setPreviewMajor(createPrevField(fieldName, subForm));
		}
		if (fieldSection.getProperty("previewMinor") != null) {
			String fieldName = fieldSection.getProperty("previewMinor").getText();
			property.setPreviewMinor(createPrevField(fieldName, subForm));
		}
		if (fieldSection.getProperty("cardinality") != null) {
			property.setCardinality(Integer.parseInt(fieldSection.getProperty("cardinality").getText()));
		}
		if (subLayout != null) {
			property.setSubLayout(subLayout);
		}

		daoFactory.getLayoutPropertyDAO().saveOrUpdate(property);
	}

	private void setTextboxOptions(Section fieldSection, Field field) {
		if (fieldSection.getProperty("datatype") != null) {
			field.setDataType(fieldSection.getProperty("datatype").getText());
		} else {
			field.setDataType(Values.STRING);
		}
		if (fieldSection.getProperty("minLength") != null) {
			field.setMinLength(Integer.parseInt(fieldSection.getProperty("minLength").getText()));
		}
		if (fieldSection.getProperty("maxLength") != null) {
			field.setMaxLength(Integer.parseInt(fieldSection.getProperty("maxLength").getText()));
		}
		if (fieldSection.getProperty("minValue") != null) {
			field.setMinValue(Integer.parseInt(fieldSection.getProperty("minValue").getText()));
		}
		if (fieldSection.getProperty("maxValue") != null) {
			field.setMaxValue(Integer.parseInt(fieldSection.getProperty("maxValue").getText()));
		}
		if (fieldSection.getProperty("defaultValue") != null) {
			field.setDefaultValue(fieldSection.getProperty("defaultValue").getText());
		}
	}

	private Field createPrevField(String fieldName, Form form) {
		Field prew = daoFactory.getFieldDAO().getField(fieldName, form.getType());
		if (prew == null) {
			prew = new Field(fieldName, null, form, Values.STRING);
			daoFactory.getFieldDAO().create(prew);
		}
		return prew;
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

	private InputStream readOdmlFromFile() {
		InputStream is = null;
		String filename = "PersonOdml.xml";
		Context context = daoFactory.getContext();

		try {
			is = context.getResources().getAssets().open(filename);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return is;
	}
}
