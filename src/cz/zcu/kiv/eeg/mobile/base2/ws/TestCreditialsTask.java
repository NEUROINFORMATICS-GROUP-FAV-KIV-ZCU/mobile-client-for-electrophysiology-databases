package cz.zcu.kiv.eeg.mobile.base2.ws;

import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.DONE;
import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.ERROR;
import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.RUNNING;

import java.util.Collections;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;
import cz.zcu.kiv.eeg.mobile.base2.ui.main.DashboardActivity;
import cz.zcu.kiv.eeg.mobile.base2.ws.ssl.SSLSimpleClientHttpRequestFactory;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class TestCreditialsTask extends AsyncTask<User, Void, User> {
	private static final String TAG = TestCreditialsTask.class.getSimpleName();
	private TaskFragment fragment;
	//private User user;

	public TestCreditialsTask(TaskFragment fragment) {
		this.fragment = fragment;
		//this.user = user;
	}

	@Override
	protected void onPreExecute() {
		fragment.setState(RUNNING, R.string.working_ws_credentials);
	}

	@Override
	protected User doInBackground(User... users) {
		User user = users[0];
		user.setFirstName(null);		
		String url = user.getUrl() + Values.SERVICE_USER_LOGIN + "login";

		HttpAuthentication authHeader = new HttpBasicAuthentication(user.getUsername(), user.getPassword());
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAuthorization(authHeader);
		requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
		HttpEntity<Object> entity = new HttpEntity<Object>(requestHeaders);
		SSLSimpleClientHttpRequestFactory factory = new SSLSimpleClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

		try {
			ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);
			User tmp = response.getBody();
			user.setFirstName(tmp.getFirstName());
			user.setSurname(tmp.getSurname());
			user.setRights(tmp.getRights());		
		} 
		catch (Exception e) {
			fragment.setState(ERROR, e);
			Log.i("test", "testovnicek");
			Log.e(TAG, e.getMessage(), e);
		}		
		return user;
	}

	@Override
	protected void onPostExecute(User testUser) {
		fragment.setState(DONE);
		
		if (testUser.getFirstName() != null) {
			fragment.dashboard.saveNewWorkspace(false, true);			
			
		}
	}

	@Override
	protected void onCancelled() {
		fragment.setState(DONE);
	}
}
