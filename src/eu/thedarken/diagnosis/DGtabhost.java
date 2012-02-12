package eu.thedarken.diagnosis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

public class DGtabhost extends TabActivity {
	protected static TabHost tabHost;
	protected static Context mContext;
	//private SharedPreferences settings;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Resources res = getResources(); // Resource object to get Drawables
        tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;
        Intent intent;  // Reusable Intent for each tab
        
        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(mContext, DGmain.class);
        spec = tabHost.newTabSpec("start").setIndicator("Diagnosis", res.getDrawable(R.xml.maintab)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(mContext, DGstats.class);
        spec = tabHost.newTabSpec("stats").setIndicator("Statistics", res.getDrawable(R.xml.batterytab)).setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(mContext, DGapps.class);
        spec = tabHost.newTabSpec("apps").setIndicator("Apps", res.getDrawable(R.xml.appstab)).setContent(intent);
        tabHost.addTab(spec);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	/*if(settings.getBoolean("reloadtabs", false)) {
    		prefEditor.putBoolean("reloadtabs", false);
    		prefEditor.commit();
    		
    		doTabs(true);
    	}*/

	    /*getTabWidget().getChildTabViewAt(0).setVisibility(TabWidget.VISIBLE);
	   	getTabWidget().getChildTabViewAt(1).setVisibility(TabWidget.VISIBLE);
		getTabWidget().getChildTabViewAt(2).setVisibility(TabWidget.VISIBLE);*/
    }
    
    /*public void setCurrentTab(int i) {
    	tabHost.setCurrentTab(i);
    }*/
    
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
	        while ((line = br.readLine()) != null)
	        {
	            buffer.append(line).append('\n');
	        }
	        text.setText(buffer.toString());
	        reader.close();
		} catch (IOException e) {
			Log.d(this.getPackageName(), "Error while reading changelog.txt");
			e.printStackTrace();
		}
        dialog.show();
    }
    
    private void showAbout() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.aboutbox);
        dialog.setTitle("About Diagnosis");
        Button xda = (Button) dialog.findViewById(R.id.xda);
        xda.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) 
            {
				Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://forum.xda-developers.com/showthread.php?t=1411074"));
				startActivity(browserIntent);
            }
        });
        Button email = (Button) dialog.findViewById(R.id.email);
        email.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) 
            {
            	createSupportEmail();
            }
        });
        Button close = (Button) dialog.findViewById(R.id.close);
        close.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) 
            {
            	dialog.dismiss();
            }
        });

        TextView text = (TextView) dialog.findViewById(R.id.HelpTextView);
        InputStreamReader reader;
        try {
			reader = new InputStreamReader(this.getAssets().open("about.txt"));
	        BufferedReader br = new BufferedReader(reader); 
	        String line = null;
	        while ((line = br.readLine()) != null)
	        {
	        	text.append(line + "\n");
	        }
	        reader.close();
		} catch (IOException e) {
			Log.d(this.getPackageName(), "Error while reading about file");
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
		intent.putExtra(android.content.Intent.EXTRA_TEXT,
		"\n\n" +
		
		"Send from inside the app.\n\n" +
		"Debug Information:\n" +
		"(This is anonymous and only tells me your device + firmware version.)\n" +
		"Diagnosis Version: " + version.toString() + "\n"
		+ "FINGERPRINT: " + android.os.Build.FINGERPRINT + "\n");
		startActivity(Intent.createChooser(intent, ""));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
}