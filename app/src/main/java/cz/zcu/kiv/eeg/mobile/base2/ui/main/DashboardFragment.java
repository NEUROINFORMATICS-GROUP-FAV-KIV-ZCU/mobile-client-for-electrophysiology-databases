package cz.zcu.kiv.eeg.mobile.base2.ui.main;

import cz.zcu.kiv.eeg.mobile.base2.R;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class DashboardFragment extends Fragment {

	public static final String TAG = DashboardFragment.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle(R.string.menu_dashboard);
		actionBar.setIcon(R.drawable.ic_action_person);
	}

	/**
	 * In moment of creating view inflates dashboard layout.
	 * 
	 * @param inflater layout inflater
	 * @param container parent view container
	 * @param savedInstanceState data from previous fragment instance
	 * @return inflated view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dashboard_layout, container, false);
	}

}
