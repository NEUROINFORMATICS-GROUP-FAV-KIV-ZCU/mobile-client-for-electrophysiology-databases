package cz.zcu.kiv.eeg.mobile.base2.data.model;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewNode {
	private View node;
	private String data;
	public TextView label;
	public LinearLayout layout; // obaluje label a node

	public String name;

	public int top;
	public int bottom;
	public int left;
	public int right;

	public ViewNode(View node, String name, int top, int bottom, int left, int right) {
		super();
		this.node = node;
		node.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		this.name = name;
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}

	public ViewNode(View node, LinearLayout layout, TextView label, String name, int top, int bottom, int left, int right) {
		super();
		this.node = node;
		node.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		this.layout = layout;

		this.label = label;
		this.name = name;
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}

	public String getData() {
		if (node instanceof TextView) {
			return ((TextView) node).getText().toString();
		}
		if (node instanceof LinearLayout) {

		}
		/*
		 * else if (view instanceof TextView) { TextView textView = (TextView)
		 * view; // do what you want with textView }
		 */

		return null;
	}

	public void setData(String data) {
		if (node instanceof TextView) {
			((TextView) node).setText(data);
		}
		if (node instanceof LinearLayout) {

		}
	}

	// pokud existuje label, tak to obalím layoutem
	public View getWrapNode() {
		if (layout == null) {
			return node;
		} else {
			layout.addView(label);
			layout.addView(node);
			return layout;
		}
	}

	public View getNode() {
		return node;
	}

}