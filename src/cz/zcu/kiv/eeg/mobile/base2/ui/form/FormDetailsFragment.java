package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.ViewBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.dao.FormLayoutsDAO;
import cz.zcu.kiv.eeg.mobile.base2.data.editor.LayoutDragListener;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormRow;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.ui.field.FieldAddActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.field.FieldEditorAddActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.main.DashboardActivity;
import android.view.View.OnClickListener;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FormDetailsFragment extends Fragment {

	public final static String TAG = FormDetailsFragment.class.getSimpleName();
	private DAOFactory daoFactory;
	private MenuItems menu;
	private Layout layout;

	private ViewBuilder vb;
	private int datasetRecordId;
	private int datasetParentId;
	private int subformField;
	private int formMode = 0;
	private boolean isEnabledMove = false;

	private LinearLayout linearLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		daoFactory = new DAOFactory(getActivity());
		Bundle extras = getActivity().getIntent().getExtras();
		int menuItemID = extras.getInt(Values.MENU_ITEM_ID, -1);
		String layoutName = extras.getString(Layout.LAYOUT_ID);
		formMode = extras.getInt(Form.FORM_MODE, Values.FORM_EDIT_DATA);
		datasetRecordId = extras.getInt(Dataset.DATASET_RECORD_ID);
		datasetParentId = extras.getInt(Dataset.DATASET_PARENT_ID);
		subformField = extras.getInt(Field.FIELD_ID);

		menu = daoFactory.getMenuItemDAO().getMenu(menuItemID);
		layout = daoFactory.getLayoutDAO().getLayout(layoutName);	
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (formMode == Values.FORM_NEW_SUBFORM) {
			vb = new ViewBuilder(this, layout, datasetRecordId, Values.FORM_NEW_DATA, menu, daoFactory);
		} else {				
		vb = new ViewBuilder(this, layout, datasetRecordId, formMode, menu, daoFactory);
		}
		linearLayout = vb.getLinearLayout();
		View view = linearLayout;

		if (formMode == Values.FORM_EDIT_LAYOUT) {
			vb.enableLocalEdit(getActivity());
		}

		return view;
	}

	// aktualizace formuláře
	@Override
	public void onStart() {
		List<Field> fields = daoFactory.getFieldDAO().getModifyFields(layout.getRootForm(), 0);
		if (fields.size() > 0) {

			for (int i = 0; i < fields.size(); i++) {
				Field tmp = fields.get(i);
				if (tmp.getAction() == Values.ACTION_EDIT) {
					linearLayout.removeAllViews();
					linearLayout.addView(vb.getLinearLayout());
					daoFactory.getFieldDAO().update(tmp);
					vb.enableLocalEdit(getActivity());

				} else if (tmp.getAction() == Values.ACTION_ADD) {
					vb.addFieldToForm(tmp.getId(), isEnabledMove, getActivity());
				} else if (tmp.getAction() == Values.ACTION_REMOVE) {
					vb.removeFieldFromDB(tmp.getId());
				}
				tmp.setAction(0);
				daoFactory.getFieldDAO().update(tmp);
			}
		}
		super.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void editLayout() {
		vb.enableLocalEdit(getActivity());
	}

	private void saveLayout() {
		vb.saveLayout();
	}

	private void addField() {
		Intent intent = new Intent(getActivity(), FieldEditorAddActivity.class);
		intent.putExtra(Form.FORM_TYPE, layout.getRootForm().getType());
		intent.putExtra(Layout.LAYOUT_NAME, layout.getName());
		intent.putExtra(Values.USED_FIELD, vb.getUsedFields());
		intent.putExtra(Values.MENU_ITEM_ID, menu.getId());
		startActivityForResult(intent, Values.PICK_FIELD_ID_REQUEST);
	}

	public void editField(int id) {
		Intent intent = new Intent(getActivity(), FieldAddActivity.class);
		intent.putExtra(Form.FORM_TYPE, layout.getRootForm().getType());
		intent.putExtra(Field.FIELD_ID, id);
		intent.putExtra(Layout.LAYOUT_NAME, layout.getName());
		intent.putExtra(Values.USED_FIELD, vb.getUsedFields());
		intent.putExtra(Values.MENU_ITEM_ID, menu.getId());
		startActivity(intent);
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
				// vb.addFieldToForm(data.getIntegerArrayListExtra(Field.FIELD_ID), isEnabledMove, getActivity());
			} else if (requestCode == Values.PICK_SUBFORM_ID) {
				Intent intent = getActivity().getIntent();
				getActivity().finish();
				startActivity(intent);
			} else if (requestCode == Values.FORM_EDIT_SUBFORM || requestCode == Values.FORM_NEW_SUBFORM) {
				vb.refreshSubforms();
			}
		}
	}

	public int saveData() {
		int newDataset = vb.saveFormData(datasetRecordId);
		if (newDataset == -1) {
			return -1;
		}

		HashMap<String, FormAdapter> adapters = ListAllFormsFragment.getAdaptersForUpdate(menu.getParentId().getId());

		for (Map.Entry<String, FormAdapter> entry : adapters.entrySet()) {

			FormAdapter adapter = entry.getValue();
			Form aForm = adapter.getForm();		

			// musím projít všechny adaptéry odpovídajícího typu, abych jim aktualizoval řádek v listview
			if (adapter != null && aForm.getType().equalsIgnoreCase(layout.getRootForm().getType())) {
				String id = "id";
				Field description1Field = null;
				Field description2Field = null;

				if (layout.getPreviewMajor() != null) {
					description1Field = daoFactory.getFieldDAO().getField(layout.getPreviewMajor().getId());
				}
				if (layout.getPreviewMinor() != null) {
					description2Field = daoFactory.getFieldDAO().getField(layout.getPreviewMinor().getId());
				}

				String descriptionData1 = null;
				String descriptionData2 = null;

				if (description1Field != null) {
					Data description1 = daoFactory.getDataDAO().getData(newDataset, description1Field.getId());
					if (description1 != null) {
						descriptionData1 = description1.getData();
					}
				}
				if (description2Field != null) {
					Data description2 = daoFactory.getDataDAO().getData(newDataset, description2Field.getId());
					if (description2 != null) {
						descriptionData2 = description2.getData();
					}
				}
				if (formMode == Values.FORM_NEW_DATA || formMode == Values.FORM_NEW_SUBFORM) {
					adapter.add(new FormRow(newDataset, descriptionData1, descriptionData2, "Já"));
					adapter.notifyDataSetChanged();
				} else {
					// aktualizace již existujícího řádku v listView
					List<FormRow> rowList = adapter.getList();
					for (int j = 0; j < rowList.size(); j++) {
						FormRow row = rowList.get(j);
						if (row.getId() == datasetRecordId) {
							row.setName(descriptionData1);
							row.setDescription(descriptionData2);
							rowList.set(j, row);
							adapter.notifyDataSetChanged();
						}
					}
				}
			}
		}
		return newDataset;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem move = menu.findItem(R.id.form_move_field);

		if (formMode == Values.FORM_EDIT_LAYOUT) {
			if (isEnabledMove) {
				move.setIcon(R.drawable.ic_action_move_off);
			} else {
				move.setIcon(R.drawable.ic_action_move_on);
			}
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
		case R.id.form_save_data:
			int datasetIdResult = saveData();
			if (datasetIdResult == -1) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(vb.error).setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								vb.error = "";
								dialog.cancel();

							}
						});
				builder.create().show();

			} else {
				
				if (formMode == Values.FORM_EDIT_SUBFORM) {
					datasetRecordId = datasetIdResult;
					getActivity().setResult(Activity.RESULT_OK);
				}else if(formMode == Values.FORM_NEW_SUBFORM){
					datasetRecordId = datasetIdResult;
					Dataset datasetParent = daoFactory.getDataSetDAO().getDataSet(datasetParentId);
					Field parentField = daoFactory.getFieldDAO().getField(subformField);
					daoFactory.getDataDAO().create(datasetParent, parentField, Integer.toString(datasetRecordId));
					getActivity().setResult(Activity.RESULT_OK);
				}
				getActivity().finish();
			}

			return true;
		case R.id.form_save_layout:
			saveLayout();
			getActivity().finish();
			return true;
		case R.id.form_add_field:
			addField();
			return true;
		case R.id.form_move_field:
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
