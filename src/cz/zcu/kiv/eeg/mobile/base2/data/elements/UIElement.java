package cz.zcu.kiv.eeg.mobile.base2.data.elements;

import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.ViewBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.editor.LayoutDragListener;
import cz.zcu.kiv.eeg.mobile.base2.data.editor.LayoutTouchListener;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.data.model.ViewNode;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormDetailsFragment;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

abstract public class UIElement {

	protected Context ctx;
	protected FormDetailsFragment fragment;  // mozna private
	protected ViewBuilder vb;			     // mozna private
	
	protected Field field;
	//protected Dataset dataset;
	protected LayoutProperty property;

	protected View element;
	protected TextView label;
	protected LinearLayout layout; // obaluje label a element
	
	private ActionMode mActionMode;
	protected Drawable background; 			// mozna private
	private Drawable editShape;

	DAOFactory daoFactory;
	
	public abstract void initData(Dataset dataset);
	public abstract String createOrUpdateData(Dataset dataset);

	public UIElement(Field field, LayoutProperty property, Context ctx, DAOFactory daoFactory, FormDetailsFragment fragment, ViewBuilder vb) {
		this.field = field;
		// /////this.dataset = dataset;
		this.property = property;
		this.ctx = ctx;
		this.daoFactory = daoFactory;
		this.fragment = fragment;
		this.vb = vb;
	}

	protected void createWrapLayout() {
		layout = new LinearLayout(ctx);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, property
				.getWeight()));
		layout.setPadding(10, 10, 0, 0);
		background = layout.getBackground();
		createLabel();
		layout.addView(element);
	}

	protected void createLabel() {
		if (property.getLabel() != null) {
			label = new TextView(ctx);
			label.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			label.setText(property.getLabel());
			layout.addView(label);
		}
	}

	protected void setTag() {
		element.setTag(R.id.NODE_ID, property.getIdNode()); // todo asi přejmenovat na ELEMENT_ID
		label.setTag(R.id.NODE_ID, property.getIdNode());
		layout.setTag(R.id.NODE_ID, property.getIdNode());
		layout.setTag(R.id.FIELD_ID, field.getId());
	}

	public void getData() {

	}

	public void setDiffWeight(int weight) {
		int newWeight = property.getWeight() - weight;
		// property.setWeight(newWeight);
		layout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, newWeight));
	}

	public View getWrapLayout() {
		return layout;
	}
		
	
	
	// drag and drop pro vyplňované pole
	public void setEditor() {
		Drawable dragShape = ctx.getResources().getDrawable(R.drawable.shape);

		if (field.getType().equalsIgnoreCase(Values.FORM)) {
			RelativeLayout parent = (RelativeLayout) label.getParent();
			parent.setOnTouchListener(new LayoutTouchListener());
			parent.setOnDragListener(new LayoutDragListener(ctx, vb.elements));
		} else {
			element.setOnTouchListener(new LayoutTouchListener());
			element.setOnDragListener(new LayoutDragListener(ctx,vb.elements));
			label.setOnTouchListener(new LayoutTouchListener());
			label.setOnDragListener(new LayoutDragListener(ctx,vb.elements));
		}

		layout.setBackgroundDrawable(dragShape);
	}

	public void removeEditor() {
		element.setOnTouchListener(null);
		element.setOnDragListener(null);
		label.setOnTouchListener(null);
		label.setOnDragListener(null);
		layout.setBackgroundDrawable(background);
	}

	public void setLocalEdit(final ViewBuilder vb, final Activity activity) {
		this.vb = vb;
		editShape = ctx.getResources().getDrawable(R.drawable.shape_edit);
		final Drawable touchShape = ctx.getResources().getDrawable(R.drawable.shape_droptarget);
		layout.setBackgroundDrawable(editShape);

		// disable editable
		element.setFocusable(false);
		element.setFocusableInTouchMode(false);
		element.setClickable(false);

		if (element instanceof EditText) {
			element.setOnLongClickListener(new View.OnLongClickListener() {
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
			label.setOnLongClickListener(new View.OnLongClickListener() {
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
		} else {
			// node.setOnLongClickListener(new View.OnLongClickListener() {
			layout.setOnLongClickListener(new View.OnLongClickListener() {
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

	}

	public void removeLocalEdit() {
		layout.setBackgroundDrawable(background);
		element.setOnLongClickListener(null);
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		// Called when the action mode is created; startActionMode() was called
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.form_local_edit_menu, menu);

			/*
			 * MenuItem test = menu.findItem(R.id.form_remove_label); View v = test.getActionView(); if (v instanceof
			 * TextView) { ((TextView) v).setText(R.string.remove); }
			 */

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// layout.setBackgroundDrawable(editShape);
			switch (item.getItemId()) {
			case R.id.form_remove_field:
				vb.removeFieldFromForm(property.getIdNode());
				mode.finish(); // Action picked, so close the CAB
				return true;
			case R.id.form_edit_field:
				fragment.editField(field.getId());
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
	
	public void setWeight(int weight) {
		property.setWeight(weight);
	}
	
	public String getFieldName() {
		return field.getName();
	}
	
	public Field getField() {
		return field;
	}
	
	public LayoutProperty getProperty() {
		return property;
	}
}
