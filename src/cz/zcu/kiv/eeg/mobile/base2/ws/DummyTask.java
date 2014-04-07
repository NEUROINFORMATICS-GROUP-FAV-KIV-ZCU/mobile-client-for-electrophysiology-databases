package cz.zcu.kiv.eeg.mobile.base2.ws;

import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.DONE;
import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.RUNNING;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import android.util.Log;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.FormBuilder;
import cz.zcu.kiv.eeg.mobile.base2.ws.ssl.SSLSimpleClientHttpRequestFactory;

public class DummyTask extends AsyncTask<Void, Integer, Void> {
    private static final String TAG = DummyTask.class.getSimpleName();
    private TaskFragment fragment;
    // "http://10.0.2.2:8084/Clarity2GoServer/pokus";
    //private final String url ="http://10.0.2.2:8080/eegdatabase-2.0-RELEASE/rest/form-layouts/get/article";
   // private final String url = "https://uu404p22-kiv.fav.zcu.cz:8443/rest/form-layouts/get/Person/Person-generated"; // DEV
    private final String url = "https://uu404p22-kiv.fav.zcu.cz:8443/rest/scenarios/all"; // DEV

    
    
    // private final String url =
    // "https://uu404p22-kiv.fav.zcu.cz:8443/rest/form-layouts/get/article";
     //private final String url = "http://eegdatabase.kiv.zcu.cz/rest/form-layouts/get/article";

    public DummyTask(TaskFragment fragment) {
	this.fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
	fragment.setState(RUNNING);
    }

    @Override
    protected Void doInBackground(Void... ignore) {
	String username = "axim@students.zcu.cz";
        //String password = "123456";
	String password = "19821983";

	// HttpHeaders requestHeaders = new HttpHeaders();
	// requestHeaders.setAccept(Collections.singletonList(new
	// MediaType("application","json")));
	// HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

	HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
	HttpHeaders requestHeaders = new HttpHeaders();
	requestHeaders.setAuthorization(authHeader);
	HttpEntity<Object> entity = new HttpEntity<Object>(requestHeaders);

	SSLSimpleClientHttpRequestFactory factory = new SSLSimpleClientHttpRequestFactory();

	RestTemplate restTemplate = new RestTemplate(factory);
	// RestTemplate restTemplate = new RestTemplate();
	restTemplate.getMessageConverters().add(new StringHttpMessageConverter()); // toto fungovalo
	restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());

	try {
	    // ResponseEntity<String> result = restTemplate.exchange(url,HttpMethod.GET, entity, String.class);
	    ResponseEntity<Resource> result = restTemplate.exchange(url, HttpMethod.GET, entity, Resource.class);
	    new FormBuilder(fragment.getDaoFactory(), result).start();    
	     
	    Log.i("test", result.getBody().toString());

	} catch (Exception e) {
	    Log.i("test", "testovnicek");
	    Log.e(TAG, e.getMessage(), e);
	}
	/*
	 * for (int i = 0; !isCancelled() && i < 100; i++) { Log.i(TAG,
	 * "publishProgress(" + i + "%)"); SystemClock.sleep(100);
	 * publishProgress(i); }
	 */
	return null;
    }

    @Override
    protected void onPostExecute(Void ignore) {
	// fragment.setRunning(false);
	fragment.setState(DONE);
    }

    @Override
    protected void onProgressUpdate(Integer... percent) {
	fragment.setState(percent[0]);
    }

    @Override
    protected void onCancelled() {
	fragment.setState(DONE);
    }   
}
