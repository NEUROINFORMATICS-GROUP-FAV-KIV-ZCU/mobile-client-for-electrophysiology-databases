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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.SpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.editor.LayoutDragListener;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FieldValue;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.data.model.ViewNode;
import cz.zcu.kiv.eeg.mobile.base2.ui.field.FieldAddActivity;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class ViewBuilder {
	private static final String TAG = ViewBuilder.class.getSimpleName();
	// todo přesunout
	private static final String LAYOUT_NAME = "layoutName";
	private static final String ELEMENT_FORM = "form";
	private static final String ELEMENT_SET = "set";
	public SparseArray<ViewNode> nodes = new SparseArray<ViewNode>(); // optimalizovaná HashMap<Integer, ?>, key = ID
																		// //TODO
																		// sekce
	private ArrayList<LinearLayout> newColumn = new ArrayList<LinearLayout>();
	private DAOFactory daoFactory;

	private LinearLayout rootLayout;
	private LinearLayout formLayout;
	private Layout layout;
	private Context ctx;
	private int evailabelId = 1;

	public ViewBuilder(Context ctx, Layout layout, DAOFactory daoFactory) {
		super();
		this.ctx = ctx;
		this.layout = layout;
		this.daoFactory = daoFactory;
	}

	public LinearLayout getLinearLayout() {
		String data = layout.getXmlData();
		Reader reader;
		try {
			reader = new Reader();
			Section odmlRoot = reader.load(new ByteArrayInputStream(data.getBytes()));
			Section odmlForm = odmlRoot.getSection(0);
			loadViews(odmlForm.getSections(), odmlForm.getReference());
			recountPositions();

			System.out.println("test");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return createForm();
	}

	private void loadViews(Vector<Section> sections, String reference) {
		for (Section section : sections) {
			// TODo tady bude list view
			if (section.getType().equalsIgnoreCase(ELEMENT_SET)) {
				// loadViews(section.getSection(0).getSections()); // todo tohle
				// upravit aby to vzalo formul�� a podle n�j vytvo�ilo list
				createView(section.getName(), reference, false);
			} else if (!section.getType().equalsIgnoreCase(ELEMENT_FORM)) {
				createView(section.getName(), reference, false);
			}
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

		if (type.equalsIgnoreCase("textbox")) {
			view = new EditText(ctx);
		} else if (type.equalsIgnoreCase("checkbox")) {
			view = new CheckBox(ctx);
		} else if (type.equalsIgnoreCase("combobox")) {
			Spinner combobox = new Spinner(ctx);
			ArrayList<String> itemList = new ArrayList<String>();

			List<FieldValue> values = daoFactory.getFieldValueDAO().getFieldValue(field.getId());
			for (FieldValue fieldValue : values) {
				itemList.add(fieldValue.getValue());
			}

			SpinnerAdapter spinnerAdapter = new SpinnerAdapter(ctx, R.layout.spinner_row_simple, itemList);
			combobox.setAdapter(spinnerAdapter);
			view = combobox;
		} else if (type.equalsIgnoreCase("choice")) {
			view = new EditText(ctx);
		} else if (type.equalsIgnoreCase("set")) { // todo tohle tu casem nebude
			EditText tmp = new EditText(ctx);
			tmp.setText("SET");
			view = tmp;
		}

		view.setTag(R.id.NODE_ID, id);
		ViewNode node = new ViewNode(view, field, property, ctx);

		nodes.put(id, node);
		evailabelId++;
		return node;

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

		int nodeId = 1;
		// průchod grafem
		while (true) {
			// vytvořím nový řádek
			LinearLayout rowLayout = new LinearLayout(ctx);
			rowLayout.setOrientation(LinearLayout.HORIZONTAL);
			rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));

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
		return rootLayout;
	}

	public void addFieldToForm(int fieldId, boolean isEnabledMove, Activity activity) {
		if (fieldId != 0) {
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

	public void removeFieldFromForm(int fieldId) {
		for (int i = 0; i < formLayout.getChildCount(); i++) {
			ViewGroup rowLayout = (ViewGroup) formLayout.getChildAt(i);

			//TODO přepočet váhy - zbývajícím přepočítat a odstraněnému nastavit 100
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
	public void initData(int datasetID) {
		if (datasetID != -1) {
			Form form = layout.getRootForm();
			Dataset dataset = daoFactory.getDataSetDAO().getDataSet(datasetID);

			if (dataset != null) {
				for (int i = 0; i < nodes.size(); i++) {
					Field field = daoFactory.getFieldDAO().getField(nodes.valueAt(i).getName(), form.getType());
					Data data = daoFactory.getDataDAO().getDataByDatasetAndField(dataset.getId(), field.getId());
					if (data != null) {
						nodes.valueAt(i).setData(data.getData());
					}
				}
			}
		}
	}

	// slouží k uložení/aktualizace do databáze
	public int saveOrUpdateData(int datasetID) {
		Form form = layout.getRootForm();
		Dataset dataset;
		if (datasetID != -1) {
			dataset = daoFactory.getDataSetDAO().getDataSet(datasetID);
		} else {
			dataset = new Dataset(form);
			daoFactory.getDataSetDAO().saveOrUpdate(dataset);
		}

		for (int i = 0; i < nodes.size(); i++) {
			Field field = daoFactory.getFieldDAO().getField(nodes.valueAt(i).getName(), form.getType());
			String data = nodes.valueAt(i).getData();
			if (data != null && !data.equals("")) {
				if (datasetID == -1) {
					daoFactory.getDataDAO().create(dataset, field, data);
				} else {
					daoFactory.getDataDAO().update(dataset, field, data); // hledat podle pole a datasetu
				}
			}
		}
		return dataset.getId();
	}

	private void recountPositions() {
		for (int i = 0; i < nodes.size(); i++) {
			int childID = nodes.keyAt(i);
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
}
