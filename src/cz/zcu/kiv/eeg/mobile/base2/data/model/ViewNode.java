package cz.zcu.kiv.eeg.mobile.base2.data.model;

import android.view.View;

public class ViewNode {
	public View node;
	public String name;
	public int top;
	public int bottom;
	public int left;
	public int right;
	
	public ViewNode(View node, String name, int top, int bottom, int left, int right) {
		super();
		this.node = node;
		this.name = name;
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}	
}
