package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.ViewBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.editor.LayoutDragListener;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormRow;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.data.model.ViewNode;
import cz.zcu.kiv.eeg.mobile.base2.ui.field.FieldAddActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.main.DashboardActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.settings.LoginActivity;
import android.view.View.OnClickListener;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FormDetailsFragment extends Fragment {

	public final static String TAG = FormDetailsFragment.class.getSimpleName();
	public final static String DATASET_ID = "datasetID";
	private DAOFactory daoFactory;
	private MenuItems menu;
	private Layout layout;
	private ViewBuilder vb;
	private int datasetID = -1;
	private boolean isEnabledEdit = false;
	private boolean isEnabledMove = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		daoFactory = new DAOFactory(getActivity());
		Bundle extras = getActivity().getIntent().getExtras();
		int menuItemID = extras.getInt(DashboardActivity.MENU_ITEM_ID, -1);
		datasetID = extras.getInt(DATASET_ID, -1);
		menu = daoFactory.getMenuItemDAO().getMenu(menuItemID);
		layout = daoFactory.getLayoutDAO().getLayoutByName(menu.getLayout().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		vb = new ViewBuilder(getActivity(), layout, daoFactory);	
		View view = vb.getLinearLayout();	
		vb.initData(datasetID);
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		int newDataset = vb.saveOrUpdateData(datasetID);
		SparseArray<FormAdapter> adapters = ListAllFormsFragment.getAdaptersForUpdate();

		for (int i = 0; i < adapters.size(); i++) {

			FormAdapter adapter = adapters.valueAt(i);
			MenuItems aMenu = adapter.getMenu();

			// musím projít všechny adaptéry odpovídajícího typu, abych jim aktualizoval řádek v listview
			if (adapter != null && aMenu.getRootForm().getType().equalsIgnoreCase(menu.getRootForm().getType())) {
				String id = "id";
				Field description1Field = daoFactory.getFieldDAO().getField(aMenu.getFieldDescription1().getId());
				Field description2Field = daoFactory.getFieldDAO().getField(aMenu.getFieldDescription2().getId());

				String descriptionData1 = null;
				String descriptionData2 = null;

				if (description1Field != null) {
					Data description1 = daoFactory.getDataDAO().getDataByDatasetAndField(newDataset,
							description1Field.getId());
					if (description1 != null) {
						descriptionData1 = description1.getData();
					}
				}
				if (description2Field != null) {
					Data description2 = daoFactory.getDataDAO().getDataByDatasetAndField(newDataset,
							description2Field.getId());
					if (description2 != null) {
						descriptionData2 = description2.getData();
					}
				}

				if (datasetID == -1) {
					adapter.add(new FormRow(newDataset, descriptionData1, descriptionData2, "Já"));
					adapter.notifyDataSetChanged();
				} else {
					// aktualizace již existujícího řádku v listView
					List<FormRow> rowList = adapter.getList();
					for (int j = 0; j < rowList.size(); j++) {
						FormRow row = rowList.get(j);
						if (row.getId() == datasetID) {
							row.setName(descriptionData1);
							row.setDescription(descriptionData2);
							rowList.set(j, row);
							adapter.notifyDataSetChanged();
						}
					}
				}
			}
		}
	}

	private void editLayout() {

		vb.enableLocalEdit(getActivity());
		isEnabledEdit = true;
		getActivity().invalidateOptionsMenu();
	}

	private void saveLayout() {
		vb.saveLayout();
		isEnabledEdit = false;
		if (isEnabledMove) {
			vb.disableEditor();
			isEnabledMove = false;
		}
		getActivity().invalidateOptionsMenu();
	}

	private void addField() {
		Intent intent = new Intent(getActivity(), FieldAddActivity.class);
		intent.putExtra(Form.FORM_TYPE, layout.getRootForm().getType());
		intent.putExtra(Layout.LAYOUT_NAME, layout.getName());
		intent.putExtra(Values.USED_FIELD, vb.getUsedFields());
		startActivityForResult(intent, Values.PICK_FIELD_ID_REQUEST);
	}

	private void moveFields() {
		if (isEnabledMove) {
			vb.disableEditor();	
			editLayout();
			isEnabledMove = false;
		} else {
			vb.enableEditor();
			isEnabledMove = true;
		}
		getActivity().invalidateOptionsMenu();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == Values.PICK_FIELD_ID_REQUEST) {
				vb.addFieldToForm(data.getIntExtra(Field.FIELD_ID, 0), isEnabledMove, getActivity());
			}
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem editMenu = menu.findItem(R.id.form_edit_layout);
		MenuItem saveMenu = menu.findItem(R.id.form_save_layout);
		MenuItem move = menu.findItem(R.id.form_move);
		MenuItem add = menu.findItem(R.id.form_add);

		if (isEnabledEdit) {
			editMenu.setVisible(false);
			saveMenu.setVisible(true);
			add.setVisible(true);
			move.setVisible(true);
			if (isEnabledMove) {
				move.setIcon(R.drawable.ic_action_move_off);
			} else {
				move.setIcon(R.drawable.ic_action_move_on);
			}
		} else {
			editMenu.setVisible(true);
			saveMenu.setVisible(false);
			move.setVisible(false);
			add.setVisible(false);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			return false;
		case R.id.form_edit_layout:
			editLayout();
			return true;
		case R.id.form_save_layout:
			saveLayout();
			return true;
		case R.id.form_add:
			addField();
			return true;
		case R.id.form_move:
			moveFields();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		daoFactory.releaseHelper();
	}
}
