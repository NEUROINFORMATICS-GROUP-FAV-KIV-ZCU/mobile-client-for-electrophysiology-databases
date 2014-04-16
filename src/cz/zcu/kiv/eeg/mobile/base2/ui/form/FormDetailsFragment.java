package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.ViewBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormRow;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.ui.main.DashboardActivity;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FormDetailsFragment extends Fragment {

	public final static String TAG = FormDetailsFragment.class.getSimpleName();
	public final static String DATASET_ID = "datasetID";
	private DAOFactory daoFactory;
	 MenuItems menu;
	private Layout layout;
	private ViewBuilder vb;
	private int datasetID = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		daoFactory = new DAOFactory(getActivity());
		Bundle extras = getActivity().getIntent().getExtras();
		int menuItemID = extras.getInt(DashboardActivity.MENU_ITEM_ID, -1);
		datasetID = extras.getInt(DATASET_ID, -1);
		menu = daoFactory.getMenuItemDAO().getMenu(menuItemID);
		layout = daoFactory.getLayoutDAO().getLayoutByName(menu.getLayout().getName());

		vb = new ViewBuilder(getActivity(), layout);
		View view = vb.getLinearLayout();
		vb.initData(datasetID);
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPause() {
		super.onPause();
		int newDataset = vb.saveOrUpdateData(datasetID);
		SparseArray<FormAdapter> adapters = ListAllFormsFragment.getAdaptersForUpdate();
		
		for (int i = 1; i <= adapters.size(); i++) {
			
			FormAdapter adapter = adapters.get(i);
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		daoFactory.releaseHelper();
	}
}
