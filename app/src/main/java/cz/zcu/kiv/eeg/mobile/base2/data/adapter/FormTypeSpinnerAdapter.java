package cz.zcu.kiv.eeg.mobile.base2.data.adapter;

import java.util.ArrayList;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FormTypeSpinnerAdapter extends ArrayAdapter<Form> {

	int rowLayout;

	public FormTypeSpinnerAdapter(Context context, int rowLayout, ArrayList<Form> arrayList) {
		super(context, rowLayout, arrayList);
		this.rowLayout = rowLayout;
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
		text.setText(getItem(position).getType());

		return row;
	}
}