package cz.zcu.kiv.eeg.mobile.base2.data.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormRow;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutRow;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class LayoutDialogAdapter extends ArrayAdapter<LayoutRow> {

	private final Context context;
	private final int rowLayout;
	private List<LayoutRow> layoutList;

	/**
	 * Adapter constructor.
	 * 
	 * @param context context
	 * @param resourceId row layout identifier
	 * @param items scenario data collection
	 */
	public LayoutDialogAdapter(Context context, int rowLayout, List<LayoutRow> items) {
		super(context, rowLayout);
		this.rowLayout = rowLayout;
		this.context = context;
		this.layoutList = new ArrayList<LayoutRow>(items.size());
	}

	/**
	 * Add scenario into adapter.
	 * 
	 * @param object scenario object
	 */
	public void add(LayoutRow object) {
		layoutList.add(object);
		notifyDataSetChanged();
	}

	@Override
	public void clear() {
		layoutList.clear();
		notifyDataSetChanged();
	}

	@Override
	public LayoutRow getItem(int position) {
		return layoutList.get(position);
	}

	@Override
	public int getCount() {
		return layoutList.size();
	}

	public List<LayoutRow> getList() {
		return layoutList;
	}

	@Override
	public boolean isEmpty() {
		return layoutList.isEmpty();
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

	static class ViewHolder {
		protected TextView layoutName;
		protected CheckBox checkbox;
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
		//View row = convertView;
		ViewHolder viewHolder = null;

		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(rowLayout, null);

			viewHolder = new ViewHolder();
			viewHolder.layoutName = (TextView) convertView.findViewById(R.id.layoutName);
			viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.pokuss);
			viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					int getPosition = (Integer) buttonView.getTag(); // Here we get the position that we have set for
																		// the checkbox using setTag.
					layoutList.get(getPosition).setChecked(buttonView.isChecked()); // Set the value of checkbox to
																					// maintain its state.
				}
			});
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.layoutName, viewHolder.layoutName);
			convertView.setTag(R.id.pokuss, viewHolder.checkbox);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.checkbox.setTag(position); // This line is important.
		viewHolder.layoutName.setText(layoutList.get(position).getName());
		viewHolder.checkbox.setChecked(layoutList.get(position).isChecked());
		return convertView;
	}

	/**
	 * Notifies GUI that adapter content changed. Forces GUI to redraw.
	 */
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
