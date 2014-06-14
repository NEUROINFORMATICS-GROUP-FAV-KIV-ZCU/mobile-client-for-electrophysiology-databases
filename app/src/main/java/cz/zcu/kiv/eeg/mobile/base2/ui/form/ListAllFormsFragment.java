package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.StoreFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormRow;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
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
	private StoreFactory daoFactory;
	private MenuItems menu;
	private TaskFragment mTaskFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true); // povolí action bar
		daoFactory = new StoreFactory(getActivity());

		FragmentManager fm = getFragmentManager();
		mTaskFragment = (TaskFragment) fm.findFragmentByTag("taskFragment");
		if (mTaskFragment == null) {
			mTaskFragment = new TaskFragment();
			fm.beginTransaction().add(mTaskFragment, "taskFragment").commit();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.form_list, container, false);
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
		setListAdapter(getStaticAdapter(getActivity(), menu,daoFactory));
	}

	public static FormAdapter getStaticAdapter(Activity activity, MenuItems menu, StoreFactory store) {
		FormAdapter adapter = adapters.get(menu.getId());

		if (adapter == null) {
			adapter = new FormAdapter(activity, R.layout.form_row, menu, new ArrayList<FormRow>());

			// podle čeho hledám
			String id = "id";// menu.getFieldID().getName(); //ID DATASETU
			Field previewMajor = store.getFieldStore().getField(menu.getPreviewMajor().getId());
			Field PreviewMinor = store.getFieldStore().getField(menu.getPreviewMinor().getId());
			Form form = store.getFormStore().getFormByType(previewMajor.getForm().getType());

			for (Dataset dataset : store.getDatasetStore().getDataSet(form)) {
				int dataset_id = dataset.getId();
				String descriptionData1 = null;
				String descriptionData2 = null;

				if (previewMajor != null) {
					Data description1 = store.getDataStore().getData(dataset.getId(), previewMajor.getId());
					if (description1 != null) {
						descriptionData1 = description1.getData();
					}
				}
				if (PreviewMinor != null) {
					Data description2 = store.getDataStore().getData(dataset.getId(), PreviewMinor.getId());
					if (description2 != null) {
						descriptionData2 = description2.getData();
					}
				}
				adapter.add(new FormRow(dataset_id, descriptionData1, descriptionData2, "Já"));
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
		//FormAdapter dataAdapter = getAdapter(menu.getId());
		FormAdapter dataAdapter = getStaticAdapter(getActivity(), menu,daoFactory);
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.form_refresh:
			clearAdapter(menu.getId());
			String url = Values.SERVICE_GET_DATA + menu.getRootForm().getType();
			// mTaskFragment.startData(url, getAdapter(menu.getId()), menu);
			
			  mTaskFragment.startData(url, getStaticAdapter(getActivity(), menu,
			  daoFactory), menu);
			 
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		daoFactory.releaseHelper();
	}
}
