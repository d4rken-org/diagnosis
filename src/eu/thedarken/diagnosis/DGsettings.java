package eu.thedarken.diagnosis;

import java.io.File;
import java.util.ArrayList;
import eu.thedarken.diagnosis.DGoverlay.Line;
import eu.thedarken.diagnosis.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

public class DGsettings extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	private Context mContext;
	private SharedPreferences settings;
	private Editor prefEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
       	settings = PreferenceManager.getDefaultSharedPreferences(mContext);
       	prefEditor = settings.edit();
        try {
        	addPreferencesFromResource(R.xml.preferences);
        } catch (Exception e) {
        	prefEditor.clear();
        	prefEditor.commit();
        	Log.d(mContext.getPackageName(), "Settings were corrupt and have been reset!");
        	addPreferencesFromResource(R.xml.preferences);
        }
        for(int i=0;i<4;i++) {
	        prefEditor.putInt("overlay.color.normal.line"+i, settings.getInt("overlay.color.normal.line"+i, DGoverlay.default_color_normal));
	        prefEditor.putInt("overlay.color.alert.line"+i, settings.getInt("overlay.color.alert.line"+i, DGoverlay.default_color_alert));
	        prefEditor.putInt("overlay.color.background.line"+i, settings.getInt("overlay.color.background.line"+i, DGoverlay.default_color_bg));
	    	prefEditor.commit();

	        ((ColorPickerPreference)findPreference("overlay.color.normal.line"+i)).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
					return true;
				}
	        });
	        ((ColorPickerPreference)findPreference("overlay.color.normal.line"+i)).setAlphaSliderEnabled(true);
	
	        ((ColorPickerPreference)findPreference("overlay.color.alert.line"+i)).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
					return true;
				}
	        });
	        ((ColorPickerPreference)findPreference("overlay.color.alert.line"+i)).setAlphaSliderEnabled(true);
	        
	        ((ColorPickerPreference)findPreference("overlay.color.background.line"+i)).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
					return true;
				}
	        });
	        ((ColorPickerPreference)findPreference("overlay.color.background.line"+i)).setAlphaSliderEnabled(true);
	        
        }   
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        settings.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        settings.unregisterOnSharedPreferenceChangeListener(this);
    }
    

	@SuppressWarnings("unchecked")
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(key.equals("general.intervall")) {
			DGoverlay.INTERVALL = (sharedPreferences.getInt("general.intervall", 5)*1000);
		} else if(key.equals("general.database.enabled")) {
			DGdata.db_cache_reset_required = true;
		} else if(key.equals("database.graphs.data_points")) {
			DGdatabase.graph_data_points = sharedPreferences.getInt("database.graphs.data_points", 50);
		} else if(key.equals("database.cachesize")) {
			DGdata.DB_CACHE_SIZE = sharedPreferences.getInt("database.cachesize", 24);
		} else if(key.equals("layout.usefahrenheit")) {
			DGoverlay.use_fahrenheit = sharedPreferences.getBoolean("layout.usefahrenheit", false);
		} else if(key.equals("layout.line0")) {
			if(DGoverlay.getLine(0) != null) {
				DGoverlay.getLine(0).layout = (ArrayList<Integer>) ObjectSerializer.deserialize(settings.getString("layout.line0", ""));
			}
			Log.d("eu.thedarken.diagnosis", "A");
		} else if(key.equals("layout.line1")) {
			if(DGoverlay.getLine(1) != null) {
				DGoverlay.getLine(1).layout = (ArrayList<Integer>) ObjectSerializer.deserialize(settings.getString("layout.line1", ""));
			}
			Log.d("eu.thedarken.diagnosis", "B");
		} else if(key.equals("layout.line2")) {
			if(DGoverlay.getLine(2) != null) {
				DGoverlay.getLine(2).layout = (ArrayList<Integer>) ObjectSerializer.deserialize(settings.getString("layout.line2", ""));
			}
			Log.d("eu.thedarken.diagnosis", "C");
		} else if(key.equals("layout.line3")) {
			if(DGoverlay.getLine(3) != null) {
				DGoverlay.getLine(3).layout = (ArrayList<Integer>) ObjectSerializer.deserialize(settings.getString("layout.line3", ""));
			}
			Log.d("eu.thedarken.diagnosis", "D");
		} else if(key.equals("layout.line3")) {
			if(DGoverlay.getLine(3) != null) {
				DGoverlay.getLine(3).layout = (ArrayList<Integer>) ObjectSerializer.deserialize(settings.getString("layout.line3", ""));
			}
			Log.d("eu.thedarken.diagnosis", "D");
		} else if(key.contains("overlay.y_pos.line")) {
			String value = sharedPreferences.getString(key, "45");
			if(value.length() < 1) {
				prefEditor.putString(key, "45");
				prefEditor.commit();
			}
		} else if(key.contains("overlay.x_pos.line")) {
			String value = sharedPreferences.getString(key, "1");
			if(value.length() < 1) {
				prefEditor.putString(key, "1");
				prefEditor.commit();
			}
		} else if(key.equals("general.internet.ping.target")) {
			String target = sharedPreferences.getString(key, "www.google.com");
			if(target.length() < 5) {
				
				target = "www.google.com"; 
				prefEditor.putString(key, target);
				prefEditor.commit();
			}
		}
		Log.d("eu.thedarken.diagnosis", "preferences changed");
		DGoverlay.initReset();
	}

	
	@Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, final Preference preference) {
		if(preference.getKey().equals("database.dropall")) {
			new clearDBsTask(this).execute();
		} else if(preference.getKey().equals("layout.line0")) {
		    Bundle b = new Bundle();
		    b.putString("currentset", settings.getString("layout.line0", null));
		    b.putInt("line", 0);
		    //Log.d("eu.thedarken.diagnosis", settings.getString("layout.line0", null));
		    Intent folderView = new Intent(mContext, DGlinebuilder.class);
		    folderView.putExtras(b);
		    startActivityForResult(folderView, 0);
		} else if(preference.getKey().equals("layout.line1")) {
		    Bundle b = new Bundle();
		    b.putString("currentset", settings.getString("layout.line1", null));
		    b.putInt("line", 1);
		    Intent folderView = new Intent(mContext, DGlinebuilder.class);
		    folderView.putExtras(b);
		    startActivityForResult(folderView, 1);
		} else if(preference.getKey().equals("layout.line2")) {
		    Bundle b = new Bundle();
		    b.putString("currentset", settings.getString("layout.line2", null));
		    b.putInt("line", 2);
		    Intent folderView = new Intent(mContext, DGlinebuilder.class);
		    folderView.putExtras(b);
		    startActivityForResult(folderView, 2);		    
		} else if(preference.getKey().equals("layout.line3")) {
		    Bundle b = new Bundle();
		    b.putString("currentset", settings.getString("layout.line3", null));
		    b.putInt("line", 3);
		    Intent folderView = new Intent(mContext, DGlinebuilder.class);
		    folderView.putExtras(b);
		    startActivityForResult(folderView, 3);		    
		}  else if(preference.getKey().contains("presets.load.slot")) {
			loadSlot(Integer.parseInt(String.valueOf(preference.getKey().charAt(preference.getKey().length()-1))));
		} else if(preference.getKey().contains("presets.save.slot")) {
			saveSlot(Integer.parseInt(String.valueOf(preference.getKey().charAt(preference.getKey().length()-1))));
		} else if(preference.getKey().equals("License.ACRA")) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/acra/source/browse/trunk/acra/LICENSE"));
			startActivity(browserIntent);
		}
		return false;
	}
	
	private void loadSlot(int pos) {
		Log.d("eu.thedarken.diagnosis", "loading slot " + pos);
		for(int lineno=0;lineno<4;lineno++) {
	    	prefEditor.putString("layout.line"+lineno,settings.getString("preset.custom.slot" + pos +".layout.line"+lineno, ObjectSerializer.serialize(new ArrayList<Line>())));
			prefEditor.putString("overlay.x_pos.line"+lineno, settings.getString("preset.custom.slot" + pos +".overlay.x_pos.line"+lineno,"1"));
			prefEditor.putString("overlay.y_pos.line"+lineno, settings.getString("preset.custom.slot" + pos +".overlay.y_pos.line"+lineno,"15"));
			prefEditor.putBoolean("overlay.align.right.line"+lineno, settings.getBoolean("preset.custom.slot" + pos +".overlay.align.right.line"+lineno, false));
	    	prefEditor.putInt("overlay.color.normal.line"+lineno, settings.getInt("preset.custom.slot" + pos +".overlay.color.normal.line"+lineno, -16318720));
	    	prefEditor.putInt("overlay.color.alert.line"+lineno, settings.getInt("preset.custom.slot" + pos +".overlay.color.alert.line"+lineno, -256));
	    	prefEditor.putString("overlay.font.type.line"+lineno, settings.getString("preset.custom.slot" + pos +".overlay.font.type.line"+lineno, "1"));
	    	prefEditor.putInt("overlay.font.size.line"+lineno, settings.getInt("preset.custom.slot" + pos +".overlay.font.size.line"+lineno, 15));
			prefEditor.putBoolean("overlay.drawbackground.line"+lineno, settings.getBoolean("preset.custom.slot" + pos +".overlay.drawbackground.line"+lineno, false));
			prefEditor.putInt("overlay.color.background.line"+lineno,settings.getInt("preset.custom.slot" + pos +".overlay.color.background.line"+lineno, 0x70000000));
		}
		prefEditor.putString("overlay.divider", settings.getString("overlay.divider", "|"));
		prefEditor.commit();
		Toast.makeText(mContext, "Restored preset from slot "+ pos,Toast.LENGTH_SHORT).show();
    	DGoverlay.initReset();
	}
	
	private void saveSlot(int pos) {
		Log.d("eu.thedarken.diagnosis", "save to slot " + pos);
		for(int lineno=0;lineno<4;lineno++) {
	    	prefEditor.putString("preset.custom.slot" + pos +".layout.line"+lineno,settings.getString("layout.line"+lineno, ObjectSerializer.serialize(new ArrayList<Line>())));
			prefEditor.putString("preset.custom.slot" + pos +".overlay.x_pos.line"+lineno, settings.getString("overlay.x_pos.line"+lineno,"1"));
			prefEditor.putString("preset.custom.slot" + pos +".overlay.y_pos.line"+lineno, settings.getString("overlay.y_pos.line"+lineno,"15"));
			prefEditor.putBoolean("preset.custom.slot" + pos +".overlay.align.right.line"+lineno, settings.getBoolean("overlay.align.right.line"+lineno, false));
	    	prefEditor.putInt("preset.custom.slot" + pos +".overlay.color.normal.line"+lineno, settings.getInt("overlay.color.normal.line"+lineno, -16318720));
	    	prefEditor.putInt("preset.custom.slot" + pos +".overlay.color.alert.line"+lineno, settings.getInt("overlay.color.alert.line"+lineno, -256));
	    	prefEditor.putString("preset.custom.slot" + pos +".overlay.font.type.line"+lineno, settings.getString("overlay.font.type.line"+lineno, "1"));
	    	prefEditor.putInt("preset.custom.slot" + pos +".overlay.font.size.line"+lineno, settings.getInt("overlay.font.size.line"+lineno, 15));
			prefEditor.putBoolean("preset.custom.slot" + pos +".overlay.drawbackground.line"+lineno, settings.getBoolean("overlay.drawbackground.line"+lineno, false));
			prefEditor.putInt("preset.custom.slot" + pos +".overlay.color.background.line"+lineno,settings.getInt("overlay.color.background.line"+lineno, 0x70000000));
		}
		prefEditor.putString("preset.custom.slot" + pos +".overlay.divider", settings.getString("overlay.divider", "|"));
		prefEditor.commit();
		Toast.makeText(mContext, "Preset saved to slot "+ pos,Toast.LENGTH_SHORT).show();
	}
	
    private class clearDBsTask extends AsyncTask<String, Void, Boolean> {
        private PreferenceActivity mActivity;
        private ProgDialog dialog;
        public clearDBsTask(PreferenceActivity activity) {
        	mActivity = activity;
        }

        protected void onPreExecute() {
        	dialog = new ProgDialog(mActivity);
        	dialog.setMessage("Clearing database");
        	dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        	dialog.show();
        	Log.d("eu.thedarken.diagnosis", "Clearing DB...");
        }

        @Override
        protected void onPostExecute(final Boolean allok) {
        	if(allok) {
        		Toast.makeText(mContext, "Database reset",Toast.LENGTH_LONG).show();
        	} else {
        		Toast.makeText(mContext, "Sorry there was an error, maybe stop Diagnosis?",Toast.LENGTH_LONG).show();
        	}
        	Log.d("eu.thedarken.diagnosis", "...done");
        	try {
	            if(dialog.isShowing()) {
	                dialog.dismiss();
	            	mActivity.removeDialog(dialog.hashCode());
	            }
	        } catch (Exception e) { }
        }
        
		@Override
		protected Boolean doInBackground(String... params) {
		    File db = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/eu.thedarken.diagnosis/databases/database.db");
		    if(db.delete()) {
		    	return true;
		    }
			return false;
		}
    }
    
}
