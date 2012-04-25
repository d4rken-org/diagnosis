package eu.thedarken.diagnosis;

import java.util.ArrayList;
import com.actionbarsherlock.app.SherlockFragment;
import eu.thedarken.diagnosis.InfoClass.AppTabInfo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DGapps extends SherlockFragment {
    private Context mContext;
    private SharedPreferences settings;
    private TableLayout apps_table;
    private Spinner sortmode;
    private DGdatabase db;
    
	private View mView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment and save it for the fragment to
		// use
		mView = inflater.inflate(R.layout.apps, container, false);
		return mView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getSherlockActivity();
        settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        db = DGdatabase.getInstance(mContext);
        apps_table = (TableLayout) mView.findViewById(R.id.apps_table);
       	sortmode = (Spinner) mView.findViewById(R.id.sortmode);

       	sortmode.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selected){
                	update();
                }
                
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
       		}
       	); 
       	
    }

    @Override
    public void onResume() {
    	super.onResume();
    	update();
    }
    
    private void update() {
    	new updateTask(this.getSherlockActivity(),sortmode.getSelectedItemPosition()).execute();
    }
    
    private class updateTask extends AsyncTask<String, Void, Boolean> {
    	private Activity mActivity;
        private ProgDialog dialog;
        private ArrayList <AppTabInfo> infos = null;
    	private int sortmode;
    	private boolean pull_apps;
        public updateTask(Activity a, int sm) {
        	mActivity = a;
        	sortmode = sm;
        	pull_apps = settings.getBoolean("general.database.doapps", false);
        }

        protected void onPreExecute() {
          	dialog = new ProgDialog(mActivity);
            dialog.setMessage(mActivity.getString(R.string.loading_data_please_wait));
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean ok) {
        	apps_table.removeAllViews();
            if(pull_apps) {
				TextView nodata = (TextView) mView.findViewById(R.id.nodata);
				nodata.setVisibility(View.GONE);
            	
        		apps_table.setVisibility(View.VISIBLE);
            	TableRow tr = new TableRow(mActivity);
            	tr.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            	tr.setGravity(Gravity.CENTER);
            	
                TextView t1 = new TextView(mActivity);
                t1.setText(mActivity.getString(R.string.seen) + " ");
                t1.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1));
                t1.setTextColor(Color.BLACK);
                t1.setSingleLine(true);

                TextView t2 = new TextView(mActivity);
                t2.setText(mActivity.getString(R.string.name));
                t2.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                t2.setTextColor(Color.BLACK);
                t2.setSingleLine(true);
                
                TextView t3 = new TextView(mActivity);
                t3.setText(mActivity.getString(R.string.cpu_percent));
                t3.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1));
                t3.setTextColor(Color.BLACK);
                t3.setSingleLine(true);
                t3.setPadding(5, 0, 0, 0);

                TextView t4 = new TextView(mActivity);
                t4.setText(mActivity.getString(R.string.ram_percent));
                t4.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1));
                t4.setTextColor(Color.BLACK);
                t4.setSingleLine(true);
                
                tr.addView(t1);
                tr.addView(t2);
                tr.addView(t3);
                tr.addView(t4);
                apps_table.addView(tr,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
                
                for(int i=0;i<infos.size() && i < 50;i++) {
                	TableRow r = new TableRow(mActivity);
                	r.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                	r.setGravity(Gravity.CENTER);
                	
                    TextView seen = new TextView(mActivity);
                    seen.setText(String.valueOf(infos.get(i).seen) + " ");
                    seen.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1));
                    seen.setTextColor(Color.BLACK);
                    seen.setSingleLine(true);
                    
                    TextView name = new TextView(mActivity);
                    name.setText(infos.get(i).command);
                    //if(infos.get(i).command.length()>29) name.setText(infos.get(i).command.substring(0, 29));
                    name.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    name.setTextColor(Color.BLACK);
                    name.setSingleLine(true);


                    TextView cpu = new TextView(mActivity);
                    cpu.setText(String.valueOf((float)Math.round(infos.get(i).avg_cpu*100)/100)+"% ");
                    cpu.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1));
                    cpu.setTextColor(Color.BLACK);
                    cpu.setSingleLine(true);
                    cpu.setPadding(5, 0, 0, 0);

                    TextView ram = new TextView(mActivity);
                    ram.setText(String.valueOf((float)Math.round(infos.get(i).avg_mem*100)/100) + "%");
                    ram.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1));
                    ram.setTextColor(Color.BLACK);
                    ram.setSingleLine(true);
                    
                    r.addView(seen);
                    r.addView(name);
                    r.addView(cpu);
                    r.addView(ram);
                    
                    apps_table.addView(r,new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
                }
            } else {
        		apps_table.setVisibility(View.GONE);
				TextView nodata = (TextView) mView.findViewById(R.id.nodata);
				nodata.setVisibility(View.VISIBLE);
            }


            dialog.dismiss();

        }
        
		@Override
		protected Boolean doInBackground(String... params) {
	    	infos = db.getAppTabInfo(sortmode,settings.getBoolean("general.database.hidesystem", false));
	    	if(infos == null)
	    		 pull_apps = false;
	    	return pull_apps;
		}
    }
}
