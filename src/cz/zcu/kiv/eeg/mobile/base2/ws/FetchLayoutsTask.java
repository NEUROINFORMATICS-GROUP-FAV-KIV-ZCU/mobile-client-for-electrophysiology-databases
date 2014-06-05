package cz.zcu.kiv.eeg.mobile.base2.ws;

import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.DONE;
import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.ERROR;

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
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.LayoutSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.FormBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutList;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;
import cz.zcu.kiv.eeg.mobile.base2.ws.ssl.SSLSimpleClientHttpRequestFactory;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FetchLayoutsTask extends AsyncTask<Void, Integer, Void> {
	private static final String TAG = FetchLayoutsTask.class.getSimpleName();
	private TaskFragment fragment;
	private FormTypeSpinnerAdapter formAdapter;

	public FetchLayoutsTask(TaskFragment fragment, FormTypeSpinnerAdapter formAdapter) {
		this.fragment = fragment;
		this.formAdapter = formAdapter;
	}

	@Override
	protected Void doInBackground(Void... ignore) {
		User user = fragment.getDaoFactory().getUserDAO().getUser();
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

		try {
			ResponseEntity<LayoutList> responseList = restTemplate.exchange(urlAvailableLayouts, HttpMethod.GET,
					entity, LayoutList.class);
			LayoutList body = responseList.getBody();

			if (body != null) {
				fragment.createProgressBarHorizontal(body.size(), R.string.working_ws_layouts);
				System.out.println("layouty ulozeny" + body.size());

				for (Layout layout : body.getLayouts()) {
					String urlLayout = user.getUrl()+ String.format(Values.SERVICE_GET_LAYOUT, layout.getFormName(), layout.getName());
					ResponseEntity<Resource> result = restTemplate.exchange(urlLayout, HttpMethod.GET, entity,
							Resource.class);
					new FormBuilder(fragment.getDaoFactory(), result).start();
					publishProgress(1);
				}
			}
		} catch (Exception e) {
			fragment.setState(ERROR, e);
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void v) {
		fragment.setState(DONE);

		formAdapter.clear();
		for (Form form : (ArrayList<Form>) fragment.getDaoFactory().getFormDAO().getForms()) {
			formAdapter.add(form);
		}
		//formAdapter = new FormTypeSpinnerAdapter(fragment.getActivity(), R.layout.spinner_row_simple, (ArrayList<Form>) fragment.getDaoFactory().getFormDAO().getForms());
		
		
		//formAdapter.notifyDataSetChanged();

		// TODO napsat kolik sem jich naimportoval
		// //Toast.makeText(fragment.activity, R.string.settings_saved,
		// Toast.LENGTH_SHORT).show();
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
