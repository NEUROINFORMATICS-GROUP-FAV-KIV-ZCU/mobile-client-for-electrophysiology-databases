package cz.zcu.kiv.eeg.mobile.base2.data.adapter;

import cz.zcu.kiv.eeg.mobile.base2.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DrawerAdapter extends ArrayAdapter<String> {

	int rowLayout;

	public DrawerAdapter(Context context, int dashboard_drawer_item, String[] itemsLabel) {	
		super(context, dashboard_drawer_item, itemsLabel); 
		this.rowLayout = dashboard_drawer_item;
	}

	 /**
     * Getter of row view in drop down element (spinner like).
     *
     * @param position    position in adapter
     * @param convertView view in which row should be displayed
     * @param parent      parent view
     * @return row view
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
    	//todo brejknout
        return getView(position, convertView, parent);
    }
    
    /**
     * Getter of row view.
     *
     * @param position    position in adapter
     * @param convertView view in which row should be displayed
     * @param parent      parent view
     * @return row view
     */ 
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout todoView;
	
		if (convertView == null) {
			todoView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflater);
			li.inflate(rowLayout, todoView, true); //projít true
		} else {
			todoView = (LinearLayout) convertView;
		}		
		
		TextView label = (TextView) todoView.findViewById(R.id.menu_label);
		label.setText(getItem(position));
		return todoView;
	}
}