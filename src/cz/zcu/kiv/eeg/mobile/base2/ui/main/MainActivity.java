package cz.zcu.kiv.eeg.mobile.base2.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private DAOFactory daoFactory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate(Bundle)");
		super.onCreate(savedInstanceState);
		daoFactory = new DAOFactory(this);

		/*User user = daoFactory.getUserDAO().getUser();
		if (user == null) {
			Intent loginIntent = new Intent(this, LoginActivity.class);
			loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(loginIntent);
		} else {*/
			Intent mainIntent = new Intent(this, DashboardActivity.class);
			mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(mainIntent);		
		/*}
		finish();*/		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		daoFactory.releaseHelper();
	}
}
