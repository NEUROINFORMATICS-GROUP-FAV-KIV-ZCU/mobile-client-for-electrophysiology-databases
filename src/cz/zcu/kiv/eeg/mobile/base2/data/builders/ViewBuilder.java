package cz.zcu.kiv.eeg.mobile.base2.data.builders;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import odml.core.Reader;
import odml.core.Section;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.sax.StartElementListener;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.SpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.SubformAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.editor.LayoutDragListener;
import cz.zcu.kiv.eeg.mobile.base2.data.editor.SubformActionMode;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FieldValue;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormRow;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.data.model.ViewNode;
import cz.zcu.kiv.eeg.mobile.base2.ui.field.FieldEditorAddActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormDetailsFragment;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.ListAllFormsFragment;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class ViewBuilder {
	private static final String TAG = ViewBuilder.class.getSimpleName();
	public SparseArray<ViewNode> nodes = new SparseArray<ViewNode>(); // optimalizovaná HashMap<Integer, ?>,

	private ArrayList<LinearLayout> newColumn = new ArrayList<LinearLayout>();
	private DAOFactory daoFactory;

	private LinearLayout rootLayout;
	private LinearLayout formLayout;
	private Layout layout;
	private MenuItems menu;
	private Form form;
	private int formMode = 0;
	private Dataset dataset;
	private Activity ctx;
	FormDetailsFragment fragment;
	private int evailabelId = 1;
	// private int datasetId;
	private ActionMode mActionMode;
	private int rootID;
	private boolean isRootForm = true;

	public ViewBuilder(FormDetailsFragment fragment, Layout layout, int datasetId, int formMode, MenuItems menu,
			DAOFactory daoFactory) {
		super();
		this.fragment = fragment;
		this.layout = layout;
		this.daoFactory = daoFactory;
		// this.datasetId = datasetId;
		this.formMode = formMode;
		this.menu = menu;

		this.ctx = fragment.getActivity();
		form = layout.getRootForm();
		dataset = daoFactory.getDataSetDAO().getDataSet(datasetId);
	}

	public LinearLayout getLinearLayout() {
		String layoutData = layout.getXmlData();
		Reader reader;
		try {
			if(layoutData != null){
				reader = new Reader();
				Section odmlRoot = reader.load(new ByteArrayInputStream(layoutData.getBytes()));
				Section odmlForm = odmlRoot.getSection(0);
				loadViews(odmlForm.getSections(), odmlForm.getReference());
				recountPositions();
				System.out.println("test");
			}		
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return createForm();
	}

	private void loadViews(Vector<Section> sections, String reference) {
		for (Section section : sections) {
			createView(section.getName(), reference, false);
		}
		System.out.println("pokusnicek");
	}

	public ViewNode createView(String name, String reference, boolean asTemplate) {
		Field field = daoFactory.getFieldDAO().getField(name, reference);
		LayoutProperty property = daoFactory.getLayoutPropertyDAO().getProperty(field.getId(), layout.getName());

		if (asTemplate) {
			property.setIdNode(evailabelId);
		}

		String type = field.getType();
		int id = property.getIdNode();
		View view = null;
		String[] data = getData(field);

		if (type.equalsIgnoreCase("textbox")) {
			view = getEditText(data[0]);
		} else if (type.equalsIgnoreCase("checkbox")) {
			view = new CheckBox(ctx);
		} else if (type.equalsIgnoreCase("combobox")) {
			view = getSpinner(data[0], field);
		} else if (type.equalsIgnoreCase("choice")) {
			view = new EditText(ctx);
		} else if (type.equalsIgnoreCase(Values.FORM)) {
			view = createSubform(data, field, property);
		}

		view.setTag(R.id.NODE_ID, id);
		ViewNode node = new ViewNode(view, field, property, ctx, fragment, this);

		nodes.put(id, node);
		evailabelId++;
		return node;
	}

	private String[] getData(Field field) {
		String[] data = null;
		if (formMode == Values.FORM_EDIT_DATA) {
			List<Data> dataList = daoFactory.getDataDAO().getAllData(dataset.getId(), field.getId());
			data = new String[dataList.size()];
			int i = 0;
			for (Data dataF : dataList) {
				if (dataF != null) {
					data[i] = dataF.getData();
				}
				i++;
			}
		}

		if (data == null || data.length == 0) {
			data = new String[] { "" };
		}
		return data;
	}

	private View getEditText(String data) {
		EditText editText = new EditText(ctx);
		editText.setText(data);
		return editText;
	}

	private View getSpinner(String data, Field field) {
		Spinner combobox = new Spinner(ctx);
		ArrayList<String> itemList = new ArrayList<String>();

		List<FieldValue> values = daoFactory.getFieldValueDAO().getFieldValue(field.getId());
		for (FieldValue fieldValue : values) {
			itemList.add(fieldValue.getValue());
		}

		SpinnerAdapter spinnerAdapter = new SpinnerAdapter(ctx, R.layout.spinner_row_simple, itemList);
		combobox.setAdapter(spinnerAdapter);

		// set data
		for (int i = 0; i < spinnerAdapter.getCount(); i++) {
			if (data.equalsIgnoreCase(spinnerAdapter.getItem(i))) {
				combobox.setSelection(i);
			}
		}
		return combobox;
	}

	private View createSubform(String[] data, final Field field, LayoutProperty property) {
		final LinearLayout wrapLayout = new LinearLayout(ctx);
		wrapLayout.setOrientation(LinearLayout.VERTICAL);
		wrapLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		if (formMode == Values.FORM_EDIT_DATA) {
			Layout sublayout = getDaoFactory().getLayoutDAO().getLayout(property.getSubLayout().getName());
			final MenuItems menuItem = getSubmenuItem(property, sublayout);
			boolean isVisible = false; //todo isVisibleEmptyField

			if (data[0].equalsIgnoreCase("")) {
				isVisible = true;
			}

			for (String dataset : data) {
				wrapLayout.addView(createSubformItem(property, sublayout, field, wrapLayout, menuItem, dataset));
			}
			wrapLayout.addView(createEmptyItem(wrapLayout, field, menuItem, isVisible));
		}
		return wrapLayout;
	}

	public Spinner createSubformItem(LayoutProperty property, Layout sublayout, final Field field,
			final LinearLayout wrapLayout, final MenuItems menuItem, String subformDataset) {

		final Spinner spinner = new Spinner(ctx);
		final ViewBuilder vb = this;
		FormAdapter adapter = ListAllFormsFragment.getStaticAdapter(ctx, getSubmenuItem(property, sublayout),
				daoFactory);
		final int datasetId = (subformDataset.equalsIgnoreCase("")) ? -1 : Integer.valueOf(subformDataset);
		spinner.setAdapter(adapter);

		if (datasetId == -1) {
			spinner.setVisibility(View.GONE);
		} else {
			spinner.setSelection(adapter.getDatasetPosition(datasetId));
		}

		spinner.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (mActionMode != null) {
					return false;
				}
				// Start the CAB using the ActionMode.Callback defined above
				mActionMode = ctx.startActionMode(new SubformActionMode(fragment, vb, wrapLayout, spinner, field,
						menuItem, datasetId)); // todo předávat ID datasetu, to potom přidám do pole obsahující seznam
												// co chci smazat
				v.setSelected(true);
				return true;
			}
		});
		return spinner;
	}

	// je vytvořena vždy a pak nastavuji pouze viditelnost
	// přidáno na konec za všechny položky
	private Spinner createEmptyItem(final LinearLayout wrapLayout, final Field field, final MenuItems menuItem,
			boolean isVisible) {
		final ViewBuilder vb = this;
		final Spinner emptySpinner = new Spinner(ctx);

		ArrayList<String> itemList = new ArrayList<String>();
		itemList.add(Values.FORM_EMPTY);
		SpinnerAdapter emptySpinnerAdapter = new SpinnerAdapter(ctx, R.layout.spinner_row_simple, itemList);
		emptySpinner.setAdapter(emptySpinnerAdapter);

		if (isVisible) {
			emptySpinner.setVisibility(View.VISIBLE);
		} else {
			emptySpinner.setVisibility(View.GONE);
		}

		emptySpinner.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP && mActionMode == null) {
					int lastChildID = wrapLayout.getChildCount() - 2;
					Spinner spinner = (Spinner) wrapLayout.getChildAt(lastChildID);
					spinner.setVisibility(View.VISIBLE);
					spinner.performClick();
					emptySpinner.setVisibility(View.GONE);
					return true;
				}
				return false;
			}
		});

		emptySpinner.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (mActionMode != null) {
					return false;
				}
				// Start the CAB using the ActionMode.Callback defined above
				mActionMode = ctx.startActionMode(new SubformActionMode(fragment, vb, wrapLayout, emptySpinner, field,
						menuItem, -1)); // todo předávat ID datasetu, to potom přidám do pole obsahující
										// seznam co chci smazat
				v.setSelected(true);
				return true;
			}
		});

		return emptySpinner;
	}

	public MenuItems getSubmenuItem(LayoutProperty property, Layout subLayout) {
		MenuItems menuItem;
		if (getDaoFactory().getMenuItemDAO().getMenu(property.getLabel()) == null) {
			menuItem = new MenuItems(property.getLabel(), subLayout, subLayout.getRootForm(),
					property.getPreviewMajor(), property.getPreviewMinor(), null);
			getDaoFactory().getMenuItemDAO().saveOrUpdate(menuItem);
		} else {
			menuItem = getDaoFactory().getMenuItemDAO().getMenu(property.getLabel());
		}
		return menuItem;
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
		if(nodes.size() > 0){
			while (true) {
				// vytvořím nový řádek
				LinearLayout rowLayout = new LinearLayout(ctx);
				rowLayout.setOrientation(LinearLayout.HORIZONTAL);
				rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT));// WRAP_CONTENT

				ViewNode node = getViewNode(nodeId);
				node.setDiffWeight(10);
				View view = node.getWrapNode();
				rowLayout.addView(getNewColumnButton(false));
				rowLayout.addView(view);

				// přidání všech položek na řádku
				while (node.getIdRight() != 0) {
					node = getViewNode(node.getIdRight());
					rowLayout.addView(node.getWrapNode());
				}

				node.setDiffWeight(10);
				rowLayout.addView(getNewColumnButton(false));
				formLayout.addView(rowLayout);

				// řádek níže
				if (node.getIdBottom() != 0) {
					nodeId = node.getIdBottom();
				} else {
					break;
				}
			}
		}	
		return rootLayout;
	}

	public void addFieldsToForm(ArrayList<Integer> fields, boolean isEnabledMove, Activity activity) {
	//public void addFieldToForm(int fieldId, boolean isEnabledMove, Activity activity) {
		
		if (fields != null) {
			for(int fieldId : fields){
				Field field = daoFactory.getFieldDAO().getField(fieldId);
				ViewNode node = createView(field.getName(), field.getForm().getType(), true);

				LinearLayout rowLayout = new LinearLayout(ctx);
				rowLayout.setOrientation(LinearLayout.HORIZONTAL);
				rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));

				node.setDiffWeight(10);
				View view = node.getWrapNode();
				rowLayout.addView(getNewColumnButton(isEnabledMove));
				rowLayout.addView(view);
				node.setDiffWeight(10);
				rowLayout.addView(getNewColumnButton(isEnabledMove));

				if (isEnabledMove) {
					node.setEditor(nodes);
				} else {
					node.setLocalEdit(this, activity);
				}
				formLayout.addView(rowLayout);
			}		
		}
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
						nodes.remove(id);
					} else {
						rowLayout.removeViewAt(j);
						nodes.remove(id);
					}
					return;
				}
			}
		}
	}

	public void saveLayout() {
		int newId = 1;
		SparseArray<ViewNode> newNodes = new SparseArray<ViewNode>();

		// průchod zleva doprava a dolu
		for (int i = 0; i < formLayout.getChildCount(); i++) {
			ViewGroup rowLayout = (ViewGroup) formLayout.getChildAt(i);

			// zpracování řádku
			int count = rowLayout.getChildCount() - 2;
			for (int j = 1; j <= count; j++) {
				ViewGroup wrapLayout = (ViewGroup) rowLayout.getChildAt(j);
				View field = wrapLayout.getChildAt(1);
				int id = (Integer) field.getTag(R.id.NODE_ID);
				ViewNode node = nodes.get(id);
				node.setIdRight(0);
				node.setIdLeft(0);
				node.setIdTop(0);
				node.setIdBottom(0);
				node.removeLocalEdit();

				ViewGroup wrapLayoutChild;
				int idChild;
				// nalezení potomka (napravo)
				if (j + 1 <= count) {
					node.setIdRight(newId + 1);
				}
				// nalezení potomka (o řádek níže)
				else if (i + 1 < formLayout.getChildCount()) {
					node.setIdBottom(newId + 1);
				}
				node.setIdNode(newId);
				newNodes.put(newId, node);
				newId++;
			}
		}
		OdmlBuilder.createODML(newNodes, layout, daoFactory);
	}

	public void enableEditor() {
		for (int i = 0; i < nodes.size(); i++) {
			ViewNode node = nodes.valueAt(i);
			node.setEditor(nodes);
		}
		for (LinearLayout column : newColumn) {
			column.setVisibility(View.VISIBLE);
		}
	}

	public void disableEditor() {
		for (int i = 0; i < nodes.size(); i++) {
			ViewNode node = nodes.valueAt(i);
			node.removeEditor();
		}
		for (LinearLayout column : newColumn) {
			column.setVisibility(View.GONE);
		}
	}

	public void enableLocalEdit(Activity activity) {
		for (int i = 0; i < nodes.size(); i++) {
			ViewNode node = nodes.valueAt(i);
			node.setLocalEdit(this, activity);
		}
	}

	private ViewNode getViewNode(int key) {
		return nodes.get(key);
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
		button.setOnDragListener(new LayoutDragListener(ctx, nodes));
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

	// načte data do formuláře
	/*
	 * public void initData(int datasetID) { //if (datasetID != Values.NEW_DATA) { if (formMode != Values.FORM_NEW_DATA)
	 * { Form form = layout.getRootForm(); Dataset dataset = daoFactory.getDataSetDAO().getDataSet(datasetID);
	 * 
	 * if (dataset != null) { for (int i = 0; i < nodes.size(); i++) { Field field =
	 * daoFactory.getFieldDAO().getField(nodes.valueAt(i).getName(), form.getType()); Data data =
	 * daoFactory.getDataDAO().getData(dataset.getId(), field.getId()); if (data != null) {
	 * nodes.valueAt(i).setData(data.getData()); } } } } }
	 */

	// slouží k uložení/aktualizace do databáze
	//public int saveFormData(int datasetID) {
	public int saveFormData(int datasetID) {
		Form form = layout.getRootForm();
		Dataset dataset;
		if (formMode != Values.FORM_NEW_DATA) {
			dataset = daoFactory.getDataSetDAO().getDataSet(datasetID);
		} else {
			dataset = new Dataset(form);
			daoFactory.getDataSetDAO().saveOrUpdate(dataset);
		}

		for (int i = 0; i < nodes.size(); i++) {
			Field field = daoFactory.getFieldDAO().getField(nodes.valueAt(i).getName(), form.getType());
			
			// položky podformuláře
			if (field.getType().equalsIgnoreCase(Values.FORM)) {
				ViewNode node = nodes.valueAt(i);
				LinearLayout spinnerLayout = (LinearLayout) node.getNode();
				String[] data = getData(field);
				
				for (int j = 0; j < spinnerLayout.getChildCount(); j++) {				
					Spinner spinner = (Spinner)spinnerLayout.getChildAt(j);
					if(spinner.getSelectedItem() instanceof FormRow && spinner.getVisibility() == View.VISIBLE){						
						FormRow row = (FormRow) spinner.getSelectedItem();
						boolean exists = false;
						
						// kontrola zda je již prvek v databázi
						for(int k = 0; k < data.length; k++){						
							if(data[k] != null && data[k].equalsIgnoreCase(Integer.toString(row.getId()))){
								data[k] = null;
								exists = true;
							}
						}
						// uložit do databáze
						if(!exists){
							createOrUpdateData(Integer.toString(row.getId()), dataset, field);
						}											
					}				
				}
				// co zbylo v data je k odstranění z databáze
				for(String idForRemove : data){		
					if(idForRemove != null){
						daoFactory.getDataDAO().delete(dataset, field, idForRemove);
					}				
				}
			
			} 		
			// ostatní pole 	
			else {
				String data = nodes.valueAt(i).getData();
				createOrUpdateData(data, dataset, field);
			}

		}
		return dataset.getId();
	}
	
	private void createOrUpdateData(String data, Dataset dataset, Field field){
		if (data != null && !data.equals("")) {
			if(field.getType().equalsIgnoreCase(Values.FORM)){
				if(daoFactory.getDataDAO().getData(dataset, field, data) == null){
					daoFactory.getDataDAO().create(dataset, field, data);	
					daoFactory.getDataDAO().getAllData(dataset.getId(), field.getId());
				}
			}
			// ostatní pole
			else if (formMode == Values.FORM_NEW_DATA) {
				daoFactory.getDataDAO().create(dataset, field, data);
			} else {
				if (daoFactory.getDataDAO().getData(dataset, field) == null) {
					daoFactory.getDataDAO().create(dataset, field, data);
				} else {
					daoFactory.getDataDAO().update(dataset, field, data);
				}
			}
		}
	}
	
	private void recountPositions() {
		rootID = Integer.MAX_VALUE;

		for (int i = 0; i < nodes.size(); i++) {
			int childID = nodes.keyAt(i);

			// rootID je vždy nejnižší id na layoutu
			if (childID < rootID) {
				rootID = childID;
			}

			ViewNode child = nodes.valueAt(i);
			ViewNode topParent = nodes.get(child.getIdTop());
			ViewNode leftParent = nodes.get(child.getIdLeft());
			if (topParent != null) {
				topParent.setIdBottom(childID);
			} else if (leftParent != null) {
				leftParent.setIdRight(childID);
			}
		}
	}

	public ArrayList<Integer> getUsedFields() {
		ArrayList<Integer> usedFields = new ArrayList<Integer>();
		for (int i = 0; i < nodes.size(); i++) {
			usedFields.add(nodes.valueAt(i).getFieldId());
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

	/*
	 * public int getDatasetId() { return datasetId; }
	 * 
	 * public void setDatasetId(int datasetId) { this.datasetId = datasetId; }
	 */
}