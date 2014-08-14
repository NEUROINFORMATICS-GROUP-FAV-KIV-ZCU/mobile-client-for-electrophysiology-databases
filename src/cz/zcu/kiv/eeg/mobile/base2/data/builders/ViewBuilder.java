package cz.zcu.kiv.eeg.mobile.base2.data.builders;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Vector;

import odml.core.Reader;
import odml.core.Section;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.editor.LayoutDragListener;
import cz.zcu.kiv.eeg.mobile.base2.data.elements.UICheckbox;
import cz.zcu.kiv.eeg.mobile.base2.data.elements.UICombobox;
import cz.zcu.kiv.eeg.mobile.base2.data.elements.UIElement;
import cz.zcu.kiv.eeg.mobile.base2.data.elements.UIForm;
import cz.zcu.kiv.eeg.mobile.base2.data.elements.UITextbox;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormDetailsFragment;
/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class ViewBuilder {
	private static final String TAG = ViewBuilder.class.getSimpleName();
	public SparseArray<UIElement> elements;

	private ArrayList<LinearLayout> newColumn = new ArrayList<LinearLayout>();
	private DAOFactory daoFactory;

	private LinearLayout rootLayout;
	private LinearLayout formLayout;
	private Layout layout;

	private int formMode = 0;
	private Dataset dataset;
	private Activity ctx;
	public FormDetailsFragment fragment;
	private int availabelId = 1;
	private ActionMode mActionMode;
	private MenuItems menu;
	private int rootID;
	public String error = "";

	public ViewBuilder(FormDetailsFragment fragment, Layout layout, int datasetRecordId, int formMode, MenuItems menu,
			DAOFactory daoFactory) {
		super();	
		this.fragment = fragment;
		this.layout = layout;
		this.daoFactory = daoFactory;
		this.formMode = formMode;
		this.menu = menu;
		this.ctx = fragment.getActivity();		
		dataset = daoFactory.getDataSetDAO().getDataSet(layout.getRootForm(), datasetRecordId, menu.getParentId());
		elements = new SparseArray<UIElement>();
	}

	public LinearLayout getLinearLayout() {
		String layoutData = layout.getXmlData();
		Reader reader;
		try {
			if (layoutData != null) {
				reader = new Reader();
				Section odmlRoot = reader.load(new ByteArrayInputStream(layoutData.getBytes()));
				Section odmlForm = odmlRoot.getSection(0);
				loadViews(odmlForm.getSections(), odmlForm.getReference());
				recountPositions();
				System.out.println("test");
			}
		} catch (Exception e) {
			Log.e(TAG,e.getMessage());
		}
		return createForm();
	}

	private void loadViews(Vector<Section> sections, String reference) {
		for (Section section : sections) {
			createView(section.getName(), reference, false);
		}
	}

	public UIElement createView(String name, String reference, boolean asTemplate) {
		Field field = daoFactory.getFieldDAO().getField(name, reference);
		LayoutProperty property = daoFactory.getLayoutPropertyDAO().getProperty(field.getId(), layout.getName());
		
		if (asTemplate) {
			property.setIdNode(availabelId);
		}

		String type = field.getType();		
		UIElement element = null;		
		
		if (type.equalsIgnoreCase("textbox")) {
			element = new UITextbox(field, property, daoFactory, ctx, fragment, this);
			
		} else if (type.equalsIgnoreCase(Values.CHECKBOX)) {
			element = new UICheckbox(field, property, daoFactory, ctx, fragment, this);
		} else if (type.equalsIgnoreCase("combobox")) {
			element = new UICombobox(field, property, daoFactory, ctx, fragment, this);
		} else if (type.equalsIgnoreCase("choice")) {
			element = new UITextbox(field, property, daoFactory, ctx, fragment, this);
		} else if (type.equalsIgnoreCase(Values.FORM)) {		
			element = new UIForm(field, property, daoFactory, ctx, fragment, this);	
		}
	
		elements.put(property.getIdNode(), element);
		availabelId++;
		return element;
	}
	
	// z načtených view vytvořím layout (průchod zleva doprava a dolu)
	private LinearLayout createForm() { // TODO create form
		formLayout = new LinearLayout(ctx);
		formLayout.setOrientation(LinearLayout.VERTICAL);
		formLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		formLayout.setTag("rootLayout"); // todo je to potřeba?

		ScrollView scroller = new ScrollView(ctx);
		scroller.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		scroller.addView(formLayout);

		rootLayout = new LinearLayout(ctx);
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		rootLayout.addView(scroller);

		int nodeId = rootID;

		// průchod grafem
		if (elements.size() > 0) {
			while (true) {
				// vytvořím nový řádek
				LinearLayout rowLayout = new LinearLayout(ctx);
				rowLayout.setOrientation(LinearLayout.HORIZONTAL);
				rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT));// WRAP_CONTENT

				UIElement element = getElement(nodeId);				
				if(formMode == Values.FORM_EDIT_DATA || formMode == Values.FORM_EDIT_SUBFORM){
					element.initData(dataset);
				}		
				element.setDiffWeight(10);
				View view = element.getWrapLayout();
				rowLayout.addView(getNewColumnButton(false));
				rowLayout.addView(view);

				// přidání všech položek na řádku
				while (element.getIdRight() != 0) {
					element = getElement(element.getIdRight());
					rowLayout.addView(element.getWrapLayout());
				}

				element.setDiffWeight(10);
				rowLayout.addView(getNewColumnButton(false));
				formLayout.addView(rowLayout);

				// řádek níže
				if (element.getIdBottom() != 0) {
					nodeId = element.getIdBottom();
				} else {
					break;
				}
			}
			// editor - poslední přidávácí řádek
			LinearLayout rowLayout = new LinearLayout(ctx);
			rowLayout.setOrientation(LinearLayout.HORIZONTAL);
			rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
			rowLayout.addView(getNewColumnButton(false));
			rowLayout.addView(getNewColumnButton(false));
			formLayout.addView(rowLayout);

		}
		return rootLayout;
	}
	
	public void addFieldToForm(int fieldId, boolean isEnabledMove, Activity activity) {
		Field field = daoFactory.getFieldDAO().getField(fieldId);
		UIElement node = createView(field.getName(), field.getForm().getType(), true);

		LinearLayout rowLayout = new LinearLayout(ctx);
		rowLayout.setOrientation(LinearLayout.HORIZONTAL);
		rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		node.setDiffWeight(10);
		View view = node.getWrapLayout();
		rowLayout.addView(getNewColumnButton(isEnabledMove));
		rowLayout.addView(view);
		node.setDiffWeight(10);
		rowLayout.addView(getNewColumnButton(isEnabledMove));

		if (isEnabledMove) {
			node.setEditor();
		} else {
			node.setLocalEdit(this, activity);
		}
		// pole přidávám na předposlední řádek, poslední řádek slouží v editoru k přesunu
		formLayout.addView(rowLayout, formLayout.getChildCount() - 1);		
	}

	public void removeFieldFromDB(int fieldId) {			
		for (int i = 0; i < formLayout.getChildCount(); i++) {
			ViewGroup rowLayout = (ViewGroup) formLayout.getChildAt(i);			
			for (int j = 0; j < rowLayout.getChildCount(); j++) {
				ViewGroup wrapLayout = (ViewGroup) rowLayout.getChildAt(j);
				if(wrapLayout.getTag(R.id.FIELD_ID) != null){
					int id = (Integer) wrapLayout.getTag(R.id.FIELD_ID);
					if (id == fieldId) {
						if (rowLayout.getChildCount() == 3) { // pole plus dva postraní sloupce
							formLayout.removeViewAt(i);
							elements.remove(id);
						} else {
							rowLayout.removeViewAt(j);
							elements.remove(id);
						}						
						return;
					}
				}		
			}
		}
		daoFactory.getFieldDAO().delete(fieldId);
		
	}

	public void removeFieldFromForm(int fieldId) {
		for (int i = 0; i < formLayout.getChildCount(); i++) {
			ViewGroup rowLayout = (ViewGroup) formLayout.getChildAt(i);

			// TODO přepočet váhy - zbývajícím přepočítat a odstraněnému nastavit 100
			for (int j = 0; j < rowLayout.getChildCount(); j++) {
				ViewGroup wrapLayout = (ViewGroup) rowLayout.getChildAt(j);
				int id = (Integer) wrapLayout.getTag(R.id.NODE_ID);
				if (id == fieldId) {
					if (rowLayout.getChildCount() == 3) { // pole plus dva postraní sloupce
						formLayout.removeViewAt(i);
						elements.remove(id);
					} else {
						rowLayout.removeViewAt(j);
						elements.remove(id);
					}
					return;
				}
			}
		}
	}

	public void saveLayout() {
		int newId = 1;
		SparseArray<UIElement> newElements = new SparseArray<UIElement>();

		// průchod zleva doprava a dolu
		for (int i = 0; i < formLayout.getChildCount() - 1; i++) {
			ViewGroup rowLayout = (ViewGroup) formLayout.getChildAt(i);

			// zpracování řádku
			int count = rowLayout.getChildCount() - 2;
			for (int j = 1; j <= count; j++) {
				ViewGroup wrapLayout = (ViewGroup) rowLayout.getChildAt(j);
				View field = wrapLayout.getChildAt(1);
				int id = (Integer) field.getTag(R.id.NODE_ID);
				UIElement element = elements.get(id);
				element.setIdRight(0);
				element.setIdLeft(0);
				element.setIdTop(0);
				element.setIdBottom(0);
				element.removeLocalEdit();

				ViewGroup wrapLayoutChild;
				int idChild;
				// nalezení potomka (napravo)
				if (j + 1 <= count) {
					element.setIdRight(newId + 1);
				}
				// nalezení potomka (o řádek níže)
				else if (i + 1 < formLayout.getChildCount() - 1) {
					element.setIdBottom(newId + 1);
				}
				element.setIdNode(newId);
				newElements.put(newId, element);
				newId++;
			}
		}
		OdmlBuilder.createODML(newElements, layout, daoFactory);
	}

	public void enableEditor() {
		for (int i = 0; i < elements.size(); i++) {
			UIElement node = elements.valueAt(i);
			node.setEditor();
		}
		for (LinearLayout column : newColumn) {
			column.setVisibility(View.VISIBLE);
		}
		newColumn.get(newColumn.size() - 1).setVisibility(View.GONE);
	}

	public void disableEditor() {
		for (int i = 0; i < elements.size(); i++) {
			UIElement node = elements.valueAt(i);
			node.removeEditor();
		}
		for (LinearLayout column : newColumn) {
			column.setVisibility(View.GONE);
		}
	}

	public void enableLocalEdit(Activity activity) {
		for (int i = 0; i < elements.size(); i++) {
			UIElement element = elements.valueAt(i);
			element.setLocalEdit(this, activity);
		}
	}

	private UIElement getElement(int key) {
		return elements.get(key);
	}

	private LinearLayout getNewColumnButton(boolean isVisible) {
		Drawable editShape = ctx.getResources().getDrawable(R.drawable.shape);
		LinearLayout layout = new LinearLayout(ctx);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 10));
		layout.setBackgroundDrawable(editShape);
		layout.setTag(R.id.NODE_ID, -1);
		Button button = new Button(ctx);
		button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		button.setOnDragListener(new LayoutDragListener(ctx, elements));//, nodes));
		button.setText(" ");
		button.setTag(R.id.NODE_ID, -1);
		layout.addView(button);
		if (isVisible) {
			layout.setVisibility(View.VISIBLE);
		} else {
			layout.setVisibility(View.GONE);
		}
		newColumn.add(layout);
		return layout;
	}

	// slouží k uložení/aktualizace do databáze
	public int saveFormData(int datasetRecordID) {
				
		Form form = layout.getRootForm();
		Dataset dataset;
			
		if (formMode != Values.FORM_NEW_DATA) {	
			dataset = daoFactory.getDataSetDAO().getDataSet(layout.getRootForm(),datasetRecordID, menu.getParentId());

		} else {
			dataset = new Dataset(form, menu.getParentId(), Values.ACTION_ADD);
			daoFactory.getDataSetDAO().saveOrUpdate(dataset);
			
			//kvůli podformulářům		
			dataset.setRecordId(dataset.getId());		
			daoFactory.getDataSetDAO().saveOrUpdate(dataset);
		}

		for (int i = 0; i < elements.size(); i++) {
			error += elements.valueAt(i).createOrUpdateData(dataset);	
		}
		if(!error.equalsIgnoreCase("")){
			if(formMode == Values.FORM_NEW_DATA){
				daoFactory.getDataSetDAO().delete(dataset.getId());
			}									
			return -1;
		}
		return dataset.getId();
	}

	private void recountPositions() {
		rootID = Integer.MAX_VALUE;

		for (int i = 0; i < elements.size(); i++) {
			int childID = elements.keyAt(i);					
			// rootID je vždy nejnižší id na layoutu
			if (childID < rootID) {
				rootID = childID;
			}

			UIElement child = elements.valueAt(i);
			UIElement topParent = elements.get(child.getIdTop());
			UIElement leftParent = elements.get(child.getIdLeft());
			if (topParent != null) {
				topParent.setIdBottom(childID);
			} else if (leftParent != null) {
				leftParent.setIdRight(childID);
			}
		}
	}
	
	public void refreshSubforms(){
		for (int i = 0; i < elements.size(); i++) {
			UIElement element = elements.valueAt(i);
			if(element.getField().getType().equalsIgnoreCase(Values.FORM)){				
				((UIForm)element).initData(dataset);	
			};
					
		}
	}

	public ArrayList<Integer> getUsedFields() {
		ArrayList<Integer> usedFields = new ArrayList<Integer>();
		for (int i = 0; i < elements.size(); i++) {
			usedFields.add(elements.valueAt(i).getField().getId());
		}
		return usedFields;
	}

	public LinearLayout getRootLayout() {
		return rootLayout;
	}

	public DAOFactory getDaoFactory() {
		return daoFactory;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void onDestroyActionMode() {
		mActionMode = null;
	}

	public MenuItems getWorkspace() {
		return menu.getParentId();
	}

	public MenuItems getMenu() {
		return menu;
	}

	public void setMenu(MenuItems menu) {
		this.menu = menu;
	}

	public int getFormMode() {
		return formMode;
	}

	public void setFormMode(int formMode) {
		this.formMode = formMode;
	}	
}
