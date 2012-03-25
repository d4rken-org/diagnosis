package eu.thedarken.diagnosis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;
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

public class DGmain extends SherlockFragmentActivity {
	private static Context mContext;
	private Intent service;
	public static boolean isPro = false;
	public static String versName = "";
	public static int versCode = 0;
	private final static int DB_DELETE_VERSION = 18;
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

		checkPro();

		service = new Intent(mContext, DGoverlay.class);

		settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		prefEditor = settings.edit();

		PreferenceManager.setDefaultValues(mContext, R.xml.preferences, false);

		BUSYBOX = mContext.getFilesDir() + "/busybox";

		new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/eu.thedarken.diagnosis/databases/").mkdirs();
		db = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/eu.thedarken.diagnosis/databases/database.db");

		prefEditor.putString("BUSYBOX", BUSYBOX);
		prefEditor.commit();

		new setupTask(mContext).execute();

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
		checkPro();
		if (DGmain.isPro) {
			getSupportActionBar().setTitle("Pro");
		} else {
			getSupportActionBar().setTitle("");
		}
		
		if(!DGmain.isPro && !settings.getBoolean("pro.advertised", false)) {
			prefEditor.putBoolean("pro.advertised", true);
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
		private Context context;
		private ProgDialog dialog;

		public setupTask(Context c) {
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
			if (dialog.isShowing())
				dialog.dismiss();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			dialog.updateMessage("Copying busybox...");
			CopyAssets();

			dialog.updateMessage("Getting busybox version");

			BUSYBOX_VERSION = getBusyboxVersion();
			if(BUSYBOX_VERSION == null || (BUSYBOX_VERSION != null && BUSYBOX_VERSION.length() == 0)) {
				dialog.updateMessage("Startup ERROR!");
				showMyDialog(Dialogs.BUSYBOX_ERROR);
			}

			if (settings.getInt("dbversion", 0) < DB_DELETE_VERSION && db.exists()) {
				if (db.delete()) {
					dialog.updateMessage("DB deletion successfull");
					Log.d(TAG, "DB deletion successfull");
					prefEditor.putInt("dbversion", DGmain.versCode);
					prefEditor.commit();
				} else {
					dialog.updateMessage("Could not delete DB");
					Log.d(TAG, "Could not delete DB");
					showMyDialog(Dialogs.REINSTALL);
				}
				prefEditor.putInt("dbversion", DGmain.versCode);
				prefEditor.commit();
				showMyDialog(Dialogs.DATABASE_REMOVAL);
			} else {
				prefEditor.putInt("dbversion", DGmain.versCode);
				prefEditor.commit();
			}

			Styles s = new Styles(mContext);
			s.initLines();

			try {
				if(!FeelGood.isSignatureOfficial(mContext, mContext.getPackageName()))
					isPro = false;
			} catch (NameNotFoundException e) {
				isPro = false;
			}
			
			return true;
		}
	}

	private class serviceTask extends AsyncTask<String, Void, Boolean> {
		private Context context;
		private ProgDialog dialog;

		public serviceTask(Context c) {
			context = c;
		}

		protected void onPreExecute() {
			dialog = new ProgDialog(context);
			dialog.setProgressStyle(DGoverlay.isRunning ? ProgressDialog.STYLE_SPINNER : ProgressDialog.STYLE_HORIZONTAL);
			dialog.updateMessage(DGoverlay.isRunning ? "Stopping service..." : "Starting service...");
			dialog.show();

		}

		@Override
		protected void onPostExecute(final Boolean ok) {
			invalidateOptionsMenu();
			if (dialog.isShowing())
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

					dialog.updateMessage("Cleaning old database entries");
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
		Process q = null;
		try {
			q = Runtime.getRuntime().exec("sh");
			OutputStreamWriter os = new OutputStreamWriter(q.getOutputStream());
			os.write("chmod 777 " + BUSYBOX + "\n");
			os.write("exit\n");
			os.flush();
			q.waitFor();
			os.close();
			Log.d(TAG, "Rights for non root busybox successfully set.");
		} catch (Exception e) {
			if (q != null)
				q.destroy();
			Log.d(TAG, "Error when trying to set rights for non rooted busybox.");
		}
	}

