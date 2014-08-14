package cz.zcu.kiv.eeg.mobile.base2.data.elements;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.Values;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.builders.ViewBuilder;
import cz.zcu.kiv.eeg.mobile.base2.data.factories.DAOFactory;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Data;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Dataset;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Field;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormRow;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;
import cz.zcu.kiv.eeg.mobile.base2.data.model.LayoutProperty;
import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormDetailsActivity;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.FormDetailsFragment;
import cz.zcu.kiv.eeg.mobile.base2.ui.form.ListAllFormsFragment;

public class UIForm extends UIElement {
	protected LinearLayout formLayout;
	protected Layout subLayout;	

	public FormAdapter adapter;
	//vybrané řádky z podformuláře
	private List<FormRow> selectedRow = new ArrayList<FormRow>();

	public UIForm(Field field, LayoutProperty property, DAOFactory daoFactory, Context ctx,
			FormDetailsFragment fragment, ViewBuilder vb) {
		super(field, property, ctx, daoFactory, fragment, vb);
		createForm();
		createWrapLayout();
		setTag();
	}

	private void createForm() {
		formLayout = new LinearLayout(ctx);
		formLayout.setOrientation(LinearLayout.VERTICAL);
		formLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		element = formLayout;
	}

	@Override
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

	@Override
	protected void createLabel() {
		if (property.getLabel() != null) {
			label = new TextView(ctx);
			label.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			label.setText(property.getLabel());
			label.setTag(R.id.NODE_ID, property.getIdNode());

			RelativeLayout labelLayout = new RelativeLayout(ctx);
			labelLayout.setTag(R.id.NODE_ID, property.getIdNode());

			label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			label.setId(1);
			label.setPadding(0, 0, 5, 5);
			label.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
			label.setBackgroundResource(R.color.edit_blue);
			RelativeLayout.LayoutParams label_param = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			label_param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			label_param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			label_param.addRule(RelativeLayout.ALIGN_PARENT_TOP);

			Button button = new Button(ctx);
			button.setPadding(0, 0, 0, 0);
			button.setText("+");
			RelativeLayout.LayoutParams button_param = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			button_param.addRule(RelativeLayout.ALIGN_BOTTOM, label.getId());
			button_param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			button_param.addRule(RelativeLayout.ALIGN_PARENT_TOP);

			subLayout = vb.getDaoFactory().getLayoutDAO().getLayout(property.getSubLayout().getName());		

			if(vb.getFormMode() != Values.FORM_EDIT_LAYOUT){
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {					
						selectSubformDialog();					
					}
				});
			}
			
