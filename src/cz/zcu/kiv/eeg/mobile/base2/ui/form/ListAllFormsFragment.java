package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormRow;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.ui.main.DashboardActivity;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class ListAllFormsFragment extends ListFragment {

	public final static String TAG = ListAllFormsFragment.class.getSimpleName();
	public static FormAdapter adapter;
	// TODO podpora pro adaptéry ze všech formulářů
	private static SparseArray<FormAdapter> adapters = new SparseArray<FormAdapter>();
	private DAOFactory daoFactory;
	private MenuItems menu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true); // povolí action bar
		menu = ((FormActivity) getActivity()).getMenuData();
		daoFactory = new DAOFactory(getActivity());
		// layout = daoFactory.getLayoutDAO().getLayoutByName(menu.getLayout().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.form_list, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// aby se aktualizoval pri navratu
		setListAdapter(null);
		setListAdapter(getAdapter());
	}

	/*
	 * @Override public void onActivityCreated(Bundle savedInstanceState) { super.onActivityCreated(savedInstanceState);
	 * setListAdapter(null); setListAdapter(getAdapter()); }
	 */

	public FormAdapter getAdapter() {
		if (adapter == null) {
			adapter = new FormAdapter(getActivity(), R.layout.form_row, new ArrayList<FormRow>());

			// podle čeho hledám
			String id = "id";// menu.getFieldID().getName(); //ID DATASETU
			Field description1Field = daoFactory.getFieldDAO().getField(menu.getFieldDescription1().getId());
			Field description2Field = daoFactory.getFieldDAO().getField(menu.getFieldDescription2().getId());
			Form form = daoFactory.getFormDAO().getFormByType(description1Field.getForm().getType());

			for (Dataset dataset : daoFactory.getDataSetDAO().getDataSet(form)) {
				int dataset_id = dataset.getId();
				String descriptionData1 = null;
				String descriptionData2 = null;

				if (description1Field != null) {
					Data description1 = daoFactory.getDataDAO().getDataByDatasetAndField(dataset.getId(),
							description1Field.getId());
					if (description1 != null) {
						descriptionData1 = description1.getData();
					}
				}
				if (description2Field != null) {
					Data description2 = daoFactory.getDataDAO().getDataByDatasetAndField(dataset.getId(),
							description2Field.getId());
					if (description2 != null) {
						descriptionData2 = description2.getData();
					}
				}
				adapter.add(new FormRow(dataset_id, descriptionData1, descriptionData2, "Já"));
			}
		}
		return adapter;
	}

	public static FormAdapter getAdapterForUpdate() {
		return adapter;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		showDetails(position);
		// this.setSelection(position);
	}

	private void showDetails(int index) {
		FormAdapter dataAdapter = getAdapter();
		boolean empty = dataAdapter == null || dataAdapter.isEmpty();
		if (!empty) {
			FormRow row = dataAdapter.getItem(index);
			Intent intent = new Intent();
			intent.setClass(getActivity(), FormDetailsActivity.class);
			intent.putExtra(FormDetailsFragment.DATASET_ID, row.getId());
			intent.putExtra(DashboardActivity.MENU_ITEM_ID, menu.getId());
			intent.putExtra(DashboardActivity.MENU_ITEM_NAME, menu.getName());
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

	/*
	 * @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	 * inflater.inflate(R.menu.scenario_all_menu, menu); super.onCreateOptionsMenu(menu, inflater); }
	 */

	@Override
	public void onDestroy() {
		super.onDestroy();
		daoFactory.releaseHelper();
	}
}
