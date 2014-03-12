package cz.zcu.kiv.eeg.mobile.base2.ws;

import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.INACTIVE;
import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.RUNNING;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import cz.zcu.kiv.eeg.mobile.base2.data.TaskState;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.ui.main.MainActivity;

/**
 * This Fragment manages a single background task and retains itself across
 * configuration changes.
 */
public class TaskFragment extends Fragment {
	private static final String TAG = TaskFragment.class.getSimpleName();

	public MainActivity activity;
	private DummyTask mTask;
	private DAOFactory daoFactory;
	// private boolean running;
	public TaskState state = INACTIVE;
	public int progress = 0; // aktivita si naèítá pøi obnovení stavu

	@Override
	public void onAttach(Activity activity) {
		Log.i(TAG, "onAttach(Activity)");
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate(Bundle)");
		super.onCreate(savedInstanceState);
		daoFactory = new DAOFactory(activity);
		setRetainInstance(true);
	}

	/**
	 * This method is <em>not</em> called when the Fragment is being retained
	 * across Activity instances. Voláno až když je aktivita odstranìná
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
	protected void setState(final int progress) {
		this.progress = progress;
		activity.changeProgress(progress);
	}

	protected void setState(final TaskState state) {
		this.state = state;
		activity.changeProgress(state, null);
	}

	protected void setState(final TaskState state, final int messageCode) {
		this.state = state;
		activity.changeProgress(state, activity.getString(messageCode));
	}

	protected void setState(final TaskState state, final int messageCode,
			final int progress) {
		this.state = state;
		activity.changeProgress(state, activity.getString(messageCode));
	}

	/************************/
	/***** LOGS & STUFF *****/
	/************************/

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated(Bundle)");
		super.onActivityCreated(savedInstanceState);
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
}