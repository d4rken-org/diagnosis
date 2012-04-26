package eu.thedarken.diagnosis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DGmain extends SherlockFragmentActivity {
	private static Context mContext;
	private Intent service;
	public static String versName = "";
	public static int versCode = 0;
	private final static int DB_DELETE_VERSION = 26;
	public static String BUSYBOX = "";
	public static String BUSYBOX_VERSION = "";
	private SharedPreferences settings;
	private SharedPreferences.Editor prefEditor;
	private final String TAG = "eu.thedarken.diagnosis.DGmain";
	public static File db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.main);

		service = new Intent(mContext, DGoverlay.class);
		settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		prefEditor = settings.edit();

		PreferenceManager.setDefaultValues(mContext, R.xml.preferences, false);

		BUSYBOX = mContext.getFilesDir() + "/busybox";

		new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/eu.thedarken.diagnosis/databases/").mkdirs();
		db = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/eu.thedarken.diagnosis/databases/database.db");

		prefEditor.putString("BUSYBOX", BUSYBOX);
		prefEditor.commit();

		new setupTask(this).execute();

		try {
			versCode = mContext.getPackageManager().getPackageInfo("eu.thedarken.diagnosis", 0).versionCode;
			versName = mContext.getPackageManager().getPackageInfo("eu.thedarken.diagnosis", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			versCode = 0;
			versName = "";
		}
		Log.d(TAG, "VersionName: " + DGmain.versName);
		Log.d(TAG, "VersionCode: " + DGmain.versCode);

		// setup action bar for tabs
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab tab = actionBar.newTab().setText("info").setTabListener(new TabListener<DGinfo>(this, "Info", DGinfo.class));
		actionBar.addTab(tab);

		tab = actionBar.newTab().setText("stats").setTabListener(new TabListener<DGstats>(this, "Stats", DGstats.class));
		actionBar.addTab(tab);

		tab = actionBar.newTab().setText("apps").setTabListener(new TabListener<DGapps>(this, "apps", DGapps.class));
		actionBar.addTab(tab);


	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (DGmain.checkPro(mContext)) {
			getSupportActionBar().setTitle(mContext.getString(R.string.pro));
		} else {
			getSupportActionBar().setTitle("");
		}
		
		if(settings.getInt("news.shown", 0) < versCode) {
			prefEditor.putInt("news.shown", versCode);
			prefEditor.commit();
			showMyDialog(Dialogs.NEWS);
		}
	}

	public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		/**
		 * Constructor used each time a new tab is created.
		 * 
		 * @param activity
		 *            The host Activity, used to instantiate the fragment
		 * @param tag
		 *            The identifier tag for the fragment
		 * @param clz
		 *            The fragment's Class, used to instantiate the fragment
		 */
		public TabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		/* The following are each of the ActionBar.TabListener callbacks */

		public void onTabSelected(Tab tab, FragmentTransaction ignoredft) {
			FragmentManager fragMgr = ((FragmentActivity) mActivity).getSupportFragmentManager();
			FragmentTransaction ft = fragMgr.beginTransaction();

			// Check if the fragment is already initialized
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				// If it exists, simply attach it in order to show it
				ft.attach(mFragment);
			}
			ft.commit();
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				// Detach the fragment, because another one is being attached
				ft.detach(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// User selected the already selected tab. Usually do nothing.
		}
	}

	private class setupTask extends AsyncTask<String, Void, Boolean> {
		private Activity mActivity;
		private ProgDialog dialog;

		public setupTask(Activity a) {
			mActivity = a;
		}

		protected void onPreExecute() {
			dialog = new ProgDialog(mActivity);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
			dialog.updateMessage(mActivity.getString(R.string.loading));
		}

		@Override
		protected void onPostExecute(final Boolean ok) {
			dialog.dismiss();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			dialog.updateMessage(mActivity.getString(R.string.copying_busybox));
			
			CopyAssets();

			dialog.updateMessage(mActivity.getString(R.string.getting_busybox_version));
			
			BUSYBOX_VERSION = getBusyboxVersion();
			if(BUSYBOX_VERSION.length() == 0) {
				dialog.updateMessage(mActivity.getString(R.string.startup_error));
				showMyDialog(Dialogs.BUSYBOX_ERROR);
			}

			if(settings.getInt("dbversion", 0) < DB_DELETE_VERSION && db.exists()) {
				if (db.delete()) {
					dialog.updateMessage(mActivity.getString(R.string.db_deletion_successfull));
					Log.d(TAG, mActivity.getString(R.string.db_deletion_successfull));
					prefEditor.putInt("dbversion", DGmain.versCode);
					prefEditor.commit();
				} else {
					dialog.updateMessage(mActivity.getString(R.string.could_not_delete_db));
					Log.d(TAG, mActivity.getString(R.string.could_not_delete_db));
					showMyDialog(Dialogs.REINSTALL);
				}
				showMyDialog(Dialogs.DATABASE_REMOVAL);
			} else {
				prefEditor.putInt("dbversion", DGmain.versCode);
				prefEditor.commit();
			}

			Styles s = new Styles(mContext);
			s.initLines();
			
			return true;
		}
	}

	private class serviceTask extends AsyncTask<String, Void, Boolean> {
		private Activity mActivity;
		private ProgDialog dialog;

		public serviceTask(Activity a) {
			mActivity = a;
		}

		protected void onPreExecute() {
			dialog = new ProgDialog(mActivity);
			dialog.setProgressStyle(DGoverlay.isRunning ? ProgressDialog.STYLE_SPINNER : ProgressDialog.STYLE_HORIZONTAL);
			dialog.updateMessage(DGoverlay.isRunning ? mActivity.getString(R.string.stopping_service) : mActivity.getString(R.string.starting_service));
			dialog.show();

		}

		@Override
		protected void onPostExecute(final Boolean ok) {
			invalidateOptionsMenu();
			dialog.dismiss();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				if (DGoverlay.isRunning) {
					DGoverlay.haltoverlay = true;
					while (DGoverlay.isRunning)
						Thread.sleep(25);
				} else {

					dialog.updateMessage(mActivity.getString(R.string.cleaning_old_database_entries));
					DGdatabase db_object = DGdatabase.getInstance(mContext.getApplicationContext());

					dialog.setMax(db_object.getTableSize());
					db_object.clean((long) (settings.getInt("database.agelimit", 48) * 3600000), dialog);

					getApplication().startService(service);
					while (!DGoverlay.isRunning)
						Thread.sleep(25);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	private Boolean CopyAssets() {
		if (!new File(mContext.getFilesDir() + "/busybox").exists()) {
			AssetManager am = mContext.getAssets();
			try {
				String fileName = "busybox";
				InputStream in = am.open("busybox");
				FileOutputStream f;
				f = mContext.openFileOutput(fileName, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
				byte[] buffer = new byte[1024];
				int len1 = 0;
				while ((len1 = in.read(buffer)) > 0) {
					f.write(buffer, 0, len1);
				}
				f.close();
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG, "busybox creation failed.");
				return false;
			}
			Log.d(TAG, "busybox has been successfully created.");
			setNonRootBusyBox();
			return true;
		} else {
			Log.d(TAG, "busybox found.");
		}
		return true;
	}

	private void setNonRootBusyBox() {
		Cmd c = new Cmd();
		c.addCommand("chmod 777 " + DGmain.BUSYBOX + "\n");
		c.execute();
		if(c.getExitCode() == 0) {
			Log.d(TAG, "Rights for non root busybox successfully set.");
		} else {
			Log.d(TAG, "Error when trying to set rights for non rooted busybox.");
		}
	}

	private String getBusyboxVersion() {
		Cmd c = new Cmd();
		c.addCommand(DGmain.BUSYBOX + " | " + DGmain.BUSYBOX + " head -n1" + "\n");
		c.setTimeout(5000);
		c.execute();
		if (c.getOutput().size()>0 && c.getOutput().get(0).length() > 20) {
			String vers = c.getOutput().get(0);
			vers = (String) vers.subSequence(0, 21);
			Log.d(TAG, "Busybox version: " + vers);
			return vers;
		} else {
			return "";
		}
	}

	public static boolean checkPro(Context useContext) {
		Context diagnosispro = null;
		try {
			diagnosispro = useContext.createPackageContext("eu.thedarken.diagnosis.pro", 0);
		} catch (NameNotFoundException e) {
			return false;
		}
		if (diagnosispro != null) {
			if (useContext.getPackageManager().checkSignatures(useContext.getPackageName(), diagnosispro.getPackageName()) == PackageManager.SIGNATURE_MATCH) {
				return true;
			}
		}
		return false;
	}

	private void showChangelog() {
		Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.changelog);
		dialog.setTitle(mContext.getString(R.string.diagnosis_changelog));
		TextView text = (TextView) dialog.findViewById(R.id.ChangelogTextView);
		text.setTextSize(13);
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(this.getAssets().open("changelog.txt"));

			BufferedReader br = new BufferedReader(reader);
			StringBuilder buffer = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				buffer.append(line).append('\n');
			}
			text.setText(buffer.toString());
			reader.close();
		} catch (IOException e) {
			Log.d(TAG, "Error while reading changelog.txt");
			e.printStackTrace();
		}
		dialog.show();
	}

	private void showAbout() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.aboutbox);
		dialog.setTitle("Diagnosis " + versName+"("+versCode+")");
		Button xda = (Button) dialog.findViewById(R.id.xda);
		xda.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://forum.xda-developers.com/showthread.php?t=1411074"));
				startActivity(browserIntent);
			}
		});
		Button email = (Button) dialog.findViewById(R.id.email);
		email.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createSupportEmail();
			}
		});

		TextView text = (TextView) dialog.findViewById(R.id.HelpTextView);
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(this.getAssets().open("about.txt"));
			BufferedReader br = new BufferedReader(reader);
			String line = null;
			while ((line = br.readLine()) != null) {
				text.append(line + "\n");
			}
			reader.close();
		} catch (IOException e) {
			Log.d(TAG, "Error while reading about file");
			e.printStackTrace();
		}
		dialog.show();
	}

	private void createSupportEmail() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@thedarken.eu" });
		StringBuilder version = new StringBuilder();
		try {
			version.append(mContext.getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
			version.append("(");
			version.append(mContext.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
			version.append(")");
		} catch (NameNotFoundException e1) {
			Log.d(mContext.getPackageName(), "Error while getting version");
			e1.printStackTrace();
		}

		String subject = "[Diagnosis] Question/Request";
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(android.content.Intent.EXTRA_TEXT, "\n\n" +

		"Send from inside the app.\n\n" + "Debug Information:\n" + "(This is anonymous and only tells me your device + firmware version.)\n"
				+ "Diagnosis Version: " + version.toString() + "\n" + "FINGERPRINT: " + android.os.Build.FINGERPRINT + "\n");
		startActivity(Intent.createChooser(intent, ""));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		if(DGoverlay.isRunning) {
			menu.findItem(R.id.starttracking).setTitle(mContext.getString(R.string.stop_tracking));
		} else {
			menu.findItem(R.id.starttracking).setTitle(mContext.getString(R.string.start_tracking));
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			showMyDialog(Dialogs.NEWS);
			break;
		case R.id.starttracking:
			new serviceTask(this).execute();
			break;
		case R.id.settings:
			Intent startPreferencesActivity = new Intent(this, DGsettings.class);
			this.startActivity(startPreferencesActivity);
			break;
		case R.id.changelog:
			showChangelog();
			break;
		case R.id.about:
			showAbout();
			break;
		}
		return true;
	}

	public void showMyDialog(int type) {
		FragmentManager ft = getSupportFragmentManager();
		DialogFragment newFragment = Dialogs.newInstance(type);
		newFragment.show(ft, "dialog");
	}
	
	public static class Dialogs extends DialogFragment {
		final static int BUSYBOX_ERROR = 0;
		final static int DATABASE_REMOVAL = 1;
		final static int REINSTALL = 2;
		final static int NEWS = 3;
		
		public static Dialogs newInstance(int type) {
			Dialogs frag = new Dialogs();
			Bundle args = new Bundle();
			args.putInt("type", type);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int id = getArguments().getInt("type");
			switch (id) {
			case BUSYBOX_ERROR:
				return new AlertDialog.Builder(getActivity())
						.setTitle(R.string.busybox_error)
						.setCancelable(true)
						.setMessage(
								R.string.busybox_error_explanation)
						.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								getActivity().finish();
							}
						}).create();
			case DATABASE_REMOVAL:
				return new AlertDialog.Builder(getActivity())
						.setTitle(R.string.database_removal)
						.setCancelable(true)
						.setMessage(
								R.string.database_removal_explanation)
						.setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						}).create();
			case REINSTALL:
				return new AlertDialog.Builder(getActivity()).setTitle(R.string.error).setCancelable(true)
						.setMessage(R.string.sorry_reinstall)
						.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								getActivity().finish();
							}
						}).create();
			case NEWS:
				return new AlertDialog.Builder(getActivity())
						.setTitle(R.string.news)
						.setCancelable(true)
						.setMessage(getString(R.string.news_content))
						.setPositiveButton(R.string.diagnosis_pro, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								try {
									Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=eu.thedarken.diagnosis.pro"));
									startActivity(marketIntent);
								} catch (Exception e) {
									Toast.makeText(getActivity(), R.string.no_market_application_found, Toast.LENGTH_SHORT).show();
								}
							}
						}).setNegativeButton(R.string.hide, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						}).create();
			}
			Dialog dialog = null;
			return dialog;
		}
	}

}