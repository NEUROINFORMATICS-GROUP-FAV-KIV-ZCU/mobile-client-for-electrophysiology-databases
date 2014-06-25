package cz.zcu.kiv.eeg.mobile.base2.ws;

import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.DONE;
import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.ERROR;
import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.RUNNING;

import java.util.ArrayList;
import java.util.Collections;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import android.util.Log;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormTypeSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.LayoutDialogAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.LayoutSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.FormBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutList;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;
import cz.zcu.kiv.eeg.mobile.base2.ws.ssl.SSLSimpleClientHttpRequestFactory;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FetchLayoutsTask extends AsyncTask<Void, Integer, ArrayList<String>> {
	private static final String TAG = FetchLayoutsTask.class.getSimpleName();
	private TaskFragment fragment;
	private MenuItems menu;

	public FetchLayoutsTask(TaskFragment fragment, MenuItems menu ) {
		this.fragment = fragment;
		this.menu = menu;
	}
	
	@Override
	protected void onPreExecute() {
		fragment.setState(RUNNING, R.string.working_ws_layouts);
	}

	@Override
	protected ArrayList<String> doInBackground(Void... ignore) {
		User user = menu.getCredential();
		String urlAvailableLayouts = user.getUrl() + Values.SERVICE_GET_AVAILABLE_LAYOUTS;

		HttpAuthentication authHeader = new HttpBasicAuthentication(user.getUsername(), user.getPassword());
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAuthorization(authHeader);
		requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
		HttpEntity<Object> entity = new HttpEntity<Object>(requestHeaders);
		SSLSimpleClientHttpRequestFactory factory = new SSLSimpleClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());
		restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());
		
		ArrayList<String> layouts = new ArrayList<String>();

		try {
			ResponseEntity<LayoutList> responseList = restTemplate.exchange(urlAvailableLayouts, HttpMethod.GET,
					entity, LayoutList.class);
			LayoutList body = responseList.getBody();

			if (body != null) {
				fragment.setState(DONE); // konec nekonečného progresu
				fragment.createProgressBarHorizontal(body.size(), R.string.working_ws_layouts);
				System.out.println("layouty ulozeny" + body.size());

				for (Layout layout : body.getLayouts()) {
					String urlLayout = user.getUrl()+ String.format(Values.SERVICE_GET_LAYOUT, layout.getFormName(), layout.getName());
					ResponseEntity<Resource> result = restTemplate.exchange(urlLayout, HttpMethod.GET, entity,
							Resource.class);
					FormBuilder formBuilder = new FormBuilder(fragment.getDaoFactory(), result);
					layouts.add(formBuilder.start());
					publishProgress(1);
				}
			}
		} catch (Exception e) {
			fragment.setState(ERROR, e);
			Log.e(TAG, e.getMessage(), e);		
		}
		return layouts;
	}

	@Override
	protected void onPostExecute(ArrayList<String> layouts) {
		fragment.setState(DONE);
		fragment.dashboard.createSelectLayoutDialog(layouts);	
	}

	@Override
	protected void onProgressUpdate(Integer... percent) {
		fragment.setStateIncrement(percent[0]);
	}

	@Override
	protected void onCancelled() {
		fragment.setState(DONE);
	}
}
