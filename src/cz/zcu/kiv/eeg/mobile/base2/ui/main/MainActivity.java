package cz.zcu.kiv.eeg.mobile.base2.ui.main;

import static cz.zcu.kiv.eeg.mobile.base2.data.TaskState.*;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.R.id;
import cz.zcu.kiv.eeg.mobile.base2.R.layout;
import cz.zcu.kiv.eeg.mobile.base2.R.menu;
import cz.zcu.kiv.eeg.mobile.base2.data.TaskState;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.ViewBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItem;
import cz.zcu.kiv.eeg.mobile.base2.ws.TaskFragment;

public class MainActivity extends FragmentActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TaskFragment mTaskFragment; //
    private ProgressBar mProgressBar;
    private ProgressDialog progressDialog; // todo volatile
    private TextView mPercent;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	Log.i(TAG, "onCreate(Bundle)");
	super.onCreate(savedInstanceState);
		
	Form form = new Form();
	form.setType("OsobaPokus");

	// FormDAO personDao = new FormDAO(this);
	// personDao.saveOrUpdate(form);

	/*
	 * setContentView(R.layout.base_person_add);
	 * findViewById(R.id.person_name_label).setOnTouchListener(new
	 * MyTouchListener());
	 * findViewById(R.id.person_surname_label).setOnTouchListener(new
	 * MyTouchListener());
	 * findViewById(R.id.person_name_value).setOnTouchListener(new
	 * MyTouchListener());
	 * 
	 * findViewById(R.id.person_name_label).setOnDragListener(new
	 * MyDragListener());
	 * findViewById(R.id.person_surname_label).setOnDragListener(new
	 * MyDragListener());
	 * findViewById(R.id.person_name_value).setOnDragListener(new
	 * MyDragListener());
	 */

	setContentView(R.layout.activity_main);
	mProgressBar = (ProgressBar) findViewById(R.id.progress_horizontal);
	mPercent = (TextView) findViewById(R.id.percent_progress);
	mButton = (Button) findViewById(R.id.task_button);
	mButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		if (mTaskFragment.state == RUNNING) {
		    mTaskFragment.cancel();
		} else {
		    mTaskFragment.start(); // spusteni asynchtasku
		}
	    }
	});

	Button dashboardButton = (Button) findViewById(R.id.dashboardButton);
	dashboardButton.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		System.out.println("pokusnicek");
		openDashboard();
	    }
	});

	RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main_layout);
	/*LinearLayout lin = new ViewBuilder(this).getLinearLayout("Person-generated");

	RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
		ViewGroup.LayoutParams.WRAP_CONTENT, 
		ViewGroup.LayoutParams.WRAP_CONTENT);

	p.addRule(RelativeLayout.BELOW, R.id.data);
	layout.addView(lin, p);*/

	// asynchr. tasky
	FragmentManager fm = getSupportFragmentManager();
	mTaskFragment = (TaskFragment) fm.findFragmentByTag("task");

	if (mTaskFragment == null) {
	    mTaskFragment = new TaskFragment();
	    fm.beginTransaction().add(mTaskFragment, "task").commit();
	}

	if (mTaskFragment.state == RUNNING) {
	    mProgressBar.setProgress(mTaskFragment.progress); // asi ani nen�
	    // pot�ebapot�eba
	    showProgressDialog("");
	    mButton.setText("Cancel");
	} else {
	    mButton.setText("Start");
	}
    }

    // ToDo - do samostatn�ch t��d - bal�k com.mobile.editor
    /*
     * private final class MyTouchListener implements OnTouchListener { public
     * boolean onTouch(View view, MotionEvent motionEvent) { if
     * (motionEvent.getAction() == MotionEvent.ACTION_DOWN) { ClipData data =
     * ClipData.newPlainText("", ""); DragShadowBuilder shadowBuilder = new
     * View.DragShadowBuilder( view); view.startDrag(data, shadowBuilder, view,
     * 0); view.setVisibility(View.INVISIBLE); return true; } else { return
     * false; } } }
     * 
     * // ToDo - do samostatn�ch t��d editor class MyDragListener implements
     * OnDragListener { Drawable enterShape = getResources().getDrawable(
     * R.drawable.shape_droptarget); Drawable normalShape =
     * getResources().getDrawable(R.drawable.shape);
     * 
     * @Override public boolean onDrag(View viewB, DragEvent event) { int action
     * = event.getAction(); switch (event.getAction()) { case
     * DragEvent.ACTION_DRAG_STARTED: // Do nothing break; case
     * DragEvent.ACTION_DRAG_ENTERED: viewB.setBackgroundDrawable(enterShape);
     * // v.setBackgroundColor(50); break; case DragEvent.ACTION_DRAG_EXITED:
     * viewB.setBackgroundDrawable(normalShape); break; case
     * DragEvent.ACTION_DROP: View viewA = (View) event.getLocalState();
     * ViewGroup layout = (ViewGroup) viewA.getParent(); int indexA =
     * layout.indexOfChild(viewA); int indexB = layout.indexOfChild(viewB);
     * 
     * if (indexA > indexB) { layout.removeView(viewB);
     * layout.removeView(viewA); layout.addView(viewA, indexB);
     * layout.addView(viewB, indexA); } else { layout.removeView(viewA);
     * layout.removeView(viewB); layout.addView(viewB, indexA);
     * layout.addView(viewA, indexB);
     * 
     * }
     * 
     * viewA.setVisibility(View.VISIBLE); break; case
     * DragEvent.ACTION_DRAG_ENDED: viewB.setBackgroundDrawable(normalShape);
     * default: break; } return true; } }
     */

    public void changeProgress(final int percent) {
	Log.i(TAG, "onProgressUpdate(" + percent + "%)");
	mProgressBar.setProgress(percent * mProgressBar.getMax() / 100);
	progressDialog.setProgress(percent * progressDialog.getMax() / 100);
	mPercent.setText(percent + "%");
    }

    public void changeProgress(final TaskState state, String message) {
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
	    // showAlert(message);
	default:
	    break;
	}
    }

    private void showProgressDialog(String message) {
	// todo nezobraz� se na onresume - kdy� aplikaci po�lu do pozad� a pak
	// spust�m
	progressDialog = new ProgressDialog(this);
	progressDialog.setTitle("Working");
	progressDialog.setMessage(message);
	// progressDialog.setMax(100);
	// progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	// progressDialog.setProgress(0);
	progressDialog.setCancelable(false);
	progressDialog.setIndeterminate(true);
	progressDialog.show();
    }

    /************************/
    /***** OPTIONS MENU *****/
    /************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
    }

    /*
     * @TargetApi(Build.VERSION_CODES.HONEYCOMB)
     * 
     * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
     * (item.getItemId()) { case R.id.menu_trigger_config_change: // Simulate a
     * configuration change recreate(); return true; } return
     * super.onOptionsItemSelected(item); }
     */

    /************************/
    /***** LOGS & STUFF *****/
    /************************/

    @Override
    protected void onStart() {
	Log.i(TAG, "onStart()");
	super.onStart();
    }

    @Override
    protected void onResume() {
	Log.i(TAG, "onResume()");
	super.onResume();
    }

    @Override
    protected void onPause() {
	Log.i(TAG, "onPause()");
	if (progressDialog != null && progressDialog.isShowing()) {
	    progressDialog.dismiss();
	}
	super.onPause();
    }

    @Override
    protected void onStop() {
	Log.i(TAG, "onStop()");
	super.onStop();
    }

    @Override
    protected void onDestroy() {
	Log.i(TAG, "onDestroy()");
	super.onDestroy();
    }

    public void openDashboard() {
	startActivity(new Intent(this, DashboardActivity.class));
    }

}
