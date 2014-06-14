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
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.LayoutSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.DataBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.FormBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormRow;
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
		User user = fragment.getDaoFactory().getUserDAO().getUser();
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
			new DataBuilder(fragment.getDaoFactory(), result).getData();
		} catch (Exception e) {
			fragment.setState(ERROR, e);
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void v) {
		DAOFactory daoFactory = fragment.getDaoFactory();
		
		String id = "id";// menu.getFieldID().getName(); //ID DATASETU
		Field description1Field = daoFactory.getFieldDAO().getField(menu.getPreviewMajor().getId());
		Field description2Field = daoFactory.getFieldDAO().getField(menu.getPreviewMinor().getId());
		Form form = daoFactory.getFormDAO().getForm(description1Field.getForm().getType());

		for (Dataset dataset : daoFactory.getDataSetDAO().getDataSet(form)) {
			int dataset_id = dataset.getId();
			String descriptionData1 = null;
			String descriptionData2 = null;

			if (description1Field != null) {
				Data description1 = daoFactory.getDataDAO().getData(dataset.getId(),
						description1Field.getId());
				if (description1 != null) {
					descriptionData1 = description1.getData();
				}
			}
			if (description2Field != null) {
				Data description2 = daoFactory.getDataDAO().getData(dataset.getId(),
						description2Field.getId());
				if (description2 != null) {
					descriptionData2 = description2.getData();
				}
			}
			adapter.add(new FormRow(dataset_id, descriptionData1, descriptionData2, "Já"));
		}
		fragment.setState(DONE);		
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
