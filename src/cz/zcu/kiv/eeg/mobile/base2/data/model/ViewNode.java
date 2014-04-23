package cz.zcu.kiv.eeg.mobile.base2.data.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.SpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.editor.LayoutDragListener;
import cz.zcu.kiv.eeg.mobile.base2.data.editor.LayoutTouchListener;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class ViewNode {
	private View node;
	private TextView label;
	private LinearLayout layout; // obaluje label a node

	public String name;
	public int top;
	public int bottom;
	public int left;
	public int right;
	public int weight;

	private Drawable background;

	public ViewNode(View node, String name, int top, int bottom, int left, int right, int weight) {
		super();
		this.node = node;
		this.name = name;
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		this.weight = weight;
	}

	public ViewNode(int id, View node, LinearLayout layout, TextView label, String name, int top, int bottom, int left,
			int right, int weight) {
		super();

		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, weight));
		layout.setPadding(10, 10, 0, 0);
		layout.addView(label);
		layout.addView(node);
		layout.setTag(id);

		this.node = node;
		node.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

		this.layout = layout;
		this.label = label;
		this.name = name;
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		this.weight = weight;
	}

	public String getData() {
		if (node instanceof TextView) {
			return ((TextView) node).getText().toString();
		} else if ((node instanceof Spinner)) {
			return (String) ((Spinner) node).getSelectedItem();
		} else if (node instanceof LinearLayout) {

		}
		/*
		 * else if (view instanceof TextView) { TextView textView = (TextView) view; // do what you want with textView }
		 */

		return null;
	}

	public void setData(String data) {
		if (node instanceof TextView) {
			((TextView) node).setText(data);
		} else if ((node instanceof Spinner)) {
			// zobrazení vybraných dat u spinneru
			Spinner spinner = ((Spinner) node);
			SpinnerAdapter adapter = (SpinnerAdapter) spinner.getAdapter();
			for (int i = 0; i < adapter.getCount(); i++) {
				if (data.equalsIgnoreCase(adapter.getItem(i))) {
					((Spinner) node).setSelection(i);
				}
			}
		}
		if (node instanceof LinearLayout) {
		}
	}

	public void setDiffWeight(int weight) {
		this.weight -= weight;
		layout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, this.weight));
	}

	public View getWrapNode() {
		return layout;
	}

	// drag and drop pro vyplňované pole
	public void setEditor(Context ctx, SparseArray<ViewNode> nodes) {
		background = layout.getBackground();
		Drawable editShape = ctx.getResources().getDrawable(R.drawable.shape);

		node.setOnTouchListener(new LayoutTouchListener());
		node.setOnDragListener(new LayoutDragListener(ctx, nodes));
		label.setOnTouchListener(new LayoutTouchListener());
		label.setOnDragListener(new LayoutDragListener(ctx, nodes));

		layout.setBackgroundDrawable(editShape);
	}

	public void removeEditor() {
		node.setOnTouchListener(null);
		node.setOnDragListener(null);
		label.setOnTouchListener(null);
		label.setOnDragListener(null);
		layout.setBackgroundDrawable(background);
	}

	public View getNode() {
		return node;
	}

	public LinearLayout getLayout() {
		return layout;
	}

}