package cz.zcu.kiv.eeg.mobile.base2.data.adapter;

import java.util.ArrayList;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class DrawerAdapter extends ArrayAdapter<MenuItems> {

	int rowLayout;

	public DrawerAdapter(Context context, int dashboard_drawer_item, ArrayList<MenuItems> items) {
		super(context, dashboard_drawer_item, items);
		this.rowLayout = dashboard_drawer_item;
	}

	/**
	 * Getter of row view in drop down element (spinner like).
	 * 
	 * @param position position in adapter
	 * @param convertView view in which row should be displayed
	 * @param parent parent view
	 * @return row view
	 */
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// todo brejknout
		return getView(position, convertView, parent);
	}

	/**
	 * Getter of row view.
	 * 
	 * @param position position in adapter
	 * @param convertView view in which row should be displayed
	 * @param parent parent view
	 * @return row view
	 */

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout todoView;
		MenuItems item = getItem(position);

		if (convertView == null) {
			todoView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflater);
			li.inflate(rowLayout, todoView, true); // projít true
		} else {
			todoView = (LinearLayout) convertView;
		}

		ImageView iconView = (ImageView) todoView.findViewById(R.id.menu_icon);
		TextView label = (TextView) todoView.findViewById(R.id.menu_label);

		if (item.getIcon() != null) {
			iconView.setImageResource(todoView.getResources().getIdentifier(item.getIcon(), "drawable",
					getContext().getPackageName()));
		}

		label.setText(getItem(position).getName());
		return todoView;
	}
}