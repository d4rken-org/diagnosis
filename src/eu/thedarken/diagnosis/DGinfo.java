package eu.thedarken.diagnosis;

import com.actionbarsherlock.app.SherlockFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class DGinfo extends SherlockFragment {
	private Context mContext;
	private SharedPreferences settings;
	private TextView db_size;
	private TextView db_status;
	private View mView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout and save it
		mView = inflater.inflate(R.layout.info, container, false);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = this.getSherlockActivity();
		settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		db_size = (TextView) mView.findViewById(R.id.db_size);
		db_status = (TextView) mView.findViewById(R.id.db_status);
		
		TextView welcome = (TextView) mView.findViewById(R.id.style_welcome);
		welcome.setText("Welcome to Diagnosis " + DGmain.versName + ", try one of these styles or visit the settings and create your own.");
	
		ArrayAdapter<CharSequence> stylesadapter = ArrayAdapter.createFromResource(mContext, R.array.stylelist, android.R.layout.simple_spinner_item );
		stylesadapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
		Spinner styles = (Spinner) mView.findViewById( R.id.stylespinner );
		styles.setAdapter( stylesadapter );
		styles.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
				int arraypos = parent.getSelectedItemPosition();
				Styles s = new Styles(mContext);
				switch(arraypos) {
					case 0:
//						s.initLines();
						break;
					case 1:
						s.setStyle1();
						break;
					case 2:
						s.setStyle2();
						break;
					case 3:
						s.setStyle3();
						break;
					case 4:
						s.setStyle4();
						break;
					case 5:
						s.setStyle5();
						break;
					case 6:
						s.setStyle6();
						break;
					case 7:
						s.setStyle7();
						break;
					case 8:
						s.setStyle8();
						break;
					case 9:
						s.setStyle9();
						break;
					case 10:
						s.setStyle10();
						break;
					case 11:
						s.setStyle11();
						break;
					case 12:
						s.setStyle12();
						break;
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	@Override
	public void onResume() {
        super.onResume();
		new startinfoTask(this.getSherlockActivity()).execute();
	}

	private class startinfoTask extends AsyncTask<String, Void, Boolean> {
		private StringBuilder db_size_sb = new StringBuilder();
		private Activity mActivity;
		private ProgDialog dialog;

		public startinfoTask(Activity a) {
			mActivity = a;
		}

		protected void onPreExecute() {
			dialog = new ProgDialog(mActivity);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
			dialog.updateMessage("Loading...");
		}

		@Override
		protected void onPostExecute(final Boolean ok) {

			db_size.setText(db_size_sb.toString());
			StringBuilder db_status_sb = new StringBuilder();
			int intervall = settings.getInt("general.intervall", 5);
			if (intervall > 1) {
				db_status_sb.append("Update interval is " + intervall + " seconds.\n");
			} else {
				db_status_sb.append("Update interval is " + intervall + " second.\n");
			}

			db_status_sb.append(settings.getInt("database.density", 6) + " data set(s) will be condensed into 1\n" + settings.getInt("database.cachesize", 24)
					+ " set(s) are cached before saving to database");

			db_status.setText(db_status_sb.toString());

			dialog.dismiss();

		}

		@Override
		protected Boolean doInBackground(String... params) {
			dialog.updateMessage("Loading database info");

			if (DGmain.db.exists()) {
				db_size_sb.append("Database size:" + Formatter.formatFileSize(mContext, DGmain.db.length()));
			} else {
				db_size_sb.append("No DB yet");
			}

			return true;
		}
	}
}