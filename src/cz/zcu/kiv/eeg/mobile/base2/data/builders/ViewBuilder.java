package cz.zcu.kiv.eeg.mobile.base2.data.builders;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import odml.core.Reader;
import odml.core.Section;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.ViewNode;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ViewBuilder {

	// todo přesunout
	private static final String LAYOUT_NAME = "layoutName";
	private static final String ELEMENT_FORM = "form";
	private static final String ELEMENT_SET = "set";	
	SparseArray<ViewNode> nodes = new SparseArray<ViewNode>(); //optimalizovaná HashMap<Integer, ?>
	
	private Context ctx; 
	
	public ViewBuilder(Context ctx) {
		super();
		this.ctx = ctx;
	}

	public LinearLayout getLinearLayout(String layoutName) {
		DAOFactory daoFactory = new DAOFactory(ctx);
		String data = daoFactory.getLayoutDAO().getLayoutByName(layoutName).getXmlData();

		Reader reader;

		try {
			reader = new Reader();
			Section odmlRoot = reader.loadAxim(new ByteArrayInputStream(data.getBytes()));
			Section odmlForm = odmlRoot.getSection(0);
			loadViews(odmlForm.getSections());
			recountPositions(); // todo docasne
									
			System.out.println("test");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return createLinearLayout();
	}
	
	private LinearLayout createLinearLayout(){
		LinearLayout layout = new LinearLayout(ctx);
		layout.setOrientation(LinearLayout.VERTICAL);
		/*layout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
				//50, 100
				
				));*/
		
		ViewNode child = null;	
		// průchod grafem
		for(int i =0; i < nodes.size(); i++){
			ViewNode parent;
			if(i == 0){
				parent = nodes.get(1);							
			}else{
				parent = child;
			}
			
			if(parent.right != 0){
				child = nodes.get(parent.right);
			}else if (parent.bottom != 0){
				child = nodes.get(parent.bottom);
			}		
			
			parent.node.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
			layout.addView(parent.node, i);	
		}	
		return layout;
	}
	
	private void loadViews(Vector<Section> sections) {
		for (Section section : sections) {
			if (section.getType().equalsIgnoreCase(ELEMENT_SET)) {
				//loadViews(section.getSection(0).getSections());   // todo tohle upravit aby to vzalo formul�� a podle n�j vytvo�ilo list
				createView(section);
			}
			else if (!section.getType().equalsIgnoreCase(ELEMENT_FORM)) {
				createView(section);
			}
		}
		System.out.println("pokusnicek");
	}

	private void createView(Section field) {
		String type = field.getType();
		String name = field.getName();
		int top = 0;
		int left = 0;		
		
		if(field.getProperty("idTop") != null){
			top	= Integer.parseInt(field.getProperty("idTop").getText());
		}
		if(field.getProperty("idLeft") != null){
			left = Integer.parseInt(field.getProperty("idLeft").getText());
		}
			  
		int id = Integer.parseInt(field.getProperty("id").getText()); 
		View view = null;
		
		if (type.equalsIgnoreCase("textbox")) {
			 view = new EditText(ctx);
		}else if (type.equalsIgnoreCase("checkbox")) {
			view = new CheckBox(ctx);		
		}else if (type.equalsIgnoreCase("combobox")) {
			 view = new EditText(ctx);
		}else if (type.equalsIgnoreCase("choice")) {
			 view = new EditText(ctx);
		}else if (type.equalsIgnoreCase("set")) {  // todo tohle tu casem nebude
			EditText tmp = new EditText(ctx);
			tmp.setText("SET");
			 view = tmp;
		}
		
		ViewNode node = new ViewNode(view, name, top, 0, left, 0);
		nodes.put(id, node);
	}
		
	private void recountPositions(){		
		for(int i = 0; i < nodes.size(); i++){
		    int childID = nodes.keyAt(i);
		    ViewNode child = nodes.valueAt(i);
		    ViewNode topParent  = nodes.get(child.top);	
		    ViewNode leftParent  = nodes.get(child.left);	
		    if(topParent != null){
		    	topParent.bottom = childID;
		    }
		    else if(leftParent != null){
		    	 leftParent.right = childID;
		    }		   			    		    
		}
	}
}
