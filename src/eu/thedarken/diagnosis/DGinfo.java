package eu.thedarken.diagnosis;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DGinfo extends SherlockFragment {
	private Context mContext;
	private SharedPreferences settings;
	private TextView db_size;
	private TextView db_status;
	private View mView;
	private ArrayList<Button> styleButtons = new ArrayList<Button>();
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

		Button b = (Button) mView.findViewById(R.id.style1);
		b.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {Styles s = new Styles(mContext);s.setStyle1();}});
		styleButtons.add(b);
		b = (Button) mView.findViewById(R.id.style2);
		b.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {Styles s = new Styles(mContext);s.setStyle2();}});
		styleButtons.add(b);
		b = (Button) mView.findViewById(R.id.style3);
		b.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {Styles s = new Styles(mContext);s.setStyle3();}});
		styleButtons.add(b);
		b = (Button) mView.findViewById(R.id.style4);
		b.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {Styles s = new Styles(mContext);s.setStyle4();}});
		styleButtons.add(b);
		b = (Button) mView.findViewById(R.id.style5);
		b.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {Styles s = new Styles(mContext);s.setStyle5();}});
		styleButtons.add(b);
		b = (Button) mView.findViewById(R.id.style6);
		b.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {Styles s = new Styles(mContext);s.setStyle6();}});
		styleButtons.add(b);
		b = (Button) mView.findViewById(R.id.style7);
		b.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {Styles s = new Styles(mContext);s.setStyle7();}});
		styleButtons.add(b);
		b = (Button) mView.findViewById(R.id.style8);
		b.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {Styles s = new Styles(mContext);s.setStyle8();}});
		styleButtons.add(b);
		b = (Button) mView.findViewById(R.id.style9);
		b.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {Styles s = new Styles(mContext);s.setStyle9();}});
		styleButtons.add(b);
		b = (Button) mView.findViewById(R.id.style10);
		b.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {Styles s = new Styles(mContext);s.setStyle10();}});
		styleButtons.add(b);
		b = (Button) mView.findViewById(R.id.style11);
		b.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {Styles s = new Styles(mContext);s.setStyle11();}});
		styleButtons.add(b);
		b = (Button) mView.findViewById(R.id.style12);
		b.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {Styles s = new Styles(mContext);s.setStyle12();}});
		styleButtons.add(b);
		
		
	}
	
	@Override
	public void onResume() {
        super.onResume();
		new startinfoTask(mContext).execute();
	}

	private class startinfoTask extends AsyncTask<String, Void, Boolean> {
		private StringBuilder db_size_sb = new StringBuilder();
		private Context context;
		private ProgDialog dialog;

		public startinfoTask(Context c) {
			context = c;
		}

		protected void onPreExecute() {
			dialog = new ProgDialog(context);
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

			if (dialog.isShowing())
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