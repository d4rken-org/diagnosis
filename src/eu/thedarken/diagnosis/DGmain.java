package eu.thedarken.diagnosis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class DGmain extends SherlockFragmentActivity {
	private static Context mContext;
	private Intent service;
	public static String versName = "";
	public static int versCode = 0;
	private final static int DB_DELETE_VERSION = 26;
	private final static int BUSYBOX_DELETE_VERSION = 26;
	public static String BUSYBOX = "";
	public static String BUSYBOX_VERSION = "";
	private SharedPreferences settings;
	private SharedPreferences.Editor prefEditor;
	private final String TAG = "eu.thedarken.diagnosis.DGmain";
	public static File db;

	private Bundle savedInstanceState;

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

		this.savedInstanceState = savedInstanceState;

		new setupTask(this).execute();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tabState", getSupportActionBar().getSelectedTab().getPosition());
	}

	@Override
	public void onResume() {
		super.onResume();
		if (DGmain.checkPro(mContext)) {
			getSupportActionBar().setTitle(mContext.getString(R.string.pro));
		} else {
			getSupportActionBar().setTitle("");
		}

		if (settings.getInt("news.shown", 0) < versCode) {
			prefEditor.putInt("news.shown", versCode);
			prefEditor.commit();
			MiscDialogFragments news = MiscDialogFragments.newInstance(MiscDialogFragments.NEWS);
			news.showDialog(getSupportFragmentManager());
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
			try {
				ft.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
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

			actionBar.removeAllTabs();

			Tab tab = actionBar.newTab().setText("info").setTabListener(new TabListener<DGinfo>(DGmain.this, "Info", DGinfo.class));
			actionBar.addTab(tab);

			tab = actionBar.newTab().setText("stats").setTabListener(new TabListener<DGstats>(DGmain.this, "Stats", DGstats.class));
			actionBar.addTab(tab);

			tab = actionBar.newTab().setText("apps").setTabListener(new TabListener<DGapps>(DGmain.this, "apps", DGapps.class));
			actionBar.addTab(tab);

			if (savedInstanceState != null) {
				actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tabState"));
			}

			dialog.dismiss();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			dialog.updateMessage(mActivity.getString(R.string.copying_busybox));

			CopyAssets();

			dialog.updateMessage(mActivity.getString(R.string.getting_busybox_version));

			BUSYBOX_VERSION = getBusyboxVersion();
			if (BUSYBOX_VERSION.length() == 0) {
				dialog.updateMessage(mActivity.getString(R.string.startup_error));
				MiscDialogFragments busybox_error = MiscDialogFragments.newInstance(MiscDialogFragments.BUSYBOX_ERROR);
				busybox_error.showDialog(getSupportFragmentManager());
			}

			dialog.updateMessage(mActivity.getString(R.string.checking_database));

			if (settings.getInt("dbversion", 0) < DB_DELETE_VERSION && db.exists()) {
				if (db.delete()) {
					dialog.updateMessage(mActivity.getString(R.string.db_deletion_successfull));
					Log.d(TAG, mActivity.getString(R.string.db_deletion_successfull));
					// DGdatabase db_object =
					// DGdatabase.getInstance(mContext.getApplicationContext());
					// db_object.init();
					prefEditor.putInt("dbversion", DB_DELETE_VERSION);
					prefEditor.commit();
				} else {
					dialog.updateMessage(mActivity.getString(R.string.could_not_delete_db));
					Log.d(TAG, mActivity.getString(R.string.could_not_delete_db));
					MiscDialogFragments reinstall = MiscDialogFragments.newInstance(MiscDialogFragments.REINSTALL);
					reinstall.showDialog(getSupportFragmentManager());
				}
				MiscDialogFragments db_removal = MiscDialogFragments.newInstance(MiscDialogFragments.DATABASE_REMOVAL);
				db_removal.showDialog(getSupportFragmentManager());
			} else {
				prefEditor.putInt("dbversion", DB_DELETE_VERSION);
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
		if (settings.getInt("busyboxversion", 0) < BUSYBOX_DELETE_VERSION) {
			new File(mContext.getFilesDir() + "/busybox").delete();
			prefEditor.putInt("busyboxversion", BUSYBOX_DELETE_VERSION);
			prefEditor.commit();
		}
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
		if (c.getExitCode() == 0) {
			Log.d(TAG, "Rights for non root busybox successfully set.");
		} else {
			Log.d(TAG, "Error when trying to set rights for non rooted busybox.");
		}
	}

	private String getBusyboxVersion() {
		Cmd c = new Cmd();
		c.addCommand(DGmain.BUSYBOX + " | " + DGmain.BUSYBOX + " head -n1" + "\n");
		c.setTimeout(15000);
		c.execute();
		if (c.getOutput().size() > 0 && c.getOutput().get(0).length() > 21) {
			String vers = c.getOutput().get(0);
			vers = (String) vers.subSequence(0, 22);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		if (DGoverlay.isRunning) {
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
				MiscDialogFragments news = MiscDialogFragments.newInstance(MiscDialogFragments.NEWS);
				news.showDialog(getSupportFragmentManager());
				break;
			case R.id.starttracking:
				new serviceTask(this).execute();
				break;
			case R.id.settings:
				Intent startPreferencesActivity = new Intent(this, DGsettings.class);
				this.startActivity(startPreferencesActivity);
				break;
			case R.id.help_translate:
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.getlocalization.com/diagnosis/"));
				startActivity(browserIntent);
				break;
			case R.id.changelog:
				ChangelogDialogFragment changelog = ChangelogDialogFragment.newInstance();
				changelog.showDialog(getSupportFragmentManager());
				break;
			case R.id.about:
				AboutDialogFragment about = AboutDialogFragment.newInstance();
				about.showDialog(getSupportFragmentManager());
				break;
		}
		return true;
	}
}