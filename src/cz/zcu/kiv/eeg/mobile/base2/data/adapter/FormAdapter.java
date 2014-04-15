package cz.zcu.kiv.eeg.mobile.base2.data.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormRow;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FormAdapter extends ArrayAdapter<FormRow> {

	private final Context context;
	private final int layoutResourceId; // Todo prejmenovat na rowLayout jako spinnerAdapte
	private List<FormRow> formList;

	/**
	 * Adapter constructor.
	 * 
	 * @param context context
	 * @param resourceId row layout identifier
	 * @param items scenario data collection
	 */
	public FormAdapter(Context context, int resourceId, List<FormRow> items) {
		super(context, resourceId);
		this.layoutResourceId = resourceId;
		this.context = context;
		formList = new ArrayList<FormRow>(items.size());
	}

	/**
	 * Add scenario into adapter.
	 * 
	 * @param object scenario object
	 */
	public void add(FormRow object) {
		formList.add(object);
		notifyDataSetChanged();
	}

	@Override
	public void clear() {
		formList.clear();
		notifyDataSetChanged();
	}

	@Override
	public FormRow getItem(int position) {
		return formList.get(position);
	}

	@Override
	public int getCount() {
		return formList.size();
	}

	public List<FormRow> getList() {
		return formList;
	}

	@Override
	public boolean isEmpty() {
		return formList.isEmpty();
	}

	/**
	 * Getter of row view.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return initView(position, convertView, parent);
	}

	/**
	 * Getter of row view in drop down element
	 */
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return initView(position, convertView, parent);
	}

	/**
	 * Getter of row view.
	 * 
	 * @param position position in adapter
	 * @param convertView view in which row should be displayed
	 * @param parent parent view
	 * @return row view
	 */
	private View initView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
		}
		FormRow record = getItem(position);
		if (record != null) {
			TextView id = (TextView) row.findViewById(R.id.rowId);
			TextView name = (TextView) row.findViewById(R.id.rowName);
			TextView description = (TextView) row.findViewById(R.id.rowDescription);
			TextView mine = (TextView) row.findViewById(R.id.rowMime);

			if (id != null) {
				id.setText(Integer.toString(record.getId()));
			}
			if (name != null) {
				name.setText(record.getName());
			}
			if (description != null) {
				description.setText(record.getDescription());
			}
			/*
			 * if (mine != null) { mine.setText(record.getMine()); }
			 */
		}
		return row;
	}

	/**
	 * Notifies GUI that adapter content changed. Forces GUI to redraw.
	 */
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
