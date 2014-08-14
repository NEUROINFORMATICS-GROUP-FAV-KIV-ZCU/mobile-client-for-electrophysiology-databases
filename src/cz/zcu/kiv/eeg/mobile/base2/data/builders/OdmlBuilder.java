package cz.zcu.kiv.eeg.mobile.base2.data.builders;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import odml.core.Property;
import odml.core.Section;
import odml.core.Writer;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.elements.UIElement;
import cz.zcu.kiv.eeg.mobile.base2.data.elements.UITextbox;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class OdmlBuilder {
	private static final String TAG = OdmlBuilder.class.getSimpleName();

	public static void createODML(SparseArray<UIElement> elements, Layout layout, DAOFactory daoFactory) {
		Section rootSection;
		try {
			String formType = layout.getRootForm().getType();
			rootSection = new Section(formType, Values.ODML_FORM);
			rootSection.setReference(formType);
			Property property = new Property(Values.ODML_LAYOUT_ID, layout.getName());
			property.setType(Values.ODML_STRING_TYPE);
			rootSection.add(property);

			for (int i = 1; i <= elements.size(); i++) {
				UIElement element = elements.get(i);
				Field field = element.getField();
				LayoutProperty layoutProperty = element.getProperty();
				
				Section section = new Section(field.getName(), field.getType());

				Property id = new Property(Values.ODML_ID_NODE, i);
				property.setType(Values.ODML_INT_TYPE);
				section.add(id);				
							
				
				Property label = new Property(Values.ODML_LABEL, layoutProperty.getLabel());
				property.setType(Values.ODML_STRING_TYPE);
				section.add(label);
				
				// TODO doplnit Required, datatype

				if (element.getIdRight() != 0) {
					Property right = new Property(Values.ODML_ID_RIGHT, element.getIdRight());
					property.setType(Values.ODML_INT_TYPE);
					section.add(right);
				} else if (element.getIdBottom() != 0) {
					Property bottom = new Property(Values.ODML_ID_BOTTOM, element.getIdBottom());
					property.setType(Values.ODML_INT_TYPE);
					section.add(bottom);
				}		
				rootSection.add(section);			
				daoFactory.getLayoutPropertyDAO().saveOrUpdate(layoutProperty);					
			}
			
			Writer writer = new Writer(rootSection);
			OutputStream stream = new ByteArrayOutputStream();
			writer.write(stream);
			String result = stream.toString();
			layout.setXmlData(result);
			layout.setState(Values.ACTION_EDIT);
			daoFactory.getLayoutDAO().saveOrUpdate(layout);		
			System.out.println("result: " + result);
			
			

		} catch (Exception e1) {			
			Log.e(TAG, e1.getMessage());
		}
	}
	
	public static void createDefaultODML(Field field, Layout layout, LayoutProperty property , DAOFactory daoFactory, Context ctx) {
		UIElement textBox = new UITextbox(field, property, daoFactory, ctx, null, null);
		SparseArray<UIElement> elements = new SparseArray<UIElement>();
		elements.put(1, textBox);
		createODML(elements, layout, daoFactory);	
	}
}
