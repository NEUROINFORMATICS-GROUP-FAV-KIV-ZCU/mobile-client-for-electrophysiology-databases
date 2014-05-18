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
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FieldValue;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
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

	private static final String LAYOUT_NAME = "layoutName";
	private static final String ELEMENT_FORM = "form";
	private static final String ELEMENT_SET = "set";

	public FormBuilder(DAOFactory daoFactory, ResponseEntity<Resource> data) {
		this.daoFactory = daoFactory;

		Reader reader;
		try {
			reader = new Reader();
			// this.xmlData = getStringFromInputStream(data.getBody().getInputStream());
			this.xmlData = getStringFromInputStream(readOdmlFromFile()); //todo testovaci odstranit tadz to nebude treba

			 //odmlRoot = reader.load(data.getBody().getInputStream());
			odmlRoot = reader.load(readOdmlFromFile());// todo testovaci
			odmlForm = odmlRoot.getSection(0);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void start() {
		if (odmlForm.getType().equalsIgnoreCase(ELEMENT_FORM)) {
			Form rootForm = saveForm(odmlForm);
			Layout layout = saveLayout(getLayoutName(odmlForm), null, rootForm);
			createFields(odmlForm.getSections(), rootForm, layout);
		}
	}

	public void createFields(Vector<Section> sections, Form form, Layout layout) {
		for (Section section : sections) {
			if (section.getType().equalsIgnoreCase(ELEMENT_FORM)) {
				Form subform = saveForm(section);

				Writer writer = new Writer(section);
				OutputStream stream = new ByteArrayOutputStream();
				writer.write(stream);
				xmlData = stream.toString();

				Layout subLayout = saveLayout(getLayoutName(section), layout, subform);
				saveField(section, form, subform, layout, subLayout);
				createFields(section.getSections(), subform, subLayout);
			} else {
				saveField(section, form, null, layout, null);
			}
		}
	}

	private Form saveForm(Section formSection) {
		return daoFactory.getFormDAO().saveOrUpdate(formSection.getReference(), odmlRoot.getDocumentDate());
	}

	private Layout saveLayout(String name, Layout rootLayout, Form form) {
		Layout layout = daoFactory.getLayoutDAO().saveOrUpdate(name, rootLayout, xmlData, form);
		if (layout == null) {
			layout = daoFactory.getLayoutDAO().getLayout(name);
		}
		return layout;
	}

	private void saveField(Section fieldSection, Form form, Form subForm, Layout layout, Layout subLayout) {
		Field field = daoFactory.getFieldDAO().getField(fieldSection.getName(), form.getType());

		if (field == null) {
			field = new Field(fieldSection.getName(), fieldSection.getType(), form);
			daoFactory.getFieldDAO().create(field);
		} else if (field.getType() == null) {
			field.setType(fieldSection.getType());
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
		// zatím hodnoty pouze pro combobox
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

	private Field createPrevField(String fieldName, Form form) {
		Field prew = daoFactory.getFieldDAO().getField(fieldName, form.getType());
		if (prew == null) {
			prew = new Field(fieldName, null, form);
			daoFactory.getFieldDAO().create(prew);
		}
		return prew;
	}

	private String getLayoutName(Section form) {
		return form.getProperty(LAYOUT_NAME).getText();
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
