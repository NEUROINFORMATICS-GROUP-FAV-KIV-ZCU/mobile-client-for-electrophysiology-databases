package cz.zcu.kiv.eeg.mobile.base2.ui.settings;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.common.TaskFragmentActivity;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;
import cz.zcu.kiv.eeg.mobile.base2.util.ValidationUtils;
import cz.zcu.kiv.eeg.mobile.base2.ws.TaskFragment;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class LoginActivity extends TaskFragmentActivity {

	private static final String TAG = LoginActivity.class.getSimpleName();
	private DAOFactory daoFactory;
	private TaskFragment mTaskFragment;
	private User user;
	private User backup;
	private TextView usernameField;
	private TextView passwordField;
	private TextView urlField;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Settings screen");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		FragmentManager fm = getFragmentManager();
		mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG + "Fragment");
		if (mTaskFragment == null) {
			mTaskFragment = new TaskFragment();
			fm.beginTransaction().add(mTaskFragment, "taskFragment").commit();
		}

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.ic_action_settings);

		usernameField = (TextView) findViewById(R.id.settings_username);
		passwordField = (TextView) findViewById(R.id.settings_password);
		urlField = (TextView) findViewById(R.id.settings_url);

		daoFactory = new DAOFactory(this);
		user = daoFactory.getUserDAO().getUser();
		if (user != null) {
			usernameField.setText(user.getUsername());
			passwordField.setText(user.getPassword());
			urlField.setText(user.getUrl());			
		} else {
			user = new User();			
			urlField.setText(Values.URL_DEFAULT);// default
		}
		user.setId(1);
	}

	private void testCredentials() {		
		user.setUsername(usernameField.getText().toString());
		user.setPassword(passwordField.getText().toString());
		user.setUrl(urlField.getText().toString());		
		String error = ValidationUtils.isUserValid(this, user);
		
		if (error.toString().isEmpty()) {				
			daoFactory.getUserDAO().saveOrUpdate(user);  
			mTaskFragment.startLogin();
		} else {
			showAlert(error.toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login_settings_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.login:
			testCredentials();
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
