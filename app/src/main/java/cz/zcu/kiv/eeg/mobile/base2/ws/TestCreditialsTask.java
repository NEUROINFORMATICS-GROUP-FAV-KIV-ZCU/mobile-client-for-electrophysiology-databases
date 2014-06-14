package cz.zcu.kiv.eeg.mobile.base2.ws;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;
import cz.zcu.kiv.eeg.mobile.base2.ui.main.DashboardActivity;
import cz.zcu.kiv.eeg.mobile.base2.ws.ssl.SSLSimpleClientHttpRequestFactory;

import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.DONE;
import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.ERROR;
import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.RUNNING;

/**
 * @author Jaroslav Hošek
 */
public class TestCreditialsTask extends AsyncTask<Void, Void, User> {
    private static final String TAG = TestCreditialsTask.class.getSimpleName();
    private TaskFragment fragment;

    public TestCreditialsTask(TaskFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        fragment.setState(RUNNING, R.string.working_ws_credentials);
    }

    @Override
    protected User doInBackground(Void... ignore) {
        User testUser = fragment.getStore().getUserStore().getTestUser();
        testUser.setFirstName(null);
        String url = testUser.getUrl() + Values.SERVICE_USER_LOGIN + "login";

        HttpAuthentication authHeader = new HttpBasicAuthentication(testUser.getUsername(), testUser.getPassword());
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
            testUser.setFirstName(tmp.getFirstName());
            testUser.setSurname(tmp.getSurname());
            testUser.setRights(tmp.getRights());
        } catch (Exception e) {
            fragment.setState(ERROR, e);
            Log.i("test", "testovnicek");
            Log.e(TAG, e.getMessage(), e);
        }
        return testUser;
    }

    @Override
    protected void onPostExecute(User testUser) {
        fragment.setState(DONE);
        if (testUser.getFirstName() != null) {
            testUser.setId(2); //ověřený uživatel
            fragment.getStore().getUserStore().saveOrUpdate(testUser);
            Intent intent = new Intent(fragment.activity, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            fragment.activity.startActivity(intent);
            fragment.activity.finish();
        }
    }

    @Override
    protected void onCancelled() {
        fragment.setState(DONE);
    }
}
