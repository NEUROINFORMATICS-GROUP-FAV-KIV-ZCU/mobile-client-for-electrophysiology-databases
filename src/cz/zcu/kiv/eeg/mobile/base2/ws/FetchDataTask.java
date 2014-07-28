package cz.zcu.kiv.eeg.mobile.base2.ws;

import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.DONE;
import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.ERROR;
import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.RUNNING;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import odml.core.Property;
import odml.core.Section;
import odml.core.Writer;

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

import android.app.Activity;
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
import cz.zcu.kiv.eeg.mobile.base2.data.model.Ids;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutList;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.data.model.RecordCount;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.ListAllFormsFragment;
import cz.zcu.kiv.eeg.mobile.base2.ws.ssl.SSLSimpleClientHttpRequestFactory;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FetchDataTask extends AsyncTask<String, Integer, ArrayList<String>> {
	private static final String TAG = FetchDataTask.class.getSimpleName();
	private TaskFragment fragment;
	private FormAdapter adapter;
	private MenuItems workspace;
	private DAOFactory daoFactory;

	public FetchDataTask(TaskFragment fragment, MenuItems menu, DAOFactory daoFactory) {
		this.fragment = fragment;
		this.adapter = adapter;
		this.workspace = menu;
		this.daoFactory = daoFactory;
	}

	@Override
	protected void onPreExecute() {
		fragment.setState(RUNNING);
	}

	@Override
	protected ArrayList<String> doInBackground(String... serviceUrl) {
		User user = workspace.getCredential();
		String url = user.getUrl() + serviceUrl[0];
		String url2 = user.getUrl() + serviceUrl[1];
		String url3 = user.getUrl() + serviceUrl[2];

		HttpAuthentication authHeader = new HttpBasicAuthentication(user.getUsername(), user.getPassword());
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAuthorization(authHeader);
		requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
		HttpEntity<Object> entity = new HttpEntity<Object>(requestHeaders);

		SSLSimpleClientHttpRequestFactory factory = new SSLSimpleClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());
		restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

		ArrayList<String> layouts = new ArrayList<String>();

		try {
			ResponseEntity<RecordCount> responseList = restTemplate.exchange(url, HttpMethod.GET, entity,
					RecordCount.class);
			RecordCount body = responseList.getBody();

			if (body != null) {

				ResponseEntity<String> result = restTemplate.exchange(url2, HttpMethod.GET, entity, String.class);
				String data = result.getBody().toString();

				data = data.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
				data = data.replaceAll("</id>", "\n");
				data = data.replace("<ids>", "\n");
				data = data.replace("</ids>", "\n");
				data = data.replaceAll("<id>", "");

				String[] dataID = data.split("\n");

				fragment.setState(DONE); // konec nekonečného progresu
				fragment.createProgressBarHorizontal(dataID.length, R.string.working_ws_layouts);
				for (int i = 1; i < dataID.length; i++) {				

					String urlLayout = url3 + dataID[i];
					ResponseEntity<Resource> result3 = restTemplate.exchange(urlLayout, HttpMethod.GET, entity,
							Resource.class);

					//System.out.println("zzzzzzzzzzzzzzzzzzzzzzz " + urlLayout );
					DataBuilder databuilder = new DataBuilder(fragment.getDaoFactory(), workspace, result3);
					databuilder.getData();
					publishProgress(1);
				}

				/*
				 * ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, }
				 * String.class);
				 */

			}
			
			/*
			 * HttpEntity<String> entita = new HttpEntity<String>(xml, requestHeaders); // Make the network request
			 * restTemplate.postForObject(url, entita, String.class);
			 */

			// ResponseEntity<Resource> result = restTemplate.exchange(url, HttpMethod.GET, entity, Resource.class);
			// new DataBuilder(fragment.getDaoFactory(), result).getData();
		} catch (Exception e) {
			fragment.setState(ERROR, e);
			Log.e(TAG, e.getMessage(), e);
		}
		return layouts;
	}
	
	
	
	
	
	
	
	
	//metoda na odeslání dat
	private void post(){
		Form form = workspace.getRootForm();
		List<Dataset> datasety = daoFactory.getDataSetDAO().getDataSet(form, workspace.getParentId());

		for (Dataset dataset : datasety) {

			Section rootSection;

			String formType = form.getType();
			rootSection = new Section();
			rootSection.setType(formType);
			rootSection.setReference(formType);
			if(dataset.getRecordId() != null){
				rootSection.setName(dataset.getRecordId());
			}
			else{
				rootSection.setName("empty");
			}

			Property property = null;
			List<Data> dataTmp = daoFactory.getDataDAO().getDataByDataset(dataset.getId());
			for (Data data : dataTmp) {
				
				Field field = daoFactory.getFieldDAO().getField(data.getId());
				if(field == null){
					continue;
				}
				//property = new Property(field.getName(), data.getData());
				property.setType(field.getDataType());
			}
			if (property != null) {
				rootSection.add(property);
			}

			Writer writer = new Writer(rootSection);
			OutputStream stream = new ByteArrayOutputStream();
			writer.write(stream);
			String xml = stream.toString();

			System.out.println("result: " + xml);
			break;
		}

		System.out.println();
		
		/*ResponseEntity<String> response = restTemplate.exchange(url3, HttpMethod.POST, entity, String.class);
		String vysledek = response.getBody();*/

		System.out.println();
	}

	@Override
	protected void onPostExecute(ArrayList<String> layouts) {
		fragment.setState(DONE);

		ListAllFormsFragment.resetAdapter(workspace);
		fragment.formActivity.finish();

		/*
		 * DAOFactory daoFactory = fragment.getDaoFactory();
		 * 
		 * String id = "id";// menu.getFieldID().getName(); //ID DATASETU Field description1Field =
		 * daoFactory.getFieldDAO().getField(menu.getPreviewMajor().getId()); Field description2Field =
		 * daoFactory.getFieldDAO().getField(menu.getPreviewMinor().getId()); Form form =
		 * daoFactory.getFormDAO().getForm(description1Field.getForm().getType());
		 * 
		 * for (Dataset dataset : daoFactory.getDataSetDAO().getDataSet(form)) { int dataset_id = dataset.getId();
		 * String descriptionData1 = null; String descriptionData2 = null;
		 * 
		 * if (description1Field != null) { Data description1 = daoFactory.getDataDAO().getData(dataset.getId(),
		 * description1Field.getId()); if (description1 != null) { descriptionData1 = description1.getData(); } } if
		 * (description2Field != null) { Data description2 = daoFactory.getDataDAO().getData(dataset.getId(),
		 * description2Field.getId()); if (description2 != null) { descriptionData2 = description2.getData(); } }
		 * adapter.add(new FormRow(dataset_id, descriptionData1, descriptionData2, "Já")); } fragment.setState(DONE);
		 */
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
