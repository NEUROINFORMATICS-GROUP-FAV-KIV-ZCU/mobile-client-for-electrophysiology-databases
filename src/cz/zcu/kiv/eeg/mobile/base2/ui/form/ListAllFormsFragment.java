package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FieldSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormRow;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;
import cz.zcu.kiv.eeg.mobile.base2.ws.TaskFragment;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class ListAllFormsFragment extends ListFragment {

	public final static String TAG = ListAllFormsFragment.class.getSimpleName();
	private static SparseArray<HashMap<String, FormAdapter>> workspaceAdapters = new SparseArray<HashMap<String, FormAdapter>>();
	private FormActivityCallBack formActivity;
	private DAOFactory daoFactory;
	private MenuItems menu;
	private Layout layout;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);		
		try {
			formActivity = (FormActivityCallBack) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true); // povolí action bar
		daoFactory = new DAOFactory(getActivity());	
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.form_list, container, false);
		View emptyView = view.findViewById(android.R.id.empty);
		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((FormActivity) getActivity()).newData();
			}
		});
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		menu = ((FormActivity) getActivity()).getMenuData();
		layout = ((FormActivity) getActivity()).getLayout();
		setListAdapter(null);
		setListAdapter(getStaticAdapter(getActivity(), menu.getParentId(), layout, daoFactory)); // TODO upravit
		registerForContextMenu(getListView());
	}
	
	public static FormAdapter getStaticAdapter(Activity activity, MenuItems workspace, Layout layout, DAOFactory daoFactory) {
		return getStaticAdapter(activity, workspace, layout, null, daoFactory);
	}
	
	public static FormAdapter getStaticAdapter(Activity activity, MenuItems workspace, Layout layout, LayoutProperty property, DAOFactory daoFactory) {
		HashMap<String, FormAdapter> layoutAdapters = workspaceAdapters.get(workspace.getId());   // nalezení správného workspacu
		if(layoutAdapters == null){
			layoutAdapters = new HashMap<String, FormAdapter>();
			workspaceAdapters.put(workspace.getId(), layoutAdapters);
		}
		FormAdapter adapter = layoutAdapters.get(layout.getName());  // nalezení FormAdapter pro daný formulář		

		if (adapter == null) {
			adapter = new FormAdapter(activity, R.layout.form_row, daoFactory, layout.getRootForm(), new ArrayList<FormRow>());

			Form form = daoFactory.getFormDAO().getForm(layout.getRootForm().getType());
			Field previewMajor = null;
			Field PreviewMinor = null;
			
			//previewMinor/Major u podformuláře se určuje z PropertyLayout, jinak z Layout
			if(property == null){
				if (layout.getPreviewMajor() != null) {
					previewMajor = daoFactory.getFieldDAO().getField(layout.getPreviewMajor().getId());			
				}
				if (layout.getPreviewMinor() != null) {
					PreviewMinor = daoFactory.getFieldDAO().getField(layout.getPreviewMinor().getId());
				}
			}else{
				if (property.getPreviewMajor() != null) {
					previewMajor = daoFactory.getFieldDAO().getField(property.getPreviewMajor().getId());			
				}
				if (property.getPreviewMinor() != null) {
					PreviewMinor = daoFactory.getFieldDAO().getField(property.getPreviewMinor().getId());
				}
			}			
			if (form != null) {
				for (Dataset dataset : daoFactory.getDataSetDAO().getDataSet(form, workspace)) {
					int dataset_id = dataset.getId();
					String descriptionData1 = null;
					String descriptionData2 = null;

					if (previewMajor != null) {
						Data description1 = daoFactory.getDataDAO().getData(dataset_id, previewMajor.getId());
						if (description1 != null) {
							descriptionData1 = description1.getData();
						}
					}
					if (PreviewMinor != null) {
						Data description2 = daoFactory.getDataDAO().getData(dataset_id, PreviewMinor.getId());
						if (description2 != null) {
							descriptionData2 = description2.getData();
						}
					}
					adapter.add(new FormRow(dataset.getRecordId(), descriptionData1, descriptionData2, "Já"));
				}
			}
			layoutAdapters.put(layout.getName(), adapter);
		}

		return adapter;
	}
	
	public static void removeAdapter(MenuItems workspace, Layout layout){
		HashMap<String, FormAdapter> layoutAdapters = workspaceAdapters.get(workspace.getId());   // nalezení správného workspacu
		if(layoutAdapters != null){
			layoutAdapters.put(layout.getName(), null);
		}	
	}

	public static HashMap<String, FormAdapter> getAdaptersForUpdate(int workspaceID) {
		return workspaceAdapters.get(workspaceID);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		showDetails(position);
	}

	private void showDetails(int index) {		
		FormAdapter dataAdapter = getStaticAdapter(getActivity(), menu.getParentId(), layout, daoFactory);
		boolean empty = dataAdapter == null || dataAdapter.isEmpty();
		if (!empty) {
			FormRow row = dataAdapter.getItem(index);
			Intent intent = new Intent();
			intent.setClass(getActivity(), FormDetailsActivity.class);
			intent.putExtra(Dataset.DATASET_RECORD_ID, row.getId());
			intent.putExtra(Values.MENU_ITEM_ID, menu.getId());		
			intent.putExtra(Layout.LAYOUT_ID, layout.getName());		
			startActivity(intent);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.form_preview_menu, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		int position = info.position;
		FormAdapter dataAdapter = getStaticAdapter(getActivity(), menu.getParentId(), layout, daoFactory);
		boolean empty = dataAdapter == null || dataAdapter.isEmpty();

		/*
		 * if (!empty) { FormRow row = dataAdapter.getItem(index); Intent intent = new Intent();
		 * intent.setClass(getActivity(), FormDetailsActivity.class); intent.putExtra(Dataset.DATASET_ID, row.getId());
		 * intent.putExtra(Values.MENU_ITEM_ID, menu.getId()); intent.putExtra(Values.MENU_ITEM_NAME, menu.getName());
		 * startActivity(intent); }
		 */

		switch (item.getItemId()) {
		case R.id.menu_edit_preview:
			if (!empty) {
				createPreviewDialog();
			}
			return true;
		case R.id.menu_delete:
			if (!empty) {
				FormRow row = dataAdapter.getItem(position);
				dataAdapter.remove(row);
			}
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	public void createPreviewDialog() {
		final Context ctx = getActivity();
		final Dialog dialog = new Dialog(ctx);
		dialog.setContentView(R.layout.form_preview_fields);
		dialog.setTitle(getString(R.string.form_preview_fields));
		
		refreshLayout();
	
		ArrayList<Field> fields = (ArrayList<Field>) daoFactory.getFieldDAO().getFieldsTextbox(
				layout.getRootForm().getType());

		final Spinner previewMajor = (Spinner) dialog.findViewById(R.id.form_spinnerPreviewMajor);
		final Spinner previewMinor = (Spinner) dialog.findViewById(R.id.form_spinnerPreviewMinor);

		FieldSpinnerAdapter previewAdapter = new FieldSpinnerAdapter(ctx, R.layout.spinner_row_simple,
				(ArrayList<Field>) fields);
		previewMajor.setAdapter(previewAdapter);

		FieldSpinnerAdapter previewAdapter2 = new FieldSpinnerAdapter(ctx, R.layout.spinner_row_simple,
				(ArrayList<Field>) fields);
		previewMinor.setAdapter(previewAdapter2);
		
		int position;
		if (layout.getPreviewMajor() != null) {
			position = previewAdapter.getPosition(layout.getPreviewMajor());
			previewMajor.setSelection(position);
		}
		if (layout.getPreviewMinor() != null) {
			position = previewAdapter2.getPosition(layout.getPreviewMinor());
			previewMinor.setSelection(position);
		}

		Button createForm = (Button) dialog.findViewById(R.id.form_change_preview);
		createForm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Field prevMajor = (Field) previewMajor.getSelectedItem();
				Field prevMinor = (Field) previewMinor.getSelectedItem();
				layout.setPreviewMajor(prevMajor);
				layout.setPreviewMinor(prevMinor);
				layout.setState(Values.ACTION_EDIT);

				daoFactory.getLayoutDAO().saveOrUpdate(layout);
				setListAdapter(null);
				
				//aktualizace - odstranění původního adaptéru a vytvoření nového
				removeAdapter(menu.getParentId(), layout);				
				setListAdapter(getStaticAdapter(getActivity(), menu.getParentId(), layout, daoFactory));			
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	private void refreshLayout(){
		layout = daoFactory.getLayoutDAO().getLayout(layout.getName());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.form_refresh:
			
			MenuItems workspace = daoFactory.getMenuItemDAO().getMenu(menu.getParentId().getId());
			User user = daoFactory.getUserDAO().getUser(workspace.getCredential().getId());
			workspace.setCredential(user);
			FormAdapter adapter = getStaticAdapter(getActivity(), menu.getParentId(), layout, daoFactory);
			formActivity.getTaskFragment().startData(workspace, layout, adapter, daoFactory);								
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

	// todo pouze kvuli not implemented
	public void showAlert(final String alert, final boolean closeActivity) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(alert).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		daoFactory.releaseHelper();
	}
}