			labelLayout.addView(label, label_param);
			labelLayout.addView(button, button_param);
			layout.addView(labelLayout);
		}
	}

	// @Override
	public void initData(Dataset dataset) {
		List<Data> dataList = daoFactory.getDataDAO().getAllData(dataset.getId(), field.getId());
		adapter = ListAllFormsFragment.getStaticAdapter((Activity) ctx, vb.getWorkspace(), subLayout, daoFactory);
		
		selectedRow = new ArrayList<FormRow>();
		fillRows();
		
		for(Data data :dataList){		
			selectedRow.add(adapter.getItem(adapter.getDatasetPosition(Integer.parseInt(data.getData()))));		
		}
		
		fillRows();
	}
	
	public void removeData(Dataset dataset) {			
		for(FormRow row : selectedRow){		
			selectedRow.remove(row);	
		}	
		fillRows();
	}

	public String createOrUpdateData(Dataset dataset) {
		String error = "";
		// již uloženo v DB
		List<Data> dataList = daoFactory.getDataDAO().getAllData(dataset.getId(), field.getId());
		
		//pokud označený řádek je v DB(dataList), tak ho již nebudu přidávat
		for(FormRow row : selectedRow){
			boolean isFill = false;
			for(Data data : dataList){				
				if(data.getData().equalsIgnoreCase(Integer.toString(row.getId()))){
					dataList.remove(data);
					isFill = true;
					break;
				}
			}
			if(!isFill){
				daoFactory.getDataDAO().create(dataset, field, Integer.toString(row.getId()));
			}
		}
		
		//odstranění položek z podformuláře (co zbylo)
		for(Data data : dataList){		
			daoFactory.getDataDAO().delete(data.getId());
		}
		return error;
	}

	public void selectSubformDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
		final LayoutInflater inflater = fragment.getActivity().getLayoutInflater();

		View dialogView = inflater.inflate(R.layout.base_list, null, false);
		final ListView listView = (ListView) dialogView.findViewById(android.R.id.list);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setEmptyView(dialogView.findViewById(android.R.id.empty));

		if(adapter == null){
			adapter = ListAllFormsFragment.getStaticAdapter((Activity) ctx, vb.getWorkspace(), subLayout, daoFactory);
		}		
		listView.setAdapter(adapter);

		dialog.setTitle(property.getLabel());
		dialog.setView(dialogView);
		dialog.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		dialog.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int len = listView.getCount();
				SparseBooleanArray checked = listView.getCheckedItemPositions();
				selectedRow.clear();

				// find out selected items
				for (int i = 0; i < len; i++) {
					if (checked.get(i)) {
						selectedRow.add(adapter.getItem(i));
					}
				}
				fillRows();
			}
		});

		dialog.setNeutralButton(R.string.dialog_button_new_item, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {											
				int datasetId = fragment.saveData();
				if (datasetId == -1) {
					final AlertDialog.Builder builder = new AlertDialog.Builder(vb.fragment.getActivity());
					builder.setMessage(vb.error).setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									vb.error = "";
									dialog.cancel();

								}
							});
					builder.create().show();
				}else{									
					Intent intentEdit = new Intent(ctx, FormDetailsActivity.class);
					intentEdit.putExtra(Dataset.DATASET_PARENT_ID, datasetId);
					intentEdit.putExtra(Field.FIELD_ID, field.getId());
					intentEdit.putExtra(Values.MENU_ITEM_ID, vb.getMenu().getId());
					intentEdit.putExtra(Layout.LAYOUT_ID, subLayout.getName());	
					intentEdit.putExtra(Form.FORM_MODE, Values.FORM_NEW_SUBFORM);
					vb.fragment.startActivityForResult(intentEdit, Values.FORM_NEW_SUBFORM);
				}	
			}
		});

		// reselect previously selected item
		for (FormRow hw : selectedRow)
			for (int i = 0; i < adapter.getCount(); i++) {
				if (adapter.getItem(i).getId() == hw.getId()) {
					listView.setItemChecked(i, true);
				}
			}
		dialog.show();
	}

	public void fillRows() {
		if (selectedRow != null ) {
			// create and inflate row by row
			final LayoutInflater inflater = getLayoutInflater(formLayout);

			// tvořim seznam všech (co zbyde v tomhle poli tak to odstranim)
			ArrayList<Integer> remove = new ArrayList<Integer>();
			for (int i = 0; i < formLayout.getChildCount(); i++) {
				View view = formLayout.getChildAt(i);
				remove.add(view.getId());
			}

			for (final FormRow record : selectedRow) {
				View row = inflater.inflate(R.layout.form_row, formLayout, false);

				TextView id = (TextView) row.findViewById(R.id.rowId);
				TextView name = (TextView) row.findViewById(R.id.rowName);
				TextView description = (TextView) row.findViewById(R.id.rowDescription);
				TextView mine = (TextView) row.findViewById(R.id.rowMime);

				if (id != null) {
					id.setText(Integer.toString(record.getId()));
				}
				if (name != null) {
					name.setText(record.getName());
				}
				if (description != null) {
					description.setText(record.getDescription());
				}

				row.setId(record.getId());

				// every row has its detail dialog
				row.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {						
						int datasetId = vb.saveFormData(vb.getDataset().getRecordId());
						if (datasetId == -1) {
							final AlertDialog.Builder builder = new AlertDialog.Builder(vb.fragment.getActivity());
							builder.setMessage(vb.error).setCancelable(false)
									.setPositiveButton("OK", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int id) {
											vb.error = "";
											dialog.cancel();

										}
									});
							builder.create().show();
						}else{
							Intent intentEdit = new Intent(ctx, FormDetailsActivity.class);
							intentEdit.putExtra(Dataset.DATASET_RECORD_ID, record.getId());
							intentEdit.putExtra(Values.MENU_ITEM_ID, vb.getMenu().getId());
							intentEdit.putExtra(Layout.LAYOUT_ID, subLayout.getName());	
							intentEdit.putExtra(Form.FORM_MODE, Values.FORM_EDIT_SUBFORM);
							vb.fragment.startActivityForResult(intentEdit, Values.FORM_EDIT_SUBFORM);
						}				
					}
				});

				// hledání jestli tam už není
				boolean isFill = false;
				for (int i = 0; i < formLayout.getChildCount(); i++) {
					View view = formLayout.getChildAt(i);
					if (view.getId() == record.getId()) {
						isFill = true;					
						remove.remove((Integer) view.getId());
					}
				}

				// pokud ne tak ho přídám
				if (isFill == false) {
					row.setBackgroundResource(R.drawable.list_selector);
					formLayout.addView(row);
				}
			}

			// odstraněné polí co tam nemaj být
			for (Integer r : remove) {
				for (int i = 0; i < formLayout.getChildCount(); i++) {
					View view = formLayout.getChildAt(i);
					if (view.getId() == r.intValue()) {

						formLayout.removeView(view);
					}
				}
			}
		} else {
			// setNoRecord(viewGroup);
		}
	}

	private void setNoRecord(ViewGroup viewGroup) {
		// inflate information, that no record is available
		TextView row = new TextView(viewGroup.getContext());
		row.setText("Zadné data");
		viewGroup.addView(row);
	}

	private LayoutInflater getLayoutInflater(ViewGroup viewGroup) {
		return (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getIdNode() {
		return super.getIdNode();
	}

}
