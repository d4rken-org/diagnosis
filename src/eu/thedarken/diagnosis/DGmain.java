package eu.thedarken.diagnosis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import eu.thedarken.diagnosis.DGdatabase;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DGmain extends Activity {
	private static Button start;
    private Intent service;
    private SharedPreferences settings;
    private static SharedPreferences.Editor prefEditor;
    private Context mContext;
    private static String BUSYBOX = "";
    private TextView db_size;
    private TextView db_status;
    private static String versName = "";
    private static int versCode = 0;
    private final static int DB_DELETE_VERSION = 16;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.maintab);
        start = (Button) findViewById(R.id.start);
        
        service = new Intent(this, DGoverlay.class);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = settings.edit();

        PreferenceManager.setDefaultValues(mContext, R.xml.preferences, false);
        
        isRunning();
        
		BUSYBOX  = mContext.getFilesDir() + "/busybox";
        
        prefEditor.putString("BUSYBOX", BUSYBOX);
        prefEditor.commit();
        
        new setupTask(this).execute();
        
		try {
	        versCode = mContext.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
	        versName = mContext.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
        db_size  = (TextView) findViewById(R.id.db_size);
        db_status = (TextView) findViewById(R.id.db_status);
        
		new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/eu.thedarken.diagnosis/databases/").mkdirs();
		
        try {
        	TextView vers = (TextView) findViewById(R.id.appvers);
        	vers.setText("Version " + versName);
		} catch (Exception e1) {
			Log.d(mContext.getPackageName(), "Error while getting version");
			e1.printStackTrace();
		}
		
		initLines();

    }
    
    @Override
    public void onResume() {
    	isRunning();
    	super.onResume();
		new startinfoTask(this).execute();
    }
    
	private void initLines() {
		if(!settings.getBoolean("initdone", false)) {
			String defl1 = "kmonaaafhdhcaabdgkgbhggbcohfhegjgmcoebhchcgbhjemgjhdhehiibncbnjjmhgbjnadaaabejaaaehdgjhkgfhihaaaaaaaaghhaeaaaaaaamhdhcaabbgkgbhggbcogmgbgoghcoejgohegfghgfhcbcockakephibihdiacaaabejaaafhggbgmhfgfhihcaabagkgbhggbcogmgbgoghcoeohfgngcgfhcigkmjfbnaljeoailacaaaahihaaaaaaaaehdhbaahoaaacaaaaaabdhdhbaahoaaacaaaaaablhdhbaahoaaacaaaaaaakhdhbaahoaaacaaaaaabahdhbaahoaaacaaaaaabbhi";
			String defl2 = "kmonaaafhdhcaabdgkgbhggbcohfhegjgmcoebhchcgbhjemgjhdhehiibncbnjjmhgbjnadaaabejaaaehdgjhkgfhihaaaaaaaabhhaeaaaaaaamhdhcaabbgkgbhggbcogmgbgoghcoejgohegfghgfhcbcockakephibihdiacaaabejaaafhggbgmhfgfhihcaabagkgbhggbcogmgbgoghcoeohfgngcgfhcigkmjfbnaljeoailacaaaahihaaaaaaaadhi";
			String defl3 = "";
			String defl4 = "";
			prefEditor.putString("layout.line0", defl1);
			prefEditor.putString("layout.line1", defl2);
			prefEditor.putString("layout.line2", defl3);
			prefEditor.putString("layout.line3", defl4);
			
    		int line = 0;
    		prefEditor.putBoolean("overlay.align.right.line" + line, false);
    		prefEditor.putString("overlay.font.type.line" + line, "1");
    		prefEditor.putString("overlay.x_pos.line" + line, "1");
    		prefEditor.putString("overlay.y_pos.line" + line, "45");
    		prefEditor.putInt("overlay.font.size.line" + line, 15);
    		prefEditor.putInt("overlay.color.normal.line" + line,0xff06ff00);
    		prefEditor.putInt("overlay.color.alert.line" + line,0xffffff00);
    		prefEditor.putBoolean("overlay.drawbackground.line" + line, false);
    		prefEditor.putInt("overlay.color.background.line" + line,0x70000000);
    		
    		line = 1;
    		prefEditor.putBoolean("overlay.align.right.line" + line, false);
    		prefEditor.putString("overlay.font.type.line" + line, "1");
    		prefEditor.putString("overlay.x_pos.line" + line, "1");
    		prefEditor.putString("overlay.y_pos.line" + line, "60");
    		prefEditor.putInt("overlay.font.size.line" + line, 15);
    		prefEditor.putInt("overlay.color.normal.line" + line,0xff06ff00);
    		prefEditor.putInt("overlay.color.alert.line" + line,0xffffff00);
    		prefEditor.putBoolean("overlay.drawbackground.line" + line, false);
    		prefEditor.putInt("overlay.color.background.line" + line,0x70000000);
    		
    		line = 2;
    		prefEditor.putBoolean("overlay.align.right.line" + line, false);
    		prefEditor.putString("overlay.font.type.line" + line, "1");
    		prefEditor.putString("overlay.x_pos.line" + line, "1");
    		prefEditor.putString("overlay.y_pos.line" + line, "75");
    		prefEditor.putInt("overlay.font.size.line" + line, 15);
    		prefEditor.putInt("overlay.color.normal.line" + line,0xff06ff00);
    		prefEditor.putInt("overlay.color.alert.line" + line,0xffffff00);
    		prefEditor.putBoolean("overlay.drawbackground.line" + line, false);
    		prefEditor.putInt("overlay.color.background.line" + line,0x70000000);
    		
    		line = 3;
    		prefEditor.putBoolean("overlay.align.right.line" + line, false);
    		prefEditor.putString("overlay.font.type.line" + line, "1");
    		prefEditor.putString("overlay.x_pos.line" + line, "1");
    		prefEditor.putString("overlay.y_pos.line" + line, "90");
    		prefEditor.putInt("overlay.font.size.line" + line, 15);
    		prefEditor.putInt("overlay.color.normal.line" + line,0xff06ff00);
    		prefEditor.putInt("overlay.color.alert.line" + line,0xffffff00);
    		prefEditor.putBoolean("overlay.drawbackground.line" + line, false);
    		prefEditor.putInt("overlay.color.background.line" + line,0x70000000);

    		prefEditor.putString("overlay.divider", "|");
			prefEditor.putBoolean("initdone", true);
			prefEditor.commit();
		}
	}

    public void setStyle1(View view) {
		Display display = getWindowManager().getDefaultDisplay(); 
		int lineno;
		
		ArrayList<Integer> line = new ArrayList<Integer>();
		line.add(4);
		line.add(19);
		line.add(10);
		lineno = 0;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, "45");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
		
    	line.clear();		
		line.add(16);
		line.add(17);
		lineno = 1;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, "45");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
		
    	line.clear();
    	line.add(25);
    	line.add(26);
    	lineno = 2;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, "60");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(2);
    	lineno = 3;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, "60");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
		
		prefEditor.putString("overlay.divider", "|");
    	prefEditor.commit();
    	
    	DGoverlay.initReset();
    }
    
    public void setStyle2(View view) {
		Display display = getWindowManager().getDefaultDisplay(); 
		int lineno;
		
		ArrayList<Integer> line = new ArrayList<Integer>();
    	line.add(24);
    	line.add(23);
		lineno = 0;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, "12");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
		
    	line.clear();
    	line.add(20);
    	line.add(22);
    	line.add(21);
		lineno = 1;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, "12");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
		
    	line.clear();
    	line.add(2);
		lineno = 2;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, "45");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
		
    	line.clear();
		lineno = 3;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, "45");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);

		prefEditor.putString("overlay.divider", "~");
    	prefEditor.commit();
    	
    	DGoverlay.initReset();
    }
    
    public void setStyle3(View view) {
		Display display = getWindowManager().getDefaultDisplay(); 
		int lineno;
		ArrayList<Integer> line = new ArrayList<Integer>();
    	
		line.add(4);
    	line.add(10);
		lineno = 0;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
		lineno = 1;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(2);
		lineno = 2;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, "45");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
		lineno = 3;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, "45");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
		prefEditor.putString("overlay.divider", " # ");
    	prefEditor.commit();
    	
    	DGoverlay.initReset();
    }
    
    public void setStyle4(View view) {
		Display display = getWindowManager().getDefaultDisplay(); 
		int lineno;
		ArrayList<Integer> line = new ArrayList<Integer>();
    	
    	line.add(5);
    	line.add(6);
    	line.add(8);
		lineno = 0;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(11);
    	line.add(19);
		lineno = 1;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
		lineno = 2;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, "45");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
		lineno = 3;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, "45");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
		prefEditor.putString("overlay.divider", " /\\ ");
    	prefEditor.commit();
    	
    	DGoverlay.initReset();
    }
    
    public void setStyle5(View view) {
		Display display = getWindowManager().getDefaultDisplay(); 
		int lineno;
		ArrayList<Integer> line = new ArrayList<Integer>();
    	
		lineno = 0;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(20);
    	line.add(22);
    	line.add(21);
		lineno = 1;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(4);
    	line.add(11);
		lineno = 2;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, String.valueOf(display.getHeight()));
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(16);
    	line.add(17);
		lineno = 3;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, String.valueOf(display.getHeight()));
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
		prefEditor.putString("overlay.divider", " ' ");
    	prefEditor.commit();
    	
    	DGoverlay.initReset();
    }
    
    public void setStyle6(View view) {
		Display display = getWindowManager().getDefaultDisplay(); 
		int lineno;
		ArrayList<Integer> line = new ArrayList<Integer>();
    	
    	line.add(4);
    	line.add(10);
		lineno = 0;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(20);
    	line.add(22);
       	line.add(21);
		lineno = 1;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(16);
    	line.add(17);
		lineno = 2;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, String.valueOf(display.getHeight()));
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(25);
    	line.add(26);
		lineno = 3;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, String.valueOf(display.getHeight()));
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
		prefEditor.putString("overlay.divider", " | ");
    	prefEditor.commit();
    	
    	DGoverlay.initReset();
    }
    
    public void setStyle7(View view) {
		Display display = getWindowManager().getDefaultDisplay(); 
		int lineno;
		ArrayList<Integer> line = new ArrayList<Integer>();
    	
    	line.add(3);
		lineno = 0;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
		lineno = 1;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
		lineno = 2;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, String.valueOf(display.getHeight()));
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(5);
    	line.add(6);
    	line.add(9);
    	line.add(8);
		lineno = 3;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, String.valueOf(display.getHeight()));
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,16);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
		
		prefEditor.putString("overlay.divider", ";");
    	prefEditor.commit();
    	
    	DGoverlay.initReset();
    }

    public void setStyle8(View view) {
		Display display = getWindowManager().getDefaultDisplay(); 
		int lineno;
		ArrayList<Integer> line = new ArrayList<Integer>();
    	
		lineno = 0;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,14);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(3);
		lineno = 1;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,14);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(4);
    	line.add(10);
    	line.add(16);
    	line.add(17);
		lineno = 2;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, String.valueOf(display.getHeight()));
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,14);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
		lineno = 3;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, String.valueOf(display.getHeight()));
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,14);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
		prefEditor.putString("overlay.divider", "*");
    	prefEditor.commit();
    	
    	DGoverlay.initReset();
    }
    public void setStyle9(View view) {
		Display display = getWindowManager().getDefaultDisplay(); 
		int lineno;
		ArrayList<Integer> line = new ArrayList<Integer>();
    	
    	line.add(16);
		lineno = 0;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,15);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(17);
		lineno = 1;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,14);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
		line.add(4);
		line.add(19);
		line.add(11);
		lineno = 2;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()/3));
		prefEditor.putString("overlay.y_pos.line"+lineno, "40");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,14);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
		lineno = 3;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, String.valueOf(display.getHeight()));
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,15);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
		prefEditor.putString("overlay.divider", " - ");
    	prefEditor.commit();
    	
    	DGoverlay.initReset();
    }
 
    public void setStyle0(View view) {
		Display display = getWindowManager().getDefaultDisplay(); 
		int lineno;
		ArrayList<Integer> line = new ArrayList<Integer>();
    	
  		lineno = 0;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,15);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(4);
		lineno = 1;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, "15");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,14);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(3);
		lineno = 2;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, "1");
		prefEditor.putString("overlay.y_pos.line"+lineno, String.valueOf(display.getHeight()));
		prefEditor.putBoolean("overlay.align.right.line"+lineno, false);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,14);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
    	
    	line.clear();
    	line.add(10);
		lineno = 3;
    	prefEditor.putString("layout.line"+lineno,ObjectSerializer.serialize(line));
		prefEditor.putString("overlay.x_pos.line"+lineno, String.valueOf(display.getWidth()-1));
		prefEditor.putString("overlay.y_pos.line"+lineno, "40");
		prefEditor.putBoolean("overlay.align.right.line"+lineno, true);
    	prefEditor.putInt("overlay.color.normal.line"+lineno, -16318720);
    	prefEditor.putInt("overlay.color.alert.line"+lineno,-256);
    	prefEditor.putString("overlay.font.type.line"+lineno, "1");
    	prefEditor.putInt("overlay.font.size.line"+lineno,14);
		prefEditor.putBoolean("overlay.drawbackground.line" + lineno, false);
		prefEditor.putInt("overlay.color.background.line" + lineno,0x70000000);
		
		prefEditor.putString("overlay.divider", " <> ");
    	prefEditor.commit();
    	
    	DGoverlay.initReset();
    }
    
    public void isRunning() {
    	if(DGoverlay.isRunning) {
    		start.setText("Stop tracking");
    	} else {
    		start.setText("Start tracking");
    	}
    }
    

    
    private class startinfoTask extends AsyncTask<String, Void, Boolean> {
    	private Context context;
        private ProgDialog dialog;
        private StringBuilder db_size_sb = new StringBuilder();
        File db;
        public startinfoTask(Context c) {
        	context = c;
        	db = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/eu.thedarken.diagnosis/databases/database.db");
        }

        protected void onPreExecute() {
          	dialog = new ProgDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
            dialog.setMessage("Loading...");
        	Log.d(mContext.getPackageName(), "VersionName: " + versName);
        	Log.d(mContext.getPackageName(), "VersionCode: " + versCode);
    		if(settings.getInt("dbversion", 0) < DB_DELETE_VERSION && db.exists()) {
    			if(db.delete()) {
    				dialog.setMessage("DB deletion successfull");
    				Log.d(mContext.getPackageName(), "DB deletion successfull");
    				prefEditor.putInt("dbversion", versCode);
    				prefEditor.commit();
    			} else {
    				dialog.setMessage("Could not delete DB");
    				Log.d(mContext.getPackageName(), "Could not delete DB");
    				showDialog(2);
    			}
				prefEditor.putInt("dbversion", versCode);
				prefEditor.commit();
    			showDialog(1);
    		} else {
				prefEditor.putInt("dbversion", versCode);
				prefEditor.commit();
    		}
        }

        @Override
        protected void onPostExecute(final Boolean ok) {
        	if(!ok) {
        		dialog.updateMessage("Startup ERROR!");		
        		showDialog(0);
        	} else {
        		db_size.setText(db_size_sb.toString());
        		StringBuilder db_status_sb = new StringBuilder();
        		int intervall = settings.getInt("general.intervall", 5);
        		if(intervall > 1) {
        			db_status_sb.append("Update intervall is " + intervall  + " seconds.\n" );
        		} else {
        			db_status_sb.append("Update intervall is " + intervall  + " second.\n" );
        		}
        		
		        db_status_sb.append(settings.getInt("database.density",6) +" data set(s) will be condensed into 1\n" + settings.getInt("database.cachesize", 24) +" set(s) are cached before saving to database");

		        db_status.setText(db_status_sb.toString());
	        	try {
		            if(dialog.isShowing()) {
		                dialog.dismiss();
		            }
		        } catch (Exception e) { }
        	}
        }
        
		@Override
		protected Boolean doInBackground(String... params) {
			dialog.updateMessage("Loading database info");
	        
	        if(db.exists()) {
	        	db_size_sb.append("Database size:" + Formatter.formatFileSize(mContext, db.length()));
	        } else {
	        	db_size_sb.append("No DB yet");
	        }
	        
	    	DGdatabase db_object = DGdatabase.getInstance(mContext.getApplicationContext());
	    	db_size_sb.append(" | DB item count: " + db_object.getItemCount());
	    	
	    	dialog.updateMessage("Getting busybox version");

			Process q = null;
		    String line = null;
			try {
				q = Runtime.getRuntime().exec("sh");
				OutputStreamWriter os = new OutputStreamWriter(q.getOutputStream());
				Scanner e = new Scanner(q.getErrorStream());
				Scanner s = new Scanner(q.getInputStream());
				os.write(BUSYBOX + "\n");
			    os.write("exit\n");  
			    os.flush();		    
			    q.waitFor();
			    os.close();
			    //Print Errors
			    while(e.hasNext())
		  		{
			 		Log.d(mContext.getPackageName(), e.nextLine());
		  		}
			    e.close();
			    line = s.nextLine();
			    s.close();
			} catch (Exception e) {
				if(q!=null) q.destroy();
				e.printStackTrace();
				Log.d(mContext.getPackageName(), "Error while getting busybox version");
			}
			String ret = "WARNING No busybox";
			if(line != null) {
				ret = line;
				if(line.length() > 20) {
					ret = (String) line.subSequence(0,21);
				}
				Log.d(mContext.getPackageName(), "Busybox version: " + ret);
			} else {
				return false;
			}
			
			
			
			return true;
		}
    }
 
    private class setupTask extends AsyncTask<String, Void, Boolean> {
    	private Context context;
        private ProgDialog dialog;
        public setupTask(Context c ) {
        	context = c;
        }

        protected void onPreExecute() {
          	dialog = new ProgDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
            dialog.setMessage("Loading...");	
        }

        @Override
        protected void onPostExecute(final Boolean ok) {
        	try {
	            if(dialog.isShowing()) {
	                dialog.dismiss();
	            }
	        } catch (Exception e) { }
        }
        
		@Override
		protected Boolean doInBackground(String... params) {
			dialog.updateMessage("Copying busybox...");
	        CopyAssets();
	        dialog.updateMessage("Setting busybox rights");
	        setNonRootBusyBox();
			return true;
		}
    }
        
    private class serviceTask extends AsyncTask<String, Void, Boolean> {
    	private Activity mActivity;
        private ProgDialog dialog;
        private boolean isRunning = false;
        public serviceTask(Activity activity ) {
        	mActivity = activity;

        }

        protected void onPreExecute() {
          	dialog = new ProgDialog(mActivity);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        	if(DGoverlay.isRunning) {
        		this.isRunning = true;
        		dialog.setMessage("Stopping service...");
        	} else {
        		dialog.setMessage("Starting service...");
        	}
        }

        @Override
        protected void onPostExecute(final Boolean ok) {
        	if(DGoverlay.isRunning) {
        		start.setText("Stop tracking");
        	} else {
        		start.setText("Start tracking");
        		prefEditor.putBoolean("general.running", false);
                prefEditor.commit();  
        	}

        	try {
	            if(dialog.isShowing()) {
	                dialog.dismiss();
	            	mActivity.removeDialog(dialog.hashCode());
	            }
	        } catch (Exception e) { }
        }
        
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				if(this.isRunning) {
					DGoverlay.haltoverlay = true;
					while(DGoverlay.isRunning) Thread.sleep(25);
				} else {
			    	dialog.updateMessage("Cleaning old database entries");
			    	DGdatabase db_object = DGdatabase.getInstance(mContext.getApplicationContext());
			    	db_object.clean((long)(settings.getInt("database.agelimit", 48)*3600000));
		    		startService(service);
		    		while(!DGoverlay.isRunning) Thread.sleep(25);
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			return true;
		}
    }
    
    public void toggle_service(View view) {
    	new serviceTask(this).execute();
    }
    
    private Boolean CopyAssets() {
		if(!new File(mContext.getFilesDir() + "/busybox").exists()) {
	        AssetManager am = mContext.getAssets();
	        try {
	            String fileName = "busybox";
	            InputStream in = am.open("busybox");
	            FileOutputStream f;
	            f  = mContext.openFileOutput(fileName, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE); 
	            byte[] buffer = new byte[1024];
	            int len1 = 0;
	            while ((len1 = in.read(buffer)) > 0) {
	                f.write(buffer, 0, len1);
	            }
	            f.close();
	        } catch (Exception e) {
	            e.printStackTrace();
				Log.d(mContext.getPackageName(), "busybox creation failed.");
				return false;
	        }
	        Log.d(mContext.getPackageName(), "busybox has been successfully created.");
	        return true;
		} else {
			Log.d(mContext.getPackageName(), "busybox found.");
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
			Log.d(this.getPackageName(), "Rights for non root busybox successfully set.");
		} catch (Exception e) {
			if(q!=null) q.destroy();
			Log.d(this.getPackageName(), "Error when trying to set rights for non rooted busybox.");
		}
    }

	@Override
	protected Dialog onCreateDialog(int id) {
	    switch(id) {
	    case 0:
	    	return new AlertDialog.Builder(this)
	    			.setTitle("BUSYBOX error!")
	    			.setCancelable(true)
	    			.setMessage("Could not use our BUSYBOX :-(\nTo prevent unwanted behavior Diagnosis will close now.\nPlease try restarting or reinstalling Diagnosis.\nShould this not help please write me an email:\n(support@thedarken.eu)\nSorry for your troubles!")
	    			.setPositiveButton("Close",
	    					new DialogInterface.OnClickListener() {
	    						@Override
	    						public void onClick(DialogInterface dialog,
	    								int which) {
	    					    	finish();
	    						    //android.os.Process.killProcess(android.os.Process.myPid());
	    						}
	    					})
	    			.create();
	    case 1:
	    	return new AlertDialog.Builder(this)
	    			.setTitle("Database removal")
	    			.setCancelable(true)
	    			.setMessage("This newer version of Diagnosis uses a different structure to store the periodic data.\nTo avoid errors and unwanted behavior, the previous version has been removed. I'm telling you this so that you are not suprised that the database is empty.")
	    			.setPositiveButton("Dismiss",
	    					new DialogInterface.OnClickListener() {
	    						@Override
	    						public void onClick(DialogInterface dialog,
	    								int which) {
	    							
	    						}
	    					})
	    			.create();
	    case 2:
	    	return new AlertDialog.Builder(this)
	    			.setTitle("Error")
	    			.setCancelable(true)
	    			.setMessage("Sorry, something went wrong and you will have to reinstall this app.")
	    			.setNegativeButton("Quit",
	    					new DialogInterface.OnClickListener() {
	    						@Override
	    						public void onClick(DialogInterface dialog,
	    								int which) {
	    					    	finish();
	    						}
	    					})
	    			.create();
	    }
	    Dialog dialog = null;
	    return dialog;
	}
}