	private String getBusyboxVersion() {
		Process q = null;
		String line = null;
		try {
			q = Runtime.getRuntime().exec("sh");
			OutputStreamWriter os = new OutputStreamWriter(q.getOutputStream());
			Scanner e = new Scanner(q.getErrorStream());
			Scanner s = new Scanner(q.getInputStream());
			os.write(DGmain.BUSYBOX + "\n");
			os.write("exit\n");
			os.flush();
			q.waitFor();
			os.close();
			// Print Errors
			while (e.hasNext()) {
				Log.d(TAG, e.nextLine());
			}
			e.close();
			line = s.nextLine();
			s.close();
		} catch (Exception e) {
			if (q != null)
				q.destroy();
			e.printStackTrace();
			Log.d(TAG, "Error while getting busybox version");
		}
		String ret = "WARNING No busybox";
		if (line != null) {
			ret = line;
			if (line.length() > 20) {
				ret = (String) line.subSequence(0, 21);
			}
			Log.d(TAG, "Busybox version: " + ret);
			return ret;
		} else {
			return null;
		}
	}

	public void checkPro() {
		Context diagnosispro = null;
		try {
			diagnosispro = mContext.createPackageContext("eu.thedarken.diagnosis.pro", 0);
		} catch (NameNotFoundException e) {
			DGmain.isPro = false;
			return;
		}
		if (diagnosispro != null) {
			if (mContext.getPackageManager().checkSignatures(mContext.getPackageName(), diagnosispro.getPackageName()) == PackageManager.SIGNATURE_MATCH) {
				DGmain.isPro = true;
				return;
			}
		}
		DGmain.isPro = false;
		return;
	}

	private void showChangelog() {
		Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.changelog);
		dialog.setTitle("Diagnosis Changelog:");
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
			menu.findItem(R.id.starttracking).setTitle("Stop Tracking");
		} else {
			menu.findItem(R.id.starttracking).setTitle("Start Tracking");
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
			new serviceTask(mContext).execute();
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

	private void showMyDialog(int type) {
		FragmentManager ft = getSupportFragmentManager();
		DialogFragment newFragment = Dialogs.newInstance(type);
		newFragment.show(ft, "dialog");
	}

	private static class Dialogs extends DialogFragment {
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
			case 0:
				return new AlertDialog.Builder(getActivity())
						.setTitle("BUSYBOX error!")
						.setCancelable(true)
						.setMessage(
								"Could not use our BUSYBOX :-(\nTo prevent unwanted behavior Diagnosis will close now.\nPlease try restarting or reinstalling Diagnosis.\nShould this not help please write me an email:\n(support@thedarken.eu)\nSorry for your troubles!")
						.setPositiveButton("Close", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								getActivity().getParent().finish();
								// android.os.Process.killProcess(android.os.Process.myPid());
							}
						}).create();
			case 1:
				return new AlertDialog.Builder(getActivity())
						.setTitle("Database removal")
						.setCancelable(true)
						.setMessage(
								"This newer version of Diagnosis uses a different structure to store the periodic data.\nTo avoid errors and unwanted behavior, the previous version has been removed. I'm telling you this so that you are not suprised that the database is empty.")
						.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						}).create();
			case 2:
				return new AlertDialog.Builder(getActivity()).setTitle("Error").setCancelable(true)
						.setMessage("Sorry, something went wrong and you will have to reinstall this app.")
						.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								getActivity().getParent().finish();
							}
						}).create();
			case 3:
				return new AlertDialog.Builder(getActivity())
						.setTitle("News")
						.setCancelable(true)
						.setMessage(
								"I'm sure you have noticed the new UI and i hope you like it.\n\nI have published 'Diagnosis Pro'.\nWhich you can purchase to enable a few additional options and support my work.\nThank you.")
						.setPositiveButton("Diagnosis Pro", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=eu.thedarken.diagnosis.pro"));
								startActivity(marketIntent);
							}
						}).setNegativeButton("Thanks, but no.", new DialogInterface.OnClickListener() {
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