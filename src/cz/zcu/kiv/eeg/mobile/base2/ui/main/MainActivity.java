package cz.zcu.kiv.eeg.mobile.base2.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.User;
import cz.zcu.kiv.eeg.mobile.base2.ui.settings.LoginActivity;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private DAOFactory daoFactory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate(Bundle)");
		super.onCreate(savedInstanceState);
		daoFactory = new DAOFactory(this);

		User user = daoFactory.getUserDAO().getUser();
		if (user == null) {
			Intent loginIntent = new Intent(this, LoginActivity.class);
			loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(loginIntent);
		} else {
			Intent mainIntent = new Intent(this, DashboardActivity.class);
			mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(mainIntent);
		}
		finish();

		/*
		 * findViewById(R.id.person_name_label).setOnTouchListener(new MyTouchListener());
		 * findViewById(R.id.person_surname_label).setOnTouchListener(new MyTouchListener());
		 * findViewById(R.id.person_name_value).setOnTouchListener(new MyTouchListener());
		 * 
		 * findViewById(R.id.person_name_label).setOnDragListener(new MyDragListener());
		 * findViewById(R.id.person_surname_label).setOnDragListener(new MyDragListener());
		 * findViewById(R.id.person_name_value).setOnDragListener(new MyDragListener());
		 */
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		daoFactory.releaseHelper();
	}

	// ToDo - do samostatn�ch t��d - bal�k com.mobile.editor
	/*
	 * private final class MyTouchListener implements OnTouchListener { public boolean onTouch(View view, MotionEvent
	 * motionEvent) { if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) { ClipData data =
	 * ClipData.newPlainText("", ""); DragShadowBuilder shadowBuilder = new View.DragShadowBuilder( view);
	 * view.startDrag(data, shadowBuilder, view, 0); view.setVisibility(View.INVISIBLE); return true; } else { return
	 * false; } } }
	 * 
	 * // ToDo - do samostatn�ch t��d editor class MyDragListener implements OnDragListener { Drawable enterShape =
	 * getResources().getDrawable( R.drawable.shape_droptarget); Drawable normalShape =
	 * getResources().getDrawable(R.drawable.shape);
	 * 
	 * @Override public boolean onDrag(View viewB, DragEvent event) { int action = event.getAction(); switch
	 * (event.getAction()) { case DragEvent.ACTION_DRAG_STARTED: // Do nothing break; case
	 * DragEvent.ACTION_DRAG_ENTERED: viewB.setBackgroundDrawable(enterShape); // v.setBackgroundColor(50); break; case
	 * DragEvent.ACTION_DRAG_EXITED: viewB.setBackgroundDrawable(normalShape); break; case DragEvent.ACTION_DROP: View
	 * viewA = (View) event.getLocalState(); ViewGroup layout = (ViewGroup) viewA.getParent(); int indexA =
	 * layout.indexOfChild(viewA); int indexB = layout.indexOfChild(viewB);
	 * 
	 * if (indexA > indexB) { layout.removeView(viewB); layout.removeView(viewA); layout.addView(viewA, indexB);
	 * layout.addView(viewB, indexA); } else { layout.removeView(viewA); layout.removeView(viewB); layout.addView(viewB,
	 * indexA); layout.addView(viewA, indexB);
	 * 
	 * }
	 * 
	 * viewA.setVisibility(View.VISIBLE); break; case DragEvent.ACTION_DRAG_ENDED:
	 * viewB.setBackgroundDrawable(normalShape); default: break; } return true; } }
	 */
}
