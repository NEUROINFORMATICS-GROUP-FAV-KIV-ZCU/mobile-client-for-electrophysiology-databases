package cz.zcu.kiv.eeg.mobile.base2.data.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FieldSpinnerAdapter extends ArrayAdapter<Field> {

	int rowLayout;
	ArrayList<Field> items;

	public FieldSpinnerAdapter(Context context, int rowLayout, ArrayList<Field> items) {
		super(context, rowLayout, items);
		this.rowLayout = rowLayout;
		this.items = items;
	}
	
	@Override
	public int getPosition(Field item) {
		for(Field field : items){
			if(field.getId() == item.getId()){
				return super.getPosition(field);
			}
		}
		return -1;
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
	public View getView(int position, View row, ViewGroup parent) {
		if (row == null) {
			LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
			row = inflater.inflate(rowLayout, parent, false);
		}

		TextView text = (TextView) row;
		text.setText(getItem(position).getName());

		return row;
	}
}