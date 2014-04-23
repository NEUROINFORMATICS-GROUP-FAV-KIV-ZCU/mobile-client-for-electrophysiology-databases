package cz.zcu.kiv.eeg.mobile.base2.data.builders;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Vector;

import odml.core.Reader;
import odml.core.Section;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.SpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.editor.LayoutDragListener;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.ViewNode;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class ViewBuilder {

	// todo přesunout
	private static final String LAYOUT_NAME = "layoutName";
	private static final String ELEMENT_FORM = "form";
	private static final String ELEMENT_SET = "set";
	private SparseArray<ViewNode> nodes = new SparseArray<ViewNode>(); // optimalizovaná HashMap<Integer, ?>, key = ID
																		// sekce
	private ArrayList<LinearLayout> newColumn = new ArrayList<LinearLayout>();
	private DAOFactory daoFactory;
	private LinearLayout formLayout;
	private Layout layout;
	private Context ctx;

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
			loadViews(odmlForm.getSections());
			recountPositions();

			System.out.println("test");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return createForm();
	}

	private void loadViews(Vector<Section> sections) {
		for (Section section : sections) {
			// TODo tady bude list view
			if (section.getType().equalsIgnoreCase(ELEMENT_SET)) {
				// loadViews(section.getSection(0).getSections()); // todo tohle
				// upravit aby to vzalo formul�� a podle n�j vytvo�ilo list
				createView(section);
			} else if (!section.getType().equalsIgnoreCase(ELEMENT_FORM)) {
				createView(section);
			}
		}
		System.out.println("pokusnicek");
	}

	private void createView(Section field) {
		String type = field.getType();
		String name = field.getName();
		int weight = 100;
		int top = 0;
		int left = 0;

		if (field.getProperty("idTop") != null) {
			top = Integer.parseInt(field.getProperty("idTop").getText());
		}
		if (field.getProperty("idLeft") != null) {
			left = Integer.parseInt(field.getProperty("idLeft").getText());
		}
		if (field.getProperty("weight") != null) {
			weight = Integer.parseInt(field.getProperty("weight").getText());
		}

		int id = Integer.parseInt(field.getProperty("id").getText());
		View view = null;

		if (type.equalsIgnoreCase("textbox")) {
			view = new EditText(ctx);
		} else if (type.equalsIgnoreCase("checkbox")) {
			view = new CheckBox(ctx);
		} else if (type.equalsIgnoreCase("combobox")) {
			Spinner combobox = new Spinner(ctx);
			ArrayList<String> itemList = new ArrayList<String>();
			for (Object value : field.getProperty("values").getValues()) {
				itemList.add(value.toString());
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

		view.setTag(id);

		ViewNode node = null;
		if (field.getProperty("label") != null) {
			LinearLayout layout = new LinearLayout(ctx);
			layout.setTag(id);

			TextView label = new TextView(ctx);
			label.setText(field.getProperty("label").getText());
			label.setTag(id);
			label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

			node = new ViewNode(id, view, layout, label, name, top, 0, left, 0, weight);
		} else {
			node = new ViewNode(view, name, top, 0, left, 0, weight);
		}
		nodes.put(id, node);

	}

	// z načtených view vytvořím layout (průchod zleva doprava a dolu)
	private LinearLayout createForm() { // TODO create form
		formLayout = new LinearLayout(ctx);
		formLayout.setOrientation(LinearLayout.VERTICAL);
		formLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		formLayout.setTag("rootLayout");

		int nodeId = 1;
		// průchod grafem
		while (true) {
			// vytvořím nový řádek
			LinearLayout rowLayout = new LinearLayout(ctx);
			rowLayout.setTag("řádek_" + nodeId);
			rowLayout.setOrientation(LinearLayout.HORIZONTAL);
			rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));

			ViewNode node = getViewNode(nodeId);
			node.setDiffWeight(10);
			View view = node.getWrapNode();
			rowLayout.addView(getWrapButton());
			rowLayout.addView(view);

			// přidání všech položek na řádku
			while (node.right != 0) {
				node = getViewNode(node.right);
				rowLayout.addView(node.getWrapNode());
			}

			node.setDiffWeight(10);
			rowLayout.addView(getWrapButton());
			formLayout.addView(rowLayout);

			if (node.bottom != 0) {
				nodeId = node.bottom;
			} else {
				break;
			}
		}
		return formLayout;
	}

	public void saveLayout() {
		SparseArray<ViewNode> newNodes = new SparseArray<ViewNode>();
		int newId = 1;

		// průchod zleva doprava a dolu
		for (int i = 0; i < formLayout.getChildCount(); i++) {
			ViewGroup rowLayout = (ViewGroup) formLayout.getChildAt(i);

			// zpracování řádku
			int count = rowLayout.getChildCount() - 2;
			for (int j = 1; j <= count; j++) {
				ViewGroup wrapLayout = (ViewGroup) rowLayout.getChildAt(j);
				View field = wrapLayout.getChildAt(1);
				int id = (Integer) field.getTag();
				ViewNode node = nodes.get(id);
				node.right = 0;
				node.left = 0;
				node.top = 0;
				node.bottom = 0;

				ViewGroup wrapLayoutChild;
				int idChild;
				// nalezení potomka (napravo)
				if (j + 1 <= count) {
					node.right = newId + 1;
				}
				// nalezení potomka (o řádek níže)
				else if (i + i < formLayout.getChildCount()) {
					node.bottom = newId + 1;
				}
				newNodes.put(newId, node);
				newId++;
			}
		}
		OdmlBuilder.createODML(newNodes, layout, daoFactory);
	}

	private ViewNode getViewNode(int key) {
		return nodes.get(key);
	}

	private LinearLayout getWrapButton() {
		Drawable editShape = ctx.getResources().getDrawable(R.drawable.shape);
		LinearLayout layout = new LinearLayout(ctx);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 10));
		layout.setBackgroundDrawable(editShape);
		Button button = new Button(ctx);
		button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		button.setOnDragListener(new LayoutDragListener(ctx, nodes));
		button.setText("+");
		button.setTag(-1);
		layout.addView(button);
		layout.setVisibility(View.GONE);
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
					Field field = daoFactory.getFieldDAO().getField(nodes.valueAt(i).name, form.getType());
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
			Field field = daoFactory.getFieldDAO().getField(nodes.valueAt(i).name, form.getType());
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
			ViewNode topParent = nodes.get(child.top);
			ViewNode leftParent = nodes.get(child.left);
			if (topParent != null) {
				topParent.bottom = childID;
			} else if (leftParent != null) {
				leftParent.right = childID;
			}
		}
	}

	public SparseArray<ViewNode> getNodes() {
		return nodes;
	}

	public ArrayList<LinearLayout> getNewColumn() {
		return newColumn;
	}
}
