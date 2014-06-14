package cz.zcu.kiv.eeg.mobile.base2.data.builders;

import android.util.Log;
import android.util.SparseArray;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.StoreFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.data.model.ViewNode;
import odml.core.Property;
import odml.core.Section;
import odml.core.Writer;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class OdmlBuilder {
	private static final String TAG = OdmlBuilder.class.getSimpleName();
	// odml z view

	public static void createODML(SparseArray<ViewNode> nodes, Layout layout, StoreFactory store) {
		Section rootSection;
		try {
			String formType = layout.getRootForm().getType();
			rootSection = new Section(formType, Values.ODML_FORM);
			rootSection.setReference(formType);
			Property property = new Property(Values.ODML_LAYOUT_ID, layout.getName());
			property.setType(Values.ODML_STRING_TYPE);
			rootSection.add(property);

			for (int i = 1; i <= nodes.size(); i++) {
				ViewNode node = nodes.get(i);
				//Field field = store.getFieldDAO().getField(node.getName(), formType);
				//LayoutProperty propertyDB = store.getLayoutPropertyDAO().getProperty(field.getId(), layout.getName());
				Field field = node.getField();
				LayoutProperty layoutProperty = node.getProperty();
				
				Section section = new Section(field.getName(), field.getType());

				Property id = new Property(Values.ODML_ID_NODE, i);
				property.setType(Values.ODML_INT_TYPE);
				section.add(id);
				//propertyDB.setIdNode(i);
							
				
				Property label = new Property(Values.ODML_LABEL, layoutProperty.getLabel());
				property.setType(Values.ODML_STRING_TYPE);
				section.add(label);
				
				// TODO doplnit Required, datatype

				if (node.getIdRight() != 0) {
					Property right = new Property(Values.ODML_ID_RIGHT, node.getIdRight());
					property.setType(Values.ODML_INT_TYPE);
					section.add(right);
				} else if (node.getIdBottom() != 0) {
					Property bottom = new Property(Values.ODML_ID_BOTTOM, node.getIdBottom());
					property.setType(Values.ODML_INT_TYPE);
					section.add(bottom);
				}		
				rootSection.add(section);
				//LayoutProperty test = store.getLayoutPropertyDAO().getProperty(layoutProperty.getId());
				//if(test != null){
					store.getLayoutPropertyStore().saveOrUpdate(layoutProperty);
				//}else{
					//store.getLayoutPropertyDAO().create(layoutProperty);
				//}
				
			}
			
			Writer writer = new Writer(rootSection);
			OutputStream stream = new ByteArrayOutputStream();
			writer.write(stream);
			String result = stream.toString();
			layout.setXmlData(result);
			store.getLayoutStore().saveOrUpdate(layout);
			System.out.println("result: " + result);
			
			

		} catch (Exception e1) {			
			Log.e(TAG, e1.getMessage());
		}
	}
}
