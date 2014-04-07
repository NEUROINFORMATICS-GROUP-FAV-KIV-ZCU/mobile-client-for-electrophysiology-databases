package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.ViewBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;

public class FormDetailsFragment extends Fragment {

	public final static String TAG = FormDetailsFragment.class.getSimpleName();
	private ViewBuilder vb;
	private int index;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle b = getActivity().getIntent().getExtras();
		index = b.getInt("index");
		vb = new ViewBuilder(getActivity());	
		View view = vb.getLinearLayout("Person-generated");
		vb.initData(index, "Person");
		return view;		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPause() {
		super.onPause();
		vb.saveData(index, "Person"); // todo odstranit "person"
	}
}
