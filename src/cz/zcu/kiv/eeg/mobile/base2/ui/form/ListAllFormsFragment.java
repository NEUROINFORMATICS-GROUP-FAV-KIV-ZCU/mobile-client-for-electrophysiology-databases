package cz.zcu.kiv.eeg.mobile.base2.ui.form;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

import cz.zcu.kiv.eeg.mobile.base2.R;
import cz.zcu.kiv.eeg.mobile.base2.data.adapter.FormAdapter;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormRow;

public class ListAllFormsFragment extends ListFragment implements View.OnClickListener {

    private final static String TAG = ListAllFormsFragment.class.getSimpleName();
    private static FormAdapter adapter; 
    //private static String layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //layout = savedInstanceState.getString("layout");
        //setHasOptionsMenu(true);  // TODO povolit
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.form_list, container, false);       
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {   //TODO podívat se na web, protože co mám v google DOCS tak se adaptéry maj u fragmentu nastavovat v onActivityCreated
        setListAdapter(null);
        setListAdapter(getAdapter());   
        super.onViewCreated(view, savedInstanceState);
    }
  
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {
            case R.id.scenario_refresh:
                update();
                Log.d(TAG, "Refresh data button pressed");
                return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.empty:
                update();
                break;
        }
    }
    
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        showDetails(position);
        this.setSelection(position);
    }
    
    private void showDetails(int index) {       
        FormAdapter dataAdapter = getAdapter();
        boolean empty = dataAdapter == null || dataAdapter.isEmpty();
        // todo předávat ID datasetu  pro který chci získat detail (aktuálně id pozice)
        if (!empty) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), FormDetailsActivity.class);
            intent.putExtra("index", index);
            //intent.putExtra("data", dataAdapter.getItem(index));
            startActivity(intent);
        }
    }  
    
    private FormAdapter getAdapter() {
        if (adapter == null) {
        	//todo tady naplním adaptér z db - předám pouze arrayList
            adapter = new FormAdapter(getActivity(), R.layout.form_row, new ArrayList<FormRow>());
            adapter.add(new FormRow(1,"Jarda", "Neco", "Já"));
            adapter.add(new FormRow(1,"Pepa", "Taky Neco", "on"));
        }
        return adapter;
    }

    /**
     * If online, fetches available public scenarios.
     */
    private void update() {

        /*CommonActivity activity = (CommonActivity) getActivity();
        if (ConnectionUtils.isOnline(activity)) {
            new FetchScenarios(activity, getAdapter(), Values.SERVICE_QUALIFIER_ALL).execute();
        } else
            activity.showAlert(activity.getString(R.string.error_offline));*/
    }    
   
   /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.scenario_all_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);                     
    }*/

    
}
