package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

/**
 * Tab listener for tab onclick event handling.
 * 
 * @author Jaroslav Hošek
 * 
 */
public class TabListener<T extends Fragment> implements ActionBar.TabListener {
	private final Activity activity;
	private final String tag;
	private final Class<T> classType;
	private Fragment fragment;

	/**
	 * Constructor used each time a new tab is created.
	 * 
	 * @param activity The host Activity, used to instantiate the fragment
	 * @param tag The identifier tag for the fragment
	 * @param classType The fragment's Class, used to instantiate the fragment
	 */
	public TabListener(Activity activity, String tag, Class<T> classType) {
		this.activity = activity;
		this.tag = tag;
		this.classType = classType;
	}

	/* The following are each of the ActionBar.TabListener callbacks */

	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		fragment = activity.getFragmentManager().findFragmentByTag(tag);
		if (fragment == null) {
			fragment = Fragment.instantiate(activity, classType.getName());
			ft.add(android.R.id.content, fragment, tag);

		} else {
			ft.attach(fragment);
		}
	}

	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
		if (fragment != null) {
			ft.detach(fragment);
		}
	}

	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// User selected the already selected tab. Usually do nothing.
	}
}