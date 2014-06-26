package cz.zcu.kiv.eeg.mobile.base2.ws;

import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.INACTIVE;
import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.RUNNING;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.common.TaskFragmentActivity;
import cz.zcu.kiv.eeg.mobile.base2.data.TaskState;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormTypeSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.LayoutDialogAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.LayoutSpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.main.DashboardActivity;

/**
 * This Fragment manages a single background task and retains itself across configuration changes.
 * 
 * @author Jaroslav Hošek
 * 
 */
public class TaskFragment extends Fragment {
	private static final String TAG = TaskFragment.class.getSimpleName();

	public TaskFragmentActivity activity;
	public DashboardActivity dashboard;
	public FormActivity formActivity;
	
	private DummyTask mTask;
	private TestCreditialsTask mTaskLogin;
	private FetchLayoutsTask mTaskLayouts;
	private FetchDataTask mTaskData;
	private DAOFactory daoFactory;
	public TaskState state = INACTIVE;
	
	boolean isRunning = false;
	public int progress = 0;
	public int max = 0;

	@Override
	public void onAttach(Activity activity) {
		Log.i(TAG, "onAttach(Activity)");
		super.onAttach(activity);
		this.activity = (TaskFragmentActivity)activity;	
		
		if(activity instanceof DashboardActivity){
			dashboard = (DashboardActivity) activity;
		}
		
		if(activity instanceof FormActivity){
			formActivity = (FormActivity) activity;
		}
		
	}
	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate(Bundle)");
		super.onCreate(savedInstanceState);
		daoFactory = new DAOFactory(activity);
		setRetainInstance(true);
	}

	/**
	 * This method is <em>not</em> called when the Fragment is being retained across Activity instances.
	 * Voláno když je aktivita odstraněna.
	 */
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
		daoFactory.releaseHelper();
		cancel();
	}

	/*****************************/
	/***** TASK FRAGMENT API *****/
	/*****************************/

	/**
	 * Start the background task.
	 */
	public void start() {
		if (state != RUNNING) {
			mTask = new DummyTask(this);
			mTask.execute();
		}
	}
	
	public void startData(String url, String url2, String url3 , MenuItems menu,DAOFactory daoFactory) {
		if (state != RUNNING) {
			mTaskData = new FetchDataTask(this, menu,daoFactory);
			mTaskData.execute(url, url2, url3);
		}
	}
	
	public void startLogin(User user) {
		if (state != RUNNING) {
			mTaskLogin = new TestCreditialsTask(this);
			mTaskLogin.execute(user);
		}
	}
	
	public void startFetchLayouts(LayoutDialogAdapter adapter, MenuItems menu) {
		if (state != RUNNING) {
			mTaskLayouts = new FetchLayoutsTask(this, adapter, menu);
			mTaskLayouts.execute();
		}
	}

	/**
	 * Cancel the background task.
	 */
	public void cancel() {
		if (state == RUNNING) {
			mTask.cancel(false);
			mTask = null;
			// running = false;
		}
	}

	public DAOFactory getDaoFactory() {
		return daoFactory;
	}

	/**
	 * Hands over progress state change towards the activity.
	 * 
	 * @param state
	 *            new service state
	 * @param messageCode
	 *            android string identifier
	 */	
	protected void setState(final TaskState state) {
		this.state = state;
		activity.changeProgress(state, null);
	}

	protected void setState(final TaskState state, final int messageCode) {
		this.state = state;
		activity.changeProgress(state, activity.getString(messageCode));
	}

	protected void setState(final TaskState state, final int messageCode, final int progress) {
		this.state = state;
		activity.changeProgress(state, activity.getString(messageCode));
	}
	
	protected void setState(TaskState state, Throwable error) {

        String message = error.getMessage() == null ? activity.getString(R.string.error_connection) : error.getMessage();
        if (error instanceof RestClientException) {

            //error on client side
            if (error instanceof HttpClientErrorException) {
                HttpStatus status = ((HttpClientErrorException) error).getStatusCode();

                switch (status) {
                    case BAD_REQUEST:
                        message = activity.getString(R.string.error_http_400);
                        break;
                    case UNAUTHORIZED:
                        message = activity.getString(R.string.error_http_401);
                        break;
                    case FORBIDDEN:
                        message = activity.getString(R.string.error_http_403);
                        break;
                    case NOT_FOUND:
                        message = activity.getString(R.string.error_http_404);
                        break;
                    case METHOD_NOT_ALLOWED:
                        message = activity.getString(R.string.error_http_405);
                        break;
                    case REQUEST_TIMEOUT:
                        message = activity.getString(R.string.error_http_408);
                        break;
                }
                //error on server side
            } else if (error instanceof HttpServerErrorException) {
                HttpStatus status = ((HttpServerErrorException) error).getStatusCode();

                switch (status) {
                    case INTERNAL_SERVER_ERROR:
                        message = activity.getString(R.string.error_http_500) + "\n " + error.getMessage();
                        break;
                    case SERVICE_UNAVAILABLE:
                        message = activity.getString(R.string.error_http_503);
                        break;
                }
                //connection broke
            } else if (error instanceof ResourceAccessException) {
                message = activity.getString(R.string.error_ssl);
            } else {
                error = ((RestClientException) error).getRootCause();
                message = error == null ? activity.getString(R.string.error_connection) : error.getMessage();

                // attempt to recognize low-level connection errors
                if (message.contains("EHOSTUNREACH"))
                    message = activity.getString(R.string.error_host_unreach);
                else if (message.contains("ECONNREFUSED"))
                    message = activity.getString(R.string.error_host_con_refused);
                else if (message.contains("ETIMEDOUT"))
                    message = activity.getString(R.string.error_host_timeout);
            }

        }
        System.out.println(message);
        //display the error message
        activity.changeProgress(state, message);
    }
	
	public void createProgressBarHorizontal(final int max,  final int titleCode) {
		//this.max = max;
		//isRunning = true;
		activity.initProgressBar(max, activity.getString(titleCode), 0);
	}
	
	protected void setState(final int progress) {
		//this.progress = progress;
		activity.changeProgress(progress);
	}
	
	protected void setStateIncrement(final int diff) {
		//this.progress += progress;
		activity.incrementProgressBy(diff);
	}
	
	protected void resetProgressCounter(){
		//max = 0;
		//isRunning = false;
	}

	/************************/
	/***** LOGS & STUFF *****/
	/************************/

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated(Bundle)");
		super.onActivityCreated(savedInstanceState);
		/*if (savedInstanceState != null) {
            // Restore last state for checked position.
            //max = savedInstanceState.getInt("pokus", -1);
            max = savedInstanceState.getInt("max",  max);
            progress = savedInstanceState.getInt("progress",  progress);
            isRunning = savedInstanceState.getBoolean("isRunning",  isRunning);
            
            if(isRunning){
            	System.out.println();
            }
        }*/
		
	}

	@Override
	public void onStart() {
		Log.i(TAG, "onStart()");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.i(TAG, "onResume()");
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.i(TAG, "onPause()");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.i(TAG, "onStop()");
		super.onStop();
	}
	
	public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pokus",  1000);
        outState.putInt("max",  max);
        outState.putInt("progress",  progress);
        outState.putBoolean("isRunning",  isRunning);
    }
}