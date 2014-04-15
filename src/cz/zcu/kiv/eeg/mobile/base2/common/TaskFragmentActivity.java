package cz.zcu.kiv.eeg.mobile.base2.common;

import cz.zcu.kiv.eeg.mobile.base2.data.TaskState;
import cz.zcu.kiv.eeg.mobile.base2.ws.FetchLayoutsTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Activity with capability of recognizing Service state. If service was set and activity is recreated, progress dialog
 * is recreated as well.
 * 
 * @author Jaroslav Hošek
 */
public class TaskFragmentActivity extends FragmentActivity {

	private static final String TAG = TaskFragmentActivity.class.getSimpleName();

	/**
	 * Progress dialog informing of common service state.
	 */
	protected volatile ProgressDialog progressDialog;

	@Override
	protected void onResume() {
		// TODO obnovit stav progresu
		Log.i(TAG, "onResume()");
		super.onResume();
		if (progressDialog != null) {
			if (progressDialog.getProgress() < progressDialog.getMax()) {
				progressDialog.show();
			}
		}
	}

	public void initProgressBar(final int max, final String title) {
		Log.i(TAG, "init");
		final Context cx = this;
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				progressDialog = new ProgressDialog(cx);
				progressDialog.setTitle(title);
				progressDialog.setMax(max);
				progressDialog.setProgress(0);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(false);
				progressDialog.show();
			}
		});
	}

	public void incrementProgressBy(final int diff) {
		Log.i(TAG, "onProgressIncrement(" + diff + "%)");
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				if (progressDialog != null) {
					progressDialog.incrementProgressBy(diff);
				}

			}
		});
	}

	public void changeProgress(final int percent) {
		Log.i(TAG, "onProgressUpdate(" + percent + "%)");
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				progressDialog.setProgress(percent * progressDialog.getMax() / 100);
			}
		});
	}

	public void changeProgress(final TaskState state, final String message) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				try {
					Log.i(TAG, "onProgressUpdate(zaslana zprava)");
					switch (state) {
					case RUNNING:
						showProgressDialog(message);
						Log.i(TAG, "pokus " + progressDialog.isIndeterminate());
						break;
					case INACTIVE:
					case DONE:
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						break;
					case ERROR:
						showAlert(message);
					default:
						break;
					}
				} catch (Exception e) {
					// Log.w(TAG, e.getMessage());
					Log.e(TAG, e.getLocalizedMessage(), e);
				}
			}
		});
	}

	private void showProgressDialog(String message) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Working");
		progressDialog.setMessage(message);
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}

	/**
	 * Display alert dialog.
	 * 
	 * @param alert message
	 */
	public void showAlert(final String alert) {
		showAlert(alert, false);
	}

	/**
	 * Method for showing alert dialog with option, whether activity should be finished after confirmation.
	 * 
	 * @param alert alert message
	 * @param closeActivity close activity on confirmation
	 */
	public void showAlert(final String alert, final boolean closeActivity) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(alert).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				if (closeActivity) {
					finish();
				}
			}
		});
		builder.create().show();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause()");
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		super.onPause();
	}
}