package cz.zcu.kiv.eeg.mobile.base2.data.editor;

import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.ViewBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormDetailsFragment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


/*
 * odstranit
 * 
 */
public class SubformActionMode implements ActionMode.Callback {

	private ViewBuilder vb;
	private LinearLayout wrapLayout;
	private Spinner spinner;
	private MenuItems menuItem;
	private Field field;
	private FormDetailsFragment fragment;
	private int datasetId;

	public SubformActionMode(FormDetailsFragment fragment, ViewBuilder vb, LinearLayout wrapLayout, Spinner spinner, Field field, MenuItems menuItem, int datasetId) {
		super();
		this.wrapLayout = wrapLayout;
		this.spinner = spinner;
		this.vb = vb;
		this.menuItem = menuItem;
		this.field = field;
		this.fragment = fragment;
		this.datasetId = datasetId;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.form_local_edit_menu, menu);

		/*MenuItem removeLabel = menu.findItem(R.id.form_remove_label);
		View viewRemove = removeLabel.getActionView();
		if (viewRemove instanceof TextView) {
			((TextView) viewRemove).setText(R.string.remove);
		}*/
		
		/*MenuItem editLabel = menu.findItem(R.id.form_edit_label);
		View viewEdit = editLabel.getActionView();
		if (viewEdit instanceof TextView) {
			((TextView) viewEdit).setText(R.string.edit);
		}*/
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.form_remove_field:
			wrapLayout.removeView(spinner);
			mode.finish(); // Action picked, so close the CAB
			return true;
			
		case R.id.form_edit_field:
			//fragment.showSubform(Values.FORM_EDIT_DATA, datasetId, menuItem, field.getId());
			//fragment.showSubform(Values.FORM_NEW_SUBFORM, 0, menuItem, field.getId());	
			
			mode.finish(); // Action picked, so close the CAB
			return true;
		default:
			return false;
		}		
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		vb.onDestroyActionMode();
		// mode = null;
		// TODO Auto-generated method stub
	}

}
