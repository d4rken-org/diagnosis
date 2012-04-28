package eu.thedarken.diagnosis;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import eu.thedarken.diagnosis.InfoClass.AppInfo;
import eu.thedarken.diagnosis.InfoClass.FreqInfo;
import eu.thedarken.diagnosis.InfoClass.CpuInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.TrafficStats;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

public class DGoverlay extends Service{
    private SharedPreferences settings;
	private Context mContext;
	private Overlay mOverlay;
	private WindowManager wm;
	public static int INTERVALL = 5000;
	public static boolean isPaused = false;
	private Handler mHandler;
	private static ArrayList<Line> lines = new ArrayList<Line>();
	private boolean clearScreen = false;
	private static boolean reset = true;
	public static boolean haltoverlay = false;
	private DGdata data;
	public static boolean screenON = true;
	private BroadcastReceiver screenOnReciever;
	public static boolean isRunning = false;
	public static float ALERT_BARRIER_CUSTOM = 70;
	private float ALERT_BARRIER_MEDIUM = 40;
	private float ALERT_BARRIER_LOW = 1;
	public static boolean use_fahrenheit = false;
	private int overlay_width;
	private int overlay_height;
    private static ArrayList<ExternalSD> external_sds = new ArrayList<ExternalSD>();
    public static int default_color_normal = 0xff06ff00;
    public static int default_color_alert = 0xffffff00;
    public static int default_color_bg = 0x70000000;
    private final static int NOTIFICATION_ID = 88;
    private final String TAG = "eu.thedarken.diagnosis.DGoverlay";
	public class Line {
		String text = new String();
		int x_pos = 0;
		int y_pos = 0;
		private int fonttype = 1;
		boolean alignright = false;
		Paint textstyle = null;
		ArrayList<Integer> layout = new ArrayList<Integer>();
		Rect bg = null;
		Paint bgstyle = null;
		boolean drawbackground = false;
		private Line() {
			textstyle = new Paint();
			textstyle.setAntiAlias(true);
			textstyle.setShadowLayer(5,5,5,0xff000000);
			setFont(fonttype);
			this.x_pos = 1;
			int y_move = 45;
			this.y_pos = (int)y_move +(lines.size()*15);
			
			bg = new Rect();
			bgstyle = new Paint();
			bgstyle.setColor(Color.BLACK);
			bgstyle.setStyle(Paint.Style.FILL);	
		}
		
		public void setFont(int type) {
			switch(type) {
				case 0:
					textstyle.setTypeface(Typeface.DEFAULT);
					break;
				case 1:
					textstyle.setTypeface(Typeface.DEFAULT_BOLD);
					break;
				case 2:
					textstyle.setTypeface(Typeface.MONOSPACE);
					break;
				case 3:
					textstyle.setTypeface(Typeface.SANS_SERIF);
					break;
				case 4:
					textstyle.setTypeface(Typeface.SERIF);
					break;
			}
			fonttype = type;
		}
	}

	@Override
	public void onCreate() {
		mContext = this;
        settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        PreferenceManager.setDefaultValues(mContext, R.xml.preferences, false);

        data = new DGdata(mContext);
        mOverlay = new Overlay(mContext);
        Log.d( TAG, "Overlay service started");
        super.onCreate();
        
        INTERVALL = (settings.getInt("general.intervall", 5)*1000);
        
        findExternalSD();
        
        use_fahrenheit = settings.getBoolean("layout.usefahrenheit", false);
        
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        overlay_width = outMetrics.widthPixels;
        overlay_height = outMetrics.heightPixels;
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
        		overlay_width,
        		overlay_height,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.TOP;

        wm.addView(mOverlay, params);
        
        mHandler = new Handler();
        mHandler.postDelayed(update, 1);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        screenOnReciever = new ScreenReceiver();
        this.registerReceiver(screenOnReciever, filter);
        
        isRunning = true;
	    Toast.makeText(this.getApplicationContext(), mContext.getString(R.string.diagnosis_service_created), Toast.LENGTH_SHORT).show();
	    
		Notification note = new Notification(R.drawable.note, mContext.getString(R.string.we_now_know_whats_going_on), System.currentTimeMillis());
		Intent i = new Intent(this, DGmain.class);

		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

		note.setLatestEventInfo(this, mContext.getString(R.string.diagnosis), mContext.getString(R.string.click_me_to_open_app), pi);
		note.flags |= Notification.FLAG_NO_CLEAR;
		note.flags |= Notification.FLAG_FOREGROUND_SERVICE; 
		note.flags |= Notification.FLAG_ONGOING_EVENT;
		if(settings.getBoolean("general.notification.enabled", true))
			this.startForeground(NOTIFICATION_ID, note);
	}
	
