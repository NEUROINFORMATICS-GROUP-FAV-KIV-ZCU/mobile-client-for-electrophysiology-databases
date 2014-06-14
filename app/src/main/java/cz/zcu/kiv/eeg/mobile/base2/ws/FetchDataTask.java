package cz.zcu.kiv.eeg.mobile.base2.ws;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.StoreFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;
import cz.zcu.kiv.eeg.mobile.base2.ws.ssl.SSLSimpleClientHttpRequestFactory;

import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.DONE;
import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.ERROR;
import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.RUNNING;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FetchDataTask extends AsyncTask<String, Integer, Void> {
	private static final String TAG = FetchDataTask.class.getSimpleName();
	private TaskFragment fragment;
	private FormAdapter adapter;
	private MenuItems menu;

	public FetchDataTask(TaskFragment fragment, FormAdapter adapter, MenuItems menu) {
		this.fragment = fragment;
		this.adapter = adapter;
		this.menu = menu;
	}

	@Override
	protected void onPreExecute() {
		fragment.setState(RUNNING);
	}
	
	@Override
	protected Void doInBackground(String... serviceUrl) {
		User user = fragment.getStore().getUserStore().getUser();
		String url = user.getUrl() + serviceUrl[0];

		HttpAuthentication authHeader = new HttpBasicAuthentication(user.getUsername(), user.getPassword());
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAuthorization(authHeader);
		requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
		HttpEntity<Object> entity = new HttpEntity<Object>(requestHeaders);
		
		SSLSimpleClientHttpRequestFactory factory = new SSLSimpleClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());

		try {
			ResponseEntity<Resource> result = restTemplate.exchange(url, HttpMethod.GET, entity, Resource.class);
//			new DataBuilder(fragment.getStore(), result).getData();
		} catch (Exception e) {
			fragment.setState(ERROR, e);
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void v) {
        StoreFactory storeFactory = fragment.getStore();

//                String id = "id";// menu.getFieldID().getName(); //ID DATASETU
//		Field description1Field = daoFactory.getFieldDAO().getField(menu.getPreviewMajor().getId());
//		Field description2Field = daoFactory.getFieldDAO().getField(menu.getPreviewMinor().getId());
//		Form form = daoFactory.getFormDAO().getFormByType(description1Field.getForm().getType());
//
//		for (Dataset dataset : daoFactory.getDataSetDAO().getDataSet(form)) {
//			int dataset_id = dataset.getId();
//			String descriptionData1 = null;
//			String descriptionData2 = null;
//
//			if (description1Field != null) {
//				Data description1 = daoFactory.getDataDAO().getData(dataset.getId(),
//						description1Field.getId());
//				if (description1 != null) {
//					descriptionData1 = description1.getData();
//				}
//			}
//			if (description2Field != null) {
//				Data description2 = daoFactory.getDataDAO().getData(dataset.getId(),
//						description2Field.getId());
//				if (description2 != null) {
//					descriptionData2 = description2.getData();
//				}
//			}
//			adapter.add(new FormRow(dataset_id, descriptionData1, descriptionData2, "Já"));
//		}
//		fragment.setState(DONE);
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
