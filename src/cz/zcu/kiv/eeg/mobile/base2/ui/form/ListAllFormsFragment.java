package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import java.util.ArrayList;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.AdapterContextMenuInfo;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FieldSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormTypeSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.LayoutSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormRow;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutMenuItems;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.ui.main.DashboardActivity;
import cz.zcu.kiv.eeg.mobile.base2.ws.TaskFragment;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class ListAllFormsFragment extends ListFragment {

	public final static String TAG = ListAllFormsFragment.class.getSimpleName();
	// public static FormAdapter adapter;
	// TODO podpora pro adaptéry ze všech formulářů
	private static SparseArray<FormAdapter> adapters = new SparseArray<FormAdapter>();
	private DAOFactory daoFactory;
	private MenuItems menu;
	private TaskFragment mTaskFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true); // povolí action bar
		daoFactory = new DAOFactory(getActivity());

		FragmentManager fm = getFragmentManager();
		mTaskFragment = (TaskFragment) fm.findFragmentByTag("taskFragment");
		if (mTaskFragment == null) {
			mTaskFragment = new TaskFragment();
			fm.beginTransaction().add(mTaskFragment, "taskFragment").commit();
		}
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
		setListAdapter(null);
		setListAdapter(getStaticAdapter(getActivity(), menu, daoFactory));
		registerForContextMenu(getListView());
	}

	public static FormAdapter getStaticAdapter(Activity activity, MenuItems menu, DAOFactory daoFactory) {
		FormAdapter adapter = adapters.get(menu.getId());

		if (adapter == null) {
			adapter = new FormAdapter(activity, R.layout.form_row, menu, daoFactory, new ArrayList<FormRow>());

			// podle čeho hledám
			String id = "id";// menu.getFieldID().getName(); //ID DATASETU

			Field previewMajor = null;
			Field PreviewMinor = null;
			Form form = daoFactory.getFormDAO().getForm(menu.getRootForm().getType());
			if (menu.getPreviewMajor() != null) {
				previewMajor = daoFactory.getFieldDAO().getField(menu.getPreviewMajor().getId());
				//form = daoFactory.getFormDAO().getForm(previewMajor.getForm().getType());
			}
			if (menu.getPreviewMinor() != null) {
				PreviewMinor = daoFactory.getFieldDAO().getField(menu.getPreviewMinor().getId());
			}
			if (form != null) {
				for (Dataset dataset : daoFactory.getDataSetDAO().getDataSet(form)) {
					int dataset_id = dataset.getId();
					String descriptionData1 = null;
					String descriptionData2 = null;

					if (previewMajor != null) {
						Data description1 = daoFactory.getDataDAO().getData(dataset.getId(), previewMajor.getId());
						if (description1 != null) {
							descriptionData1 = description1.getData();
						}
					}
					if (PreviewMinor != null) {
						Data description2 = daoFactory.getDataDAO().getData(dataset.getId(), PreviewMinor.getId());
						if (description2 != null) {
							descriptionData2 = description2.getData();
						}
					}
					adapter.add(new FormRow(dataset_id, descriptionData1, descriptionData2, "Já"));
				}
			}
			adapters.put(menu.getId(), adapter);
		}

		return adapter;
	}

	private void clearAdapter(int menuID) {
		adapters.put(menuID, null);
	}

	public static SparseArray<FormAdapter> getAdaptersForUpdate() {
		return adapters;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		showDetails(position);
		// this.setSelection(position);
	}

	private void showDetails(int index) {
		// FormAdapter dataAdapter = getAdapter(menu.getId());
		FormAdapter dataAdapter = getStaticAdapter(getActivity(), menu, daoFactory);
		boolean empty = dataAdapter == null || dataAdapter.isEmpty();
		if (!empty) {
			FormRow row = dataAdapter.getItem(index);
			Intent intent = new Intent();
			intent.setClass(getActivity(), FormDetailsActivity.class);
			intent.putExtra(Dataset.DATASET_ID, row.getId());
			intent.putExtra(Values.MENU_ITEM_ID, menu.getId());
			intent.putExtra(Values.MENU_ITEM_NAME, menu.getName());
			startActivity(intent);
		}
	}

	/**
	 * If online, fetches available public data.
	 */
	private void update() {

		/*
		 * CommonActivity activity = (CommonActivity) getActivity(); if (ConnectionUtils.isOnline(activity)) { new
		 * FetchScenarios(activity, getAdapter(), Values.SERVICE_QUALIFIER_ALL).execute(); } else
		 * activity.showAlert(activity.getString(R.string.error_offline));
		 */
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
		FormAdapter dataAdapter = getStaticAdapter(getActivity(), menu, daoFactory);
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
				createFormDialog();
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

	public void createFormDialog() {
		final Context ctx = getActivity();
		final Dialog dialog = new Dialog(ctx);
		dialog.setContentView(R.layout.form_preview_fields);
		dialog.setTitle(getString(R.string.form_preview_fields));
		ArrayList<Field> fields = (ArrayList<Field>) daoFactory.getFieldDAO().getFieldsTextbox(menu.getRootForm().getType());

		final Spinner previewMajor = (Spinner) dialog.findViewById(R.id.form_spinnerPreviewMajor);
		final Spinner previewMinor = (Spinner) dialog.findViewById(R.id.form_spinnerPreviewMinor);

		FieldSpinnerAdapter previewAdapter = new FieldSpinnerAdapter(ctx, R.layout.spinner_row_simple,
				(ArrayList<Field>) fields);
		previewMajor.setAdapter(previewAdapter);

		FieldSpinnerAdapter previewAdapter2 = new FieldSpinnerAdapter(ctx, R.layout.spinner_row_simple,
				(ArrayList<Field>) fields);
		previewMinor.setAdapter(previewAdapter2);

		int position;
		if(menu.getPreviewMajor() != null){
			position = previewAdapter.getPosition(menu.getPreviewMajor());
			previewMajor.setSelection(position);
		}
		if(menu.getPreviewMajor() != null){
			position = previewAdapter2.getPosition(menu.getPreviewMinor());
			previewMinor.setSelection(position);
		}	

		Button createForm = (Button) dialog.findViewById(R.id.form_change_preview);
		createForm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {			
				Field prevMajor  = (Field) previewMajor.getSelectedItem();
				Field prevMinor = (Field) previewMinor.getSelectedItem();		
				menu.setPreviewMajor(prevMajor);
				menu.setPreviewMinor(prevMinor);
				
				daoFactory.getMenuItemDAO().saveOrUpdate(menu);
				setListAdapter(null);
				adapters.put(menu.getId(), null);
				setListAdapter(getStaticAdapter(getActivity(), menu, daoFactory));			
				dialog.dismiss();

			}
		});

		dialog.show();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.form_refresh:
			showAlert("Not implemented yet", false);

			/*
			 * clearAdapter(menu.getId()); String url = Values.SERVICE_GET_DATA + menu.getRootForm().getType(); //
			 * mTaskFragment.startData(url, getAdapter(menu.getId()), menu);
			 * 
			 * mTaskFragment.startData(url, getStaticAdapter(getActivity(), menu, daoFactory), menu);
			 */

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
