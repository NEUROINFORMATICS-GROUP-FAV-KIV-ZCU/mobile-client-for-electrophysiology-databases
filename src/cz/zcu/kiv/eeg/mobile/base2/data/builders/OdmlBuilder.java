package cz.zcu.kiv.eeg.mobile.base2.data.builders;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import odml.core.Property;
import odml.core.Section;
import odml.core.Writer;
import android.util.SparseArray;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.ViewNode;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class OdmlBuilder {
	// odml z view

	public static void createODML(SparseArray<ViewNode> nodes, Layout layout, DAOFactory daoFactory) {
		Section rootSection;
		try {
			String formType = layout.getRootForm().getType();
			rootSection = new Section(formType, Values.ODML_FORM);
			Property property = new Property(Values.ODML_LAYOUT_NAME, layout.getName());
			property.setType(Values.ODML_STRING_TYPE);
			rootSection.add(property);

			for (int i = 1; i <= nodes.size(); i++) {
				ViewNode node = nodes.get(i);
				Field field = daoFactory.getFieldDAO().getField(node.name, formType);

				Section section = new Section(field.getName(), field.getType());

				Property id = new Property(Values.ODML_ID, i);
				property.setType(Values.ODML_INT_TYPE);
				section.add(id);

				Property label = new Property(Values.ODML_LABEL, field.getLabel());
				property.setType(Values.ODML_STRING_TYPE);
				section.add(label);
				
				// TODO doplnit Required, datatype

				if (node.right != 0) {
					Property right = new Property(Values.ODML_ID_RIGHT, node.right);
					property.setType(Values.ODML_INT_TYPE);
					section.add(right);
				} else if (node.bottom != 0) {
					Property bottom = new Property(Values.ODML_ID_BOTTOM, node.bottom);
					property.setType(Values.ODML_INT_TYPE);
					section.add(bottom);
				}
				
				rootSection.add(section);
			}
			Writer writer = new Writer(rootSection);
			OutputStream stream = new ByteArrayOutputStream();
			writer.write(stream);
			String result = stream.toString();
			System.out.println("result: " + result);

		} catch (Exception e1) {			
			e1.printStackTrace();
		}
	}
}
