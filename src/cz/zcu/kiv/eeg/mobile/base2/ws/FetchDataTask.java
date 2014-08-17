package cz.zcu.kiv.eeg.mobile.base2.ws;

import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.DONE;
import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.ERROR;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import odml.core.Property;
import odml.core.Section;
import odml.core.Writer;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.DataBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.FormBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormLayouts;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormRow;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutList;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Record;
import cz.zcu.kiv.eeg.mobile.base2.data.model.RecordCount;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.ListAllFormsFragment;
import cz.zcu.kiv.eeg.mobile.base2.ws.ssl.SSLSimpleClientHttpRequestFactory;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class FetchDataTask extends AsyncTask<Void, Integer, HashMap<String, Layout>> {
	private static final String TAG = FetchDataTask.class.getSimpleName();
	private TaskFragment fragment;
	private FormAdapter adapter;
	private MenuItems workspace;
	private DAOFactory daoFactory;
	private Layout rootLayout;
	private User user;
	private StringBuilder errorMessage = new StringBuilder();

	public FetchDataTask(TaskFragment fragment, MenuItems workspace, Layout rootLayout, FormAdapter adapter,
			DAOFactory daoFactory) {
		this.fragment = fragment;
		this.adapter = adapter;
		this.workspace = workspace;
		this.daoFactory = daoFactory;
		this.rootLayout = rootLayout;
		this.user = workspace.getCredential();

	}

	@Override
	protected void onPreExecute() {
		fragment.createProgressBarHorizontal(1, R.string.working_ws_layouts);
	}

	@Override
	protected HashMap<String, Layout> doInBackground(Void... ignore) {
		HttpAuthentication authHeader = new HttpBasicAuthentication(user.getUsername(), user.getPassword());
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAuthorization(authHeader);
		requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
		requestHeaders.setContentType(MediaType.APPLICATION_XML); // post, put

		SSLSimpleClientHttpRequestFactory factory = new SSLSimpleClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());
		restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

		HashMap<String, Layout> layouts = new HashMap<String, Layout>();
		// aktualize (hlavně stavu)
		rootLayout = daoFactory.getLayoutDAO().getLayout(rootLayout.getName());
		getLayoutsForUpdate(rootLayout, layouts);

		try {
			updateLayouts(requestHeaders, restTemplate, layouts);
			updateData(requestHeaders, restTemplate, layouts);
			sendData(requestHeaders, restTemplate, layouts);

		} catch (Exception e) {
			fragment.setState(ERROR, e);
			Log.e(TAG, e.getMessage(), e);
		}
		return layouts;
	}

	private void sendData(HttpHeaders requestHeaders, RestTemplate restTemplate, HashMap<String, Layout> layouts) {
		String url = user.getUrl() + Values.SERVICE_POST_DATA;

		for (Map.Entry<String, Layout> entry : layouts.entrySet()) {
			Layout layout = entry.getValue();
			String formName = layout.getRootForm().getType();

			HashMap<Integer, String> xmlData = getData(layout);
			for (Map.Entry<Integer, String> xmlEntry : xmlData.entrySet()) {
				int datasetID = xmlEntry.getKey();
				Dataset dataset = daoFactory.getDataSetDAO().getDataSet(datasetID);
				fragment.resetProgressBarHorizontal(xmlData.size(), R.string.working_ws_upload_data, layout
						.getRootForm().getType());
				String xml = xmlEntry.getValue();			

				try {
					HttpEntity<Object> entity = new HttpEntity<Object>(xml, requestHeaders);
					ResponseEntity<Record> responseList = restTemplate.exchange(url, HttpMethod.POST, entity,
							Record.class, formName);
					Record recordId = responseList.getBody();

					if (recordId != null) {
						dataset.setRecordId(recordId.getId());
						dataset.setState(0);
						daoFactory.getDataSetDAO().saveOrUpdate(dataset);
					}
					publishProgress(1);
				} catch (Exception e) {
					errorMessage.append("Data saving for " + dataset.getForm().getType() + " entity (ID:" + datasetID
							+ ") is not supported by server.");

					publishProgress(1);
					Log.e(TAG, e.getMessage(), e);
				}
			}

		}
	}

	private HashMap<Integer, String> getData(Layout layout) {
		Form form = daoFactory.getFormDAO().getForm(layout.getRootForm().getType());
		List<Dataset> datasetList = daoFactory.getDataSetDAO().getDataSet(form, Values.ACTION_ADD);	
		HashMap<Integer, String> xmlData = new HashMap<Integer, String>();

		for (Dataset dataset : datasetList) {
			Section rootSection = new Section();
			rootSection.setType(form.getType());

			Property property = null;
			List<Data> dataList = daoFactory.getDataDAO().getDataByDataset(dataset.getId());
			for (Data data : dataList) {

				Field field = daoFactory.getFieldDAO().getField(data.getField().getId());
				if (field == null) {
					continue;
				}
				try {
					property = new Property(field.getName(), data.getData());
				} catch (Exception e) {
					e.printStackTrace();
				}
				property.setType(field.getDataType());
				rootSection.add(property);
			}

			Writer writer = new Writer(rootSection);
			OutputStream stream = new ByteArrayOutputStream();
			writer.write(stream);
			String xml = stream.toString();
			xmlData.put(dataset.getId(), xml);
		}
		return xmlData;
	}

	private void updateData(HttpHeaders requestHeaders, RestTemplate restTemplate, HashMap<String, Layout> layouts) {
		String countUrl = user.getUrl() + Values.SERVICE_GET_DATA_COUNT;
		String idsUrl = user.getUrl() + Values.SERVICE_GET_IDS;
		String dataUrl = user.getUrl() + Values.SERVICE_GET_DATA;
		int errorId = -1;

		int k = 0;
		for (Map.Entry<String, Layout> entry : layouts.entrySet()) {
			Layout layout = entry.getValue();
			String formName = layout.getRootForm().getType();
			fragment.resetProgressBarHorizontal(1, R.string.working_ws_data, layout.getRootForm().getType());

			try {
				HttpEntity<Object> entity = new HttpEntity<Object>(requestHeaders);
				ResponseEntity<RecordCount> responseList = restTemplate.exchange(countUrl, HttpMethod.GET, entity,
						RecordCount.class, formName);
				RecordCount count = responseList.getBody();

				if (count != null) {

					ResponseEntity<String> result = restTemplate.exchange(idsUrl, HttpMethod.GET, entity, String.class,
							formName);
					String data = result.getBody().toString();

					// TODO fix - mapování přes JSON a XML vyhazovalo chybu
					data = data.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
					data = data.replaceAll("</id>", "\n");
					data = data.replace("<ids>", "\n");
					data = data.replace("</ids>", "\n");
					data = data.replaceAll("<id>", "");

					String[] dataID = data.split("\n");

					publishProgress(1);
					fragment.resetProgressBarHorizontal(dataID.length, R.string.working_ws_data, layout.getRootForm()
							.getType());
					for (int i = 1; i < dataID.length; i++) {
						errorId = Integer.parseInt(dataID[i]);
						if (k == 297) {
							System.out.println();
						}
						k++;
						System.out.println("AAA počet " + k);
						System.out.println("AAA layout: " + layout.getName() + "ID: " + dataID[i] + " i:" + i);

						ResponseEntity<Resource> result3 = restTemplate.exchange(dataUrl, HttpMethod.GET, entity,
								Resource.class, layout.getRootForm().getType(), dataID[i]);

						DataBuilder databuilder = new DataBuilder(fragment.getDaoFactory(), workspace, result3);
						databuilder.getData();
						publishProgress(1);

						/*if (i == 15) {
							break;
						}*/
					}
				}
			} catch (Exception e) {
				setDataError(formName, errorId, e);
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	/**
	 * @param requestHeaders
	 * @param restTemplate
	 * @param layouts
	 */
	private void updateLayouts(HttpHeaders requestHeaders, RestTemplate restTemplate, HashMap<String, Layout> layouts) {
		String url = user.getUrl() + Values.SERVICE_LAYOUT;
		String urlAvailableLayouts = user.getUrl() + Values.SERVICE_GET_AVAILABLE_LAYOUTS;

		List<Layout> updateLayouts = new ArrayList<Layout>();
		List<Layout> newLayouts = new ArrayList<Layout>();
		List<Layout> modifiedLayouts = new ArrayList<Layout>();

		for (Map.Entry<String, Layout> entry : layouts.entrySet()) {
			Layout layout = entry.getValue();
			if (layout.getState() == Values.ACTION_ADD) {
				newLayouts.add(layout);
			} else if (layout.getState() == Values.ACTION_EDIT) {
				modifiedLayouts.add(layout);
			} else {
				updateLayouts.add(layout);
			}
		}

		// stažení seznamu existujících layoutů na serveru
		HttpEntity<Object> entityList = new HttpEntity<Object>(requestHeaders);
		ResponseEntity<LayoutList> responseList = restTemplate.exchange(urlAvailableLayouts, HttpMethod.GET,
				entityList, LayoutList.class);
		LayoutList body = responseList.getBody();

		if (body != null) {
			// pokud modifikovaný layout na serveru neexistuje, tak je potřeba ho napřed přidat
			modifiedLayouts = recountModifiedLayouts(body, modifiedLayouts, newLayouts);
		}

		// upload nově vytvořených layoutů
		if (newLayouts.size() > 0) {
			boolean conflict = false;
			fragment.resetProgressBarHorizontal(newLayouts.size(), R.string.working_ws_uploading_new_layouts);
			for (Layout layout : newLayouts) {
				try {
					HttpEntity<Object> entity = new HttpEntity<Object>(layout.getXmlData(), requestHeaders);
					restTemplate.exchange(url, HttpMethod.POST, entity, Resource.class, layout.getRootForm().getType(),
							layout.getName());
					layout.setState(0);
					daoFactory.getLayoutDAO().saveOrUpdate(layout);
					publishProgress(1);
				} catch (Exception error) {
					if (error instanceof HttpClientErrorException) {
						HttpStatus status = ((HttpClientErrorException) error).getStatusCode();
						if (status == HttpStatus.CONFLICT) {
							if (!conflict) {
								errorMessage.append(fragment.getActivity().getString(R.string.error_http_409));
								conflict = true;
							}
							errorMessage.append("\t" + layout.getName() + "\n");
						} else {
							fragment.setState(ERROR, error);
							Log.e(TAG, error.getMessage(), error);
						}
					}
				}
			}
		}

		// upload změněných layoutů
		if (modifiedLayouts.size() > 0) {
			boolean forbidden = false;
			fragment.resetProgressBarHorizontal(updateLayouts.size(), R.string.working_ws_uploading_layouts);
			for (Layout layout : modifiedLayouts) {
				try {
					HttpEntity<Object> entity = new HttpEntity<Object>(layout.getXmlData(), requestHeaders);
					restTemplate.exchange(url, HttpMethod.PUT, entity, Resource.class, layout.getRootForm().getType(),
							layout.getName());
					layout.setState(0);
					daoFactory.getLayoutDAO().saveOrUpdate(layout);
					publishProgress(1);
				} catch (Exception error) {
					if (error instanceof HttpClientErrorException) {
						HttpStatus status = ((HttpClientErrorException) error).getStatusCode();
						if (status == HttpStatus.FORBIDDEN) {
							if (!forbidden) {
								errorMessage.append(fragment.getActivity().getString(
										R.string.error_http_403_layout_update));
								forbidden = true;
							}
							errorMessage.append("\t" + layout.getName() + "\n");
						} else {
							fragment.setState(ERROR, error);
							Log.e(TAG, error.getMessage(), error);
						}
					}
				}
			}
		}

		// aktualizace layoutů v telefonu
		if (updateLayouts.size() > 0) {
			fragment.resetProgressBarHorizontal(updateLayouts.size(), R.string.working_ws_updating_layouts);
			for (Layout layout : updateLayouts) {
				HttpEntity<Object> entity = new HttpEntity<Object>(requestHeaders);
				ResponseEntity<Resource> result = restTemplate.exchange(url, HttpMethod.GET, entity, Resource.class,
						layout.getRootForm().getType(), layout.getName());
				FormBuilder formBuilder = new FormBuilder(fragment.getDaoFactory(), result);
				formBuilder.start();
				publishProgress(1);
			}
		}
	}

	private void getLayoutsForUpdate(Layout rootLayout, HashMap<String, Layout> layouts) {
		layouts.put(rootLayout.getName(), rootLayout);

		List<Layout> sublayouts = new ArrayList<Layout>();
		List<FormLayouts> formLayouts = daoFactory.getFormLayoutsDAO().getFormLayouts(rootLayout);
		for (FormLayouts formLayout : formLayouts) {
			if (formLayout.getSublayout() != null) {
				sublayouts.add(daoFactory.getLayoutDAO().getLayout(formLayout.getSublayout().getName()));
			}
		}

		for (Layout sublayout : sublayouts) {
			sublayout = daoFactory.getLayoutDAO().getLayout(sublayout.getName());
			if (!layouts.containsKey(sublayout.getName())) {
				getLayoutsForUpdate(sublayout, layouts);
			}
		}
	}

	private List<Layout> recountModifiedLayouts(LayoutList body, List<Layout> modifiedLayouts, List<Layout> newLayouts) {
		List<Layout> modifiedLayoutsCopy = new ArrayList<Layout>();
		for (Layout layout : modifiedLayouts) {
			modifiedLayoutsCopy.add(layout);
		}

		for (Layout modifiedLayout : modifiedLayouts) {
			boolean used = false;
			for (Layout layout : body.getLayouts()) {
				if (modifiedLayout.getName().equalsIgnoreCase(layout.getName())) {
					used = true;
				}
			}

			if (!used) {
				newLayouts.add(modifiedLayout);
				modifiedLayoutsCopy.remove(modifiedLayout);
			}
		}
		return modifiedLayoutsCopy;
	}

	private void setDataError(String entity, int id, Exception e) {
		Activity activity = fragment.getActivity();
		if (id >= 0) {
			errorMessage.append(entity + ", ID:" + id + "\n");
		}

		if (e instanceof HttpClientErrorException) {
			HttpStatus status = ((HttpClientErrorException) e).getStatusCode();

			switch (status) {
			case BAD_REQUEST:
				errorMessage.append(activity.getString(R.string.error_http_400) + "\n ");
				break;
			case UNAUTHORIZED:
				errorMessage.append(activity.getString(R.string.error_http_401) + "\n ");
				break;
			case FORBIDDEN:
				errorMessage.append(activity.getString(R.string.error_http_403) + "\n ");
				break;
			case NOT_FOUND:
				errorMessage.append(activity.getString(R.string.error_http_404_data) + "\n ");
				break;
			case METHOD_NOT_ALLOWED:
				errorMessage.append(activity.getString(R.string.error_http_405) + "\n ");
				break;
			case REQUEST_TIMEOUT:
				errorMessage.append(activity.getString(R.string.error_http_408) + "\n ");
				break;
			}
			// error on server side
		} else if (e instanceof HttpServerErrorException) {
			HttpStatus status = ((HttpServerErrorException) e).getStatusCode();
			switch (status) {
			case INTERNAL_SERVER_ERROR:
				errorMessage.append(activity.getString(R.string.error_http_500) + "\n ");
				break;
			case SERVICE_UNAVAILABLE:
				errorMessage.append(activity.getString(R.string.error_http_503) + "\n ");
				break;
			}		
		}
	}

	@Override
	protected void onPostExecute(HashMap<String, Layout> layouts) {
		if (errorMessage.length() > 0) {
			fragment.showAlert(errorMessage.toString());
		} else {
			for (Map.Entry<String, Layout> entry : layouts.entrySet()) {
				Layout layout = entry.getValue();
				if (layout != rootLayout) {
					ListAllFormsFragment.removeAdapter(workspace, layout);
					ListAllFormsFragment.getStaticAdapter(fragment.getActivity(), workspace, rootLayout, daoFactory);
				}
			}
		}

		adapter.clear();
		Field previewMajor = null;
		Field previewMinor = null;

		if (rootLayout.getPreviewMajor() != null) {
			previewMajor = daoFactory.getFieldDAO().getField(rootLayout.getPreviewMajor().getId());
		}
		if (rootLayout.getPreviewMinor() != null) {
			previewMinor = daoFactory.getFieldDAO().getField(rootLayout.getPreviewMinor().getId());
		}

		for (Dataset dataset : daoFactory.getDataSetDAO().getDataSet(rootLayout.getRootForm(), workspace)) {
			int dataset_id = dataset.getId();
			String descriptionData1 = null;
			String descriptionData2 = null;

			if (previewMajor != null) {
				Data description1 = daoFactory.getDataDAO().getData(dataset_id, previewMajor.getId());
				if (description1 != null) {
					descriptionData1 = description1.getData();
				}
			}
			if (previewMinor != null) {
				Data description2 = daoFactory.getDataDAO().getData(dataset_id, previewMinor.getId());
				if (description2 != null) {
					descriptionData2 = description2.getData();
				}
			}
			adapter.add(new FormRow(dataset.getRecordId(), descriptionData1, descriptionData2, "Já"));
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
