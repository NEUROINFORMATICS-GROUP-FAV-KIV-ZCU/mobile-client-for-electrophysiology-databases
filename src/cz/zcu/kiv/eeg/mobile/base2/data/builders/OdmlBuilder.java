package cz.zcu.kiv.eeg.mobile.base2.data.builders;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import odml.core.Property;
import odml.core.Section;
import odml.core.Writer;
import android.util.Log;
import android.util.SparseArray;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.data.model.ViewNode;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class OdmlBuilder {
	private static final String TAG = OdmlBuilder.class.getSimpleName();
	// odml z view

	public static void createODML(SparseArray<ViewNode> nodes, Layout layout, DAOFactory daoFactory) {
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
				//Field field = daoFactory.getFieldDAO().getField(node.getName(), formType);
				//LayoutProperty propertyDB = daoFactory.getLayoutPropertyDAO().getProperty(field.getId(), layout.getName());
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
				//LayoutProperty test = daoFactory.getLayoutPropertyDAO().getProperty(layoutProperty.getId());
				//if(test != null){
					daoFactory.getLayoutPropertyDAO().saveOrUpdate(layoutProperty);
				//}else{
					//daoFactory.getLayoutPropertyDAO().create(layoutProperty);
				//}
				
			}
			
			Writer writer = new Writer(rootSection);
			OutputStream stream = new ByteArrayOutputStream();
			writer.write(stream);
			String result = stream.toString();
			layout.setXmlData(result);
			daoFactory.getLayoutDAO().saveOrUpdate(layout);		
			System.out.println("result: " + result);
			
			

		} catch (Exception e1) {			
			Log.e(TAG, e1.getMessage());
		}
	}
	
	public static void createDefaultODML(Field field, Layout layout, LayoutProperty property, DAOFactory daoFactory) {
		ViewNode node = new ViewNode(field, property);
		SparseArray<ViewNode> nodes = new SparseArray<ViewNode>();
		nodes.put(1, node);
		createODML(nodes, layout, daoFactory);	
	}
}
