package cz.zcu.kiv.eeg.mobile.base2.data.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.SpinnerAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.ViewBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.editor.LayoutDragListener;
import cz.zcu.kiv.eeg.mobile.base2.data.editor.LayoutTouchListener;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
public class ViewNode {
	private Context ctx;
	private Field field;
	private LayoutProperty property;
	private View node;
	private TextView label;
	private LinearLayout layout; // obaluje label a node
	private Drawable background;
	private Drawable editShape;
	private ActionMode mActionMode;
	private ViewBuilder vb;

	public ViewNode(View node, Field field, LayoutProperty property, Context ctx) {
		this.ctx = ctx;
		this.field = field;
		this.property = property;
		this.node = node;
		this.node.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		layout = new LinearLayout(ctx);
		layout.setTag(R.id.NODE_ID, property.getIdNode());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, property
				.getWeight()));
		layout.setPadding(10, 10, 0, 0);
		background = layout.getBackground();

		if (property.getLabel() != null) {
			label = new TextView(ctx);
			label.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			label.setText(property.getLabel());
			label.setTag(R.id.NODE_ID, property.getIdNode());
			layout.addView(label);
		}
		layout.addView(node);
	}

	public String getData() {
		if (node instanceof TextView) {
			return ((TextView) node).getText().toString();
		} else if ((node instanceof Spinner)) {
			return (String) ((Spinner) node).getSelectedItem();
		} else if (node instanceof LinearLayout) {

		}
		/*
		 * else if (view instanceof TextView) { TextView textView = (TextView) view; // do what you want with textView }
		 */

		return null;
	}

	public void setData(String data) {
		if (node instanceof TextView) {
			((TextView) node).setText(data);
		} else if ((node instanceof Spinner)) {
			// zobrazení vybraných dat u spinneru
			Spinner spinner = ((Spinner) node);
			SpinnerAdapter adapter = (SpinnerAdapter) spinner.getAdapter();
			for (int i = 0; i < adapter.getCount(); i++) {
				if (data.equalsIgnoreCase(adapter.getItem(i))) {
					((Spinner) node).setSelection(i);
				}
			}
		}
		if (node instanceof LinearLayout) {
		}
	}

	public void setDiffWeight(int weight) {
		int newWeight = property.getWeight() - weight;
		//property.setWeight(newWeight);
		layout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, newWeight));
	}

	public View getWrapNode() {
		return layout;
	}

	// drag and drop pro vyplňované pole
	public void setEditor(SparseArray<ViewNode> nodes) {
		//background = layout.getBackground(); přesunuto do konstruktoru
		Drawable dragShape = ctx.getResources().getDrawable(R.drawable.shape);

		node.setOnTouchListener(new LayoutTouchListener());
		node.setOnDragListener(new LayoutDragListener(ctx, nodes));
		label.setOnTouchListener(new LayoutTouchListener());
		label.setOnDragListener(new LayoutDragListener(ctx, nodes));

		layout.setBackgroundDrawable(dragShape);
	}

	public void removeEditor() {
		node.setOnTouchListener(null);
		node.setOnDragListener(null);
		label.setOnTouchListener(null);
		label.setOnDragListener(null);
		layout.setBackgroundDrawable(background);
	}

	public void setLocalEdit(final ViewBuilder vb, final Activity activity) {
		this.vb = vb;
		editShape = ctx.getResources().getDrawable(R.drawable.shape_edit);
		final Drawable touchShape = ctx.getResources().getDrawable(R.drawable.shape_droptarget);	
		layout.setBackgroundDrawable(editShape);
			
		node.setOnLongClickListener(new View.OnLongClickListener() {
			// Called when the user long-clicks on someView
			public boolean onLongClick(View view) {
				if (mActionMode != null) {
					return false;
				}
				// Start the CAB using the ActionMode.Callback defined above
				mActionMode = activity.startActionMode(mActionModeCallback);
				layout.setBackgroundDrawable(touchShape);
				view.setSelected(true);
				return true;
			}
		});
	}
	
	public void removeLocalEdit() {
		layout.setBackgroundDrawable(background);
		node.setOnLongClickListener(null);
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		// Called when the action mode is created; startActionMode() was called
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.form_local_edit_menu, menu);
			
			MenuItem test = menu.findItem( R.id.form_sracka );
			View v = test.getActionView();
			if( v instanceof TextView )
			{
				((TextView)v).setText(R.string.remove);			
			}			
			
			return true;
		}
		
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {		
			return false; // Return false if nothing is done
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			//layout.setBackgroundDrawable(editShape);
			switch (item.getItemId()) {
			case R.id.form_remove_field:										
				vb.removeFieldFromForm(property.getIdNode());
				mode.finish(); // Action picked, so close the CAB
				return true;
			default:
				return false;
			}
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {		
			mActionMode = null;
			layout.setBackgroundDrawable(editShape);
		}
	};

	public View getNode() {
		return node;
	}

	public LinearLayout getLayout() {
		return layout;
	}

	public int getIdNode() {
		return property.getIdNode();
	}

	public void setIdNode(int idNode) {
		property.setIdNode(idNode);
	}

	public int getIdTop() {
		return property.getIdTop();
	}

	public void setIdTop(int idTop) {
		property.setIdTop(idTop);
	}

	public int getIdBottom() {
		return property.getIdBottom();
	}

	public void setIdBottom(int idBottom) {
		property.setIdBottom(idBottom);
	}

	public int getIdLeft() {
		return property.getIdLeft();
	}

	public void setIdLeft(int idLeft) {
		property.setIdLeft(idLeft);
	}

	public int getIdRight() {
		return property.getIdRight();
	}

	public void setIdRight(int idRight) {
		property.setIdRight(idRight);
	}

	public String getName() {
		return field.getName();
	}

	public LayoutProperty getProperty() {
		return property;
	}

	public void setProperty(LayoutProperty property) {
		this.property = property;
	}

	public int getFieldId() {
		return field.getId();
	}
	
	public void setWeight(int weight){
		property.setWeight(weight);
	}

	public Field getField() { // do4asn0
		return field;
	}
}