	@Override
	public void onDestroy() {
        Log.d( TAG, "Overlay service destroyed");
		wm.removeView(mOverlay);
		this.unregisterReceiver(screenOnReciever);
		data.close();
        isRunning = false;
        stopForeground(true);
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY ;

	}

	public static Line getLine(int position) {
		if(lines.size() <= position) {
			return null;
		}
		return lines.get(position);
	}
	
	public static ArrayList<Line> getLines() {
		if(lines == null) return new ArrayList<Line>();
		return lines;
	}
	
    class ExternalSD {
    	File path;
    	boolean isCovered = false;
    	String label = "";
    }
	
	private void findExternalSD() {
		ArrayList<String> canidates = new ArrayList<String>();
		canidates.add("/mnt/sdcard-ext");
		canidates.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/external_sd");
		canidates.add("/mnt/emmc");
		canidates.add("/emmc");
		canidates.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/_ExternalSD");
		canidates.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sd");
		canidates.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sdcard2");
		canidates.add("/mnt/sdcard2");
		canidates.add("/mnt/external1");
		canidates.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ext_sd");
//		canidates.add("/data/sdext2");
		canidates.add("/mnt/usb_storage");
		canidates.add("/mnt/sdcard/removable_sdcard");
		canidates.add("/Removable/MicroSD");
		
		for(String c : canidates) {
			File canidate = new File(c);
			if (canidate.exists() && canidate.canRead()) {
				ExternalSD e = new ExternalSD();
				e.isCovered = alreadyCovered(canidate);
				e.path = canidate;
				e.label = e.path.getName();
				external_sds.add(e);
			}
		}

		for(ExternalSD sd : getExternalSDs())
			Log.d(TAG, "External SD found at " + sd.path.getAbsolutePath().toString());
	}
	
	private boolean alreadyCovered(File f) {
		if (f.getAbsolutePath().toString().contains(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/"))
			return true;
		for(ExternalSD sd : getExternalSDs()) {
			if(sd.path.getAbsolutePath().toString().contains(f.getAbsolutePath().toString()))
				return true;
		}
		return false;
	}
	
   public static ArrayList<ExternalSD> getExternalSDs() {
    	return external_sds;
    }
	
	class Overlay extends ViewGroup {
	    public Overlay(Context context) {
	        super(context);
	    }

	    @Override
	    protected void onDraw(Canvas canvas) {
	        super.onDraw(canvas);
	        if(clearScreen) {
	        	canvas.drawColor(0, PorterDuff.Mode.CLEAR);	
	        	clearScreen = false;
	        } else {
		        for(Line l : lines) {
		        	int x = 0;
		        	if(l.alignright) {
		        		l.x_pos = overlay_width;
		        		x = (int) (l.x_pos - l.textstyle.measureText(l.text));
		        	} else {
		        		x = l.x_pos;
		        	}
		        	if(l.y_pos > overlay_height) {
		        		l.y_pos = overlay_height;
		        	}
		        	
		        	if(l.drawbackground) {
						l.textstyle.getTextBounds(l.text, 0, l.text.length(), l.bg);
						canvas.translate(x, l.y_pos);
						canvas.drawRect(l.bg, l.bgstyle);
						canvas.translate(-x, -l.y_pos);
		        	}
		        	
		        	canvas.drawText(l.text, x, l.y_pos, l.textstyle);
		        }
	        }
	    }

	    @Override
	    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
	    	
	    }
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);       
        Log.d( TAG, "Configuration changed, reloading...");
        reset = true;
	}
	
	public class ScreenReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
	        	DGoverlay.screenON = false;
	        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
	        	DGoverlay.screenON = true;
	        }
	    }

	}
	
	public static void initReset() {
		reset = true;
	}
	
	@SuppressWarnings("unchecked")
	private Runnable update=new Runnable() {
	    public void run() {
	        data.update();
	        if(screenON) {
		    	if(reset) {
		            DisplayMetrics outMetrics = new DisplayMetrics();
		            wm.getDefaultDisplay().getMetrics(outMetrics);
		            overlay_width = outMetrics.widthPixels;
		            overlay_height = outMetrics.heightPixels;
		            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
		            		overlay_width,
		            		overlay_height,
		                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
		                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
		                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
		                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
		                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
		                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
		                    PixelFormat.TRANSLUCENT);
		            params.gravity = Gravity.LEFT | Gravity.TOP;

		            wm.updateViewLayout(mOverlay, params);
		            
		            use_fahrenheit = settings.getBoolean("layout.usefahrenheit", false);
		            
		            
		    		lines.clear();
		    		Line init;
		    		int line;
		    		
		    		line = 0;
		    		init = new Line();
		    		init.layout = (ArrayList<Integer>) ObjectSerializer.deserialize(settings.getString("layout.line" + line, ""));
		    		if(init.layout == null) init.layout = new ArrayList<Integer>();
		    		init.alignright = settings.getBoolean("overlay.align.right.line" + line, false);
		    		init.setFont(Integer.parseInt(settings.getString("overlay.font.type.line" + line, "0")));
		    		init.x_pos = Integer.parseInt(settings.getString("overlay.x_pos.line" + line, "1"));
		    		init.y_pos = Integer.parseInt(settings.getString("overlay.y_pos.line" + line, "45"));
		    		init.textstyle.setTextSize(settings.getInt("overlay.font.size.line" + line, 15));
		    		init.textstyle.setColor(settings.getInt("overlay.color.normal.line" + line,default_color_normal));
		    		init.bgstyle.setColor(settings.getInt("overlay.color.background.line" + line,default_color_bg));
		    		init.drawbackground = settings.getBoolean("overlay.drawbackground.line" + line, false);
		    		lines.add(init);
		    		
		    		line = 1;
		    		init = new Line();
		    		init.layout = (ArrayList<Integer>) ObjectSerializer.deserialize(settings.getString("layout.line" + line, ""));
		    		if(init.layout == null) init.layout = new ArrayList<Integer>();
		    		init.alignright = settings.getBoolean("overlay.align.right.line" + line, false);
		    		init.setFont(Integer.parseInt(settings.getString("overlay.font.type.line" + line, "0")));
		    		init.x_pos = Integer.parseInt(settings.getString("overlay.x_pos.line" + line, "1"));
		    		init.y_pos = Integer.parseInt(settings.getString("overlay.y_pos.line" + line, "60"));
		    		init.textstyle.setTextSize(settings.getInt("overlay.font.size.line" + line, 15));
		    		init.textstyle.setColor(settings.getInt("overlay.color.normal.line" + line,default_color_normal));
		    		init.bgstyle.setColor(settings.getInt("overlay.color.background.line" + line,default_color_bg));
		    		init.drawbackground = settings.getBoolean("overlay.drawbackground.line" + line, false);
		    		lines.add(init);
		    		
		    		line = 2;
		    		init = new Line();
		    		init.layout = (ArrayList<Integer>) ObjectSerializer.deserialize(settings.getString("layout.line" + line, ""));
		    		if(init.layout == null) init.layout = new ArrayList<Integer>();
		    		init.alignright = settings.getBoolean("overlay.align.right.line" + line, false);
		    		init.setFont(Integer.parseInt(settings.getString("overlay.font.type.line" + line, "0")));
		    		init.x_pos = Integer.parseInt(settings.getString("overlay.x_pos.line" + line, "1"));
		    		init.y_pos = Integer.parseInt(settings.getString("overlay.y_pos.line" + line, "75"));
		    		init.textstyle.setTextSize(settings.getInt("overlay.font.size.line" + line, 15));
		    		init.textstyle.setColor(settings.getInt("overlay.color.normal.line" + line,default_color_normal));
		    		init.bgstyle.setColor(settings.getInt("overlay.color.background.line" + line,default_color_bg));
		    		init.drawbackground = settings.getBoolean("overlay.drawbackground.line" + line, false);
		    		lines.add(init);
		    		
		    		line = 3;
		    		init = new Line();
		    		init.layout = (ArrayList<Integer>) ObjectSerializer.deserialize(settings.getString("layout.line" + line, ""));
		    		if(init.layout == null) init.layout = new ArrayList<Integer>();
		    		init.alignright = settings.getBoolean("overlay.align.right.line" + line, false);
		    		init.setFont(Integer.parseInt(settings.getString("overlay.font.type.line" + line, "0")));
		    		init.x_pos = Integer.parseInt(settings.getString("overlay.x_pos.line" + line, "1"));
		    		init.y_pos = Integer.parseInt(settings.getString("overlay.y_pos.line" + line, "90"));
		    		init.textstyle.setTextSize(settings.getInt("overlay.font.size.line" + line, 15));
		    		init.textstyle.setColor(settings.getInt("overlay.color.normal.line" + line,default_color_normal));
		    		init.bgstyle.setColor(settings.getInt("overlay.color.background.line" + line,default_color_bg));
		    		init.drawbackground = settings.getBoolean("overlay.drawbackground.line" + line, false);
		    		lines.add(init);
		    		
		    		
			        Log.d( TAG, "reset done");
		    		setLine(0);
		    		setLine(1);
		    		setLine(2);
		    		setLine(3);
		    		reset = false;
		    	} else {
		    		setLine(0);
		    		setLine(1);
		    		setLine(2);
		    		setLine(3);
		    	}
		    	mOverlay.invalidate();
	        } else {
	        	//Screen was off
	        }
		    if(haltoverlay) {
		    	haltoverlay = false;
		        Log.d( TAG, "halted");
		        reset = true;
		        stopSelf();
		    } else {
		    	mHandler.postDelayed(update, INTERVALL);
		    }
	    }
	};
	
	private void setLine(int line) {
		StringBuilder toset = new StringBuilder();
		LinkedList<Integer> tobuild = new LinkedList<Integer>();
		tobuild.addAll(lines.get(line).layout);
	    Integer item;
		while(!tobuild.isEmpty()) {
			item = tobuild.removeFirst();		
			switch(item) {
		        //<item>Select something</item>
				case 0:
					toset.append("");
					break;
		    	//<item>Apps >X%CPU</item>
				case 1:
					toset.append(prepHighest(ALERT_BARRIER_CUSTOM));
					if(toset.length() > 0) {
						lines.get(line).textstyle.setColor(settings.getInt("overlay.color.alert.line"+line, default_color_alert));
					} else {
						lines.get(line).textstyle.setColor(settings.getInt("overlay.color.normal.line"+line, default_color_normal));
					}
					break;
		        //<item>Apps >40%CPU</item>
				case 2:
					toset.append(prepHighest(ALERT_BARRIER_MEDIUM));
					if(toset.length() > 0) {
						lines.get(line).textstyle.setColor(settings.getInt("overlay.color.alert.line"+line, default_color_alert));
					} else {
						lines.get(line).textstyle.setColor(settings.getInt("overlay.color.normal.line"+line, default_color_normal));
					}
					break;
		        //<item>Apps >1% CPU</item>
				case 3:
					toset.append(prepHighest(ALERT_BARRIER_LOW));
					if(toset.length() > 0) {
						lines.get(line).textstyle.setColor(settings.getInt("overlay.color.alert.line"+line, default_color_alert));
					} else {
						lines.get(line).textstyle.setColor(settings.getInt("overlay.color.normal.line"+line, default_color_normal));
					}
					break;
		        //<item>Total Cpu use</item>
				case 4:
					toset.append("CPU " + (int)CpuInfo.calcAvgCpu(data.getCpu().usage) + "%");
					break;
		        //<item>Cpu usage USR</item>
				case 5:
					toset.append("user " + (int)CpuInfo.calcAvgCpu(data.getCpu().user) + "%");
					break;
		        //<item>Cpu usage SYS</item>
				case 6:
					toset.append("sys " + (int)CpuInfo.calcAvgCpu(data.getCpu().system) + "%");
					break;
		        //<item>Cpu usage IDLE</item>
				case 7:
					toset.append("idl " + (int)CpuInfo.calcAvgCpu(data.getCpu().idle) + "%");
					break;
		        //<item>Cpu usage IO</item>
				case 8:
					toset.append("io " + (int)CpuInfo.calcAvgCpu(data.getCpu().io) + "%");
					break;
		        //<item>Cpu usage NICE</item>
				case 9:
					toset.append("nic " + (int)CpuInfo.calcAvgCpu(data.getCpu().nice) + "%");
					break;
		        //<item>Total free memory</item>
				case 10:
					toset.append("MEM " + Formatter.formatFileSize(mContext,(long)(data.getMem().total_free)));
					break;
		        //<item>Real free memory</item>
				case 11:
					toset.append("free " + Formatter.formatFileSize(mContext,(long)(data.getMem().free)));
					break;
		        //<item>Used memory</item>
				case 12:
					toset.append("used " + Formatter.formatFileSize(mContext,(long)(data.getMem().used)));
					break;
		        //<item>Buffered memory</item>
				case 13:
					toset.append("buff " + Formatter.formatFileSize(mContext,(long)(data.getMem().buff)));
					break;
		        //<item>Cached memory</item>
				case 14:
					toset.append("cache " + Formatter.formatFileSize(mContext,(long)(data.getMem().cached)));
					break;
				//<item>System time</item>    
				case 15:
					toset.append("Systime:" + System.currentTimeMillis());
					break;
		        //<item>Download</item>
				case 16: //Download
					if(data.getNet().rate_down == TrafficStats.UNSUPPORTED) {
						toset.append("N/A");
					} else {
						toset.append("DL " + Formatter.formatFileSize(mContext, data.getNet().rate_down) + "/s");
					}
					break;
		        //<item>Upload</item>
				case 17: //Upload
					if(data.getNet().rate_up == TrafficStats.UNSUPPORTED) {
						toset.append("N/A");
					} else {
						toset.append("UL " + Formatter.formatFileSize(mContext, data.getNet().rate_up) + "/s");
					}
					break;
		        //<item>System load</item>
				case 18: //Load
					toset.append("L " + data.getLoad().first + " " + data.getLoad().second + " " + data.getLoad().third);
					break;
		        //<item>Active app count</item>
				case 19: //Active apps
					toset.append("Apps" + "(" + data.getCpu().act_apps_cur +")");
					break;
		        //<item>Battery level</item>
				case 20: //Batt level
					toset.append(data.getBatt().level + "%");
					break;
		        //<item>Battery voltage</item>
				case 21: //Batt voltage
					toset.append(((float)data.getBatt().voltage/1000) + "V");
					break;
		        //<item>Battery temp</item>
				case 22: //Batt temp
					toset.append(data.getBatt().formatTemp(data.getBatt().batt_temp_cur,use_fahrenheit));
					break;
		        //<item>Time</item>
				case 23: //Time
					toset.append(new SimpleDateFormat("HH:mm:ss").format(new Date()));
					break;
		        //<item>Date</item>
				case 24: //Date
					toset.append(DateFormat.getDateInstance().format(new Date()));
					break;
		        //<item>Disk READ</item>
				case 25: //Disk read rate
					toset.append("R " + Formatter.formatFileSize(mContext, data.getDisk().read_rate) + "/s");
					break;
		        //<item>Disk WRITE</item>
				case 26: //Disk write rate
					toset.append("W " + Formatter.formatFileSize(mContext, data.getDisk().write_rate) + "/s");
					break;
		        //<item>CPU frequency</item>
				case 27:
					toset.append((FreqInfo.calcAvgCoreFrequency(data.getFreq().cpu_frequency)/1000) + "Mhz");
					break;
				//<item>Free external space</item>
				case 28:
					toset.append("Ext " + Formatter.formatFileSize(mContext,(data.getSpace().extern_total-data.getSpace().extern_used)));
					break;
				//<item>Free sdcard space</item>
				case 29:
					toset.append("SD " + Formatter.formatFileSize(mContext,(data.getSpace().sdcard_total-data.getSpace().sdcard_used)));
					break;
					//<item>Free system space</item>
				case 30:
					toset.append("Sys " + Formatter.formatFileSize(mContext,(data.getSpace().system_total-data.getSpace().system_used)));
					break;
				//<item>Free internal space</item>
				case 31:
					toset.append("Data " + Formatter.formatFileSize(mContext,(data.getSpace().data_total-data.getSpace().data_used)));
					break;
				//<item>Ping to google.com</item>
				case 32:
					long ping = data.getPing().ping;
					if(ping > 500) {
						toset.append("Ping >500ms");
					} else {
						toset.append("Ping " + ping + "ms");
					}
					break;
				//<item>WIFI signal strength</item>
				case 33:
					toset.append("Wifi " + data.getWlan().formatSignal(data.getWlan().signal));
					break;
				//<item>CELL signal strength</item>
				case 34:
					toset.append("Cell " + data.getPhone().formatSignal(data.getPhone().gsm_signal));
					break;
				case 35:
					if(data.getWlan().ip == 0) {
						toset.append("no ip");
					} else {
						toset.append(Formatter.formatIpAddress(data.getWlan().ip));
					}
					break;
				case 36:
					if(data.getWlan().name.length() > 0) {
						toset.append(data.getWlan().name);
					} else {
						toset.append("no wifi");
					}
					break;
				case 37:
					if(data.getWlan().linkspeed > 0) {
						toset.append(data.getWlan().linkspeed + "Mbps");
					} else {
						toset.append("no link");
					}
					break;
				case 38:
					if(data.getNet().mobile_rate_down == TrafficStats.UNSUPPORTED) {
						toset.append("N/A");
					} else {
						toset.append("cDL " + Formatter.formatFileSize(mContext, data.getNet().mobile_rate_down) + "/s");
					}
					break;
				case 39:
					if(data.getNet().mobile_rate_up == TrafficStats.UNSUPPORTED) {
						toset.append("N/A");
					} else {
						toset.append("cUL " + Formatter.formatFileSize(mContext, data.getNet().mobile_rate_up) + "/s");
					}
					break;
			        //<item>Core1 frequency</item>
				case 40:
					if(DGdata.CORES > 0)
						toset.append("C1 " + (data.getFreq().cpu_frequency[0]/1000) + "Mhz");
					else
						toset.append("C1 N/A");
					break;
			        //<item>Core2 frequency</item>
				case 41:
					if(DGdata.CORES > 1)
						toset.append("C2 " + (data.getFreq().cpu_frequency[1]/1000) + "Mhz");
					else
						toset.append("C2 N/A");
					break;
			        //<item>Core3 frequency</item>
				case 42:
					if(DGdata.CORES > 2)
						toset.append("C3 " + (data.getFreq().cpu_frequency[2]/1000) + "Mhz");
					else
						toset.append("C3 N/A");
					break;
					
					//<item>Core#1 usage [Pro]</item><!-- 44 -->
				case 44:
					if(DGdata.CORES > 0)
						toset.append("C1 " + (int)data.getCpu().usage[0] + "%");
					else
						toset.append("C1 N/A");
					break;
					//<item>Core#1 USR [Pro]</item><!-- 45 -->
				case 45:
					if(DGdata.CORES > 0)
						toset.append("C1[usr] " + (int)data.getCpu().user[0] + "%");
					else
						toset.append("C1[usr] N/A");
					break;
					//<item>Core#1 SYS [Pro]</item><!-- 46 -->
				case 46:
					if(DGdata.CORES > 0)
						toset.append("C1[sys] " + (int)data.getCpu().system[0] + "%");
					else
						toset.append("C1[sys] N/A");
					break;
					//<item>Core#1 IDLE [Pro]</item><!-- 47 -->
				case 47:
					if(DGdata.CORES > 0)
						toset.append("C1[idl] " + (int)data.getCpu().idle[0] + "%");
					else
						toset.append("C1[idl] N/A");
					break;
					//<item>Core#1 IO [Pro]</item><!-- 48 -->
				case 48:
					if(DGdata.CORES > 0)
						toset.append("C1[io] " + (int)data.getCpu().io[0] + "%");
					else
						toset.append("C1[io] N/A");
					break;
					//<item>Core#1 NICE [Pro]</item><!-- 49 -->
				case 49:
					if(DGdata.CORES > 0)
						toset.append("C1[nic] " + (int)data.getCpu().nice[0] + "%");
					else
						toset.append("C1[nic] N/A");
					break; 
					//<item>Core#2 usage [Pro]</item><!-- 50 -->
				case 50:
					if(DGdata.CORES > 1)
						toset.append("C2 " + (int)data.getCpu().usage[1] + "%");
					else
						toset.append("C2 N/A");
					break;
					//<item>Core#2 USR [Pro]</item><!-- 51 -->
				case 51:
					if(DGdata.CORES > 1)
						toset.append("C2[usr] " + (int)data.getCpu().user[1] + "%");
					else
						toset.append("C2[usr] N/A");
					break;
					//<item>Core#2 SYS [Pro]</item><!-- 52 -->
				case 52:
					if(DGdata.CORES > 1)
						toset.append("C2[sys] " + (int)data.getCpu().system[1] + "%");
					else
						toset.append("C2[sys] N/A");
					break;
					//<item>Core#2 IDLE [Pro]</item><!-- 53 -->
				case 53:
					if(DGdata.CORES > 1)
						toset.append("C2[idl] " + (int)data.getCpu().idle[1] + "%");
					else
						toset.append("C2[idl] N/A");
					break;
					//<item>Core#2 IO [Pro]</item><!-- 54 -->
				case 54:
					if(DGdata.CORES > 1)
						toset.append("C2[io] " + (int)data.getCpu().io[1] + "%");
					else
						toset.append("C2[io] N/A");
					break;
					//<item>Core#2 NICE [Pro]</item><!-- 55 -->
				case 55:
					if(DGdata.CORES > 1)
						toset.append("C2[nic] " + (int)data.getCpu().nice[1] + "%");
					else
						toset.append("C2[nic] N/A");
					break; 
					//<item>Core#3 usage [Pro]</item><!-- 56 -->
				case 56:
					if(DGdata.CORES > 2)
						toset.append("C3 " + (int)data.getCpu().usage[2] + "%");
					else
						toset.append("C3 N/A");
					break;
					//<item>Core#3 USR [Pro]</item><!-- 57 -->
				case 57:
					if(DGdata.CORES > 2)
						toset.append("C3[usr] " + (int)data.getCpu().user[2] + "%");
					else
						toset.append("C3[usr] N/A");
					break;
					//<item>Core#3 SYS [Pro]</item><!-- 58 -->
				case 58:
					if(DGdata.CORES > 2)
						toset.append("C3[sys] " + (int)data.getCpu().system[2] + "%");
					else
						toset.append("C3[sys] N/A");
					break;
					//<item>Core#3 IDLE [Pro]</item><!-- 59 -->
				case 59:
					if(DGdata.CORES > 2)
						toset.append("C3[idl] " + (int)data.getCpu().idle[2] + "%");
					else
						toset.append("C3[idl] N/A");
					break;
					//<item>Core#3 IO [Pro]</item><!-- 60 -->
				case 60:
					if(DGdata.CORES > 2)
						toset.append("C3[io] " + (int)data.getCpu().io[2] + "%");
					else
						toset.append("C3[io] N/A");
					break;
					//<item>Core#3 NICE [Pro]</item><!-- 61 -->
				case 61:
					if(DGdata.CORES > 2)
						toset.append("C3[nic] " + (int)data.getCpu().nice[2] + "%");
					else
						toset.append("C3[nic] N/A");
					break; 
					//<item>Core#4 usage [Pro]</item><!-- 62 -->
				case 62:
					if(DGdata.CORES > 3)
						toset.append("C4 " + (int)data.getCpu().usage[3] + "%");
					else
						toset.append("C4 N/A");
					break;
					//<item>Core#4 USR [Pro]</item><!-- 63 -->
				case 63:
					if(DGdata.CORES > 3)
						toset.append("C4[usr] " + (int)data.getCpu().user[3] + "%");
					else
						toset.append("C4[usr] N/A");
					break;
					//<item>Core#4 SYS [Pro]</item><!-- 64 -->
				case 64:
					if(DGdata.CORES > 3)
						toset.append("C4[sys] " + (int)data.getCpu().system[3] + "%");
					else
						toset.append("C4[sys] N/A");
					break;
					//<item>Core#4 IDLE [Pro]</item><!-- 65 -->
				case 65:
					if(DGdata.CORES > 3)
						toset.append("C4[idl] " + (int)data.getCpu().idle[3] + "%");
					else
						toset.append("C4[idl] N/A");
					break;
					//<item>Core#4 IO [Pro]</item><!-- 66 -->
				case 66:
					if(DGdata.CORES > 3)
						toset.append("C4[io] " + (int)data.getCpu().io[3] + "%");
					else
						toset.append("C4[io] N/A");
					break;
					//<item>Core#4 NICE [Pro]</item><!-- 67 -->
				case 67:
					if(DGdata.CORES > 3)
						toset.append("C4[nic] " + (int)data.getCpu().nice[3] + "%");
					else
						toset.append("C4[nic] N/A");
					break; 
				default:
					toset.append("");
					break;
			}
			if(!tobuild.isEmpty()) toset.append(settings.getString("overlay.divider", "|"));
		}

		lines.get(line).text = toset.toString();
	}
	
	String prepHighest(float barrier) {
		ArrayList<AppInfo> apps = data.getAppsByCpu(barrier);
		int cutoff = 15;
		if(apps.size()==0) return "";
		StringBuilder ret = new StringBuilder();
		for(AppInfo a : apps) {
			if(a.cpu > barrier)
				if(settings.getBoolean("general.database.hidesystem", false)) {
					if((a.command.startsWith("[") && a.command.endsWith("]"))) {
						continue;
					}
				}
				if(a.command.length()>=cutoff) {
					ret.append(".." + a.command.substring(a.command.length()-cutoff) + "@" + a.cpu + "%" + " ");
				} else {
					ret.append(a.command + "@" + a.cpu + "%" + " ");
				}
		}
		return ret.toString();
	}
}
