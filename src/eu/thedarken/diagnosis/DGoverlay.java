package eu.thedarken.diagnosis;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import eu.thedarken.diagnosis.InfoClass.AppInfo;
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
	private float ALERT_BARRIER_HIGH = 65;
	private float ALERT_BARRIER_MEDIUM = 40;
	private float ALERT_BARRIER_LOW = 1;
	public static boolean use_fahrenheit = false;
	private int overlay_width;
	private int overlay_height;
    public static String external_sd_path = "";
    public static int default_color_normal = 0xff06ff00;
    public static int default_color_alert = 0xffffff00;
    public static int default_color_bg = 0x70000000;
    private final static int NOTIFICATION_ID = 88;
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
        Log.d(mContext.getPackageName(), "Overlay service started");
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
	    Toast.makeText(this.getApplicationContext(), "Diagnosis service created", Toast.LENGTH_SHORT).show();
	    
		Notification note = new Notification(R.drawable.note, "We now know whats going on!", System.currentTimeMillis());
		Intent i = new Intent(this, DGmain.class);

		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

		note.setLatestEventInfo(this, "Diagnosis", "Click me to open the App", pi);
		note.flags |= Notification.FLAG_NO_CLEAR;
		note.flags |= Notification.FLAG_FOREGROUND_SERVICE; 
		note.flags |= Notification.FLAG_ONGOING_EVENT;
		if(settings.getBoolean("general.notification.enabled", true))
			this.startForeground(NOTIFICATION_ID, note);
	}
	
	@Override
	public void onDestroy() {
        Log.d(mContext.getPackageName(), "Overlay service destroyed");
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
	
    private void findExternalSD() {
		if(new File("/mnt/sdcard-ext").exists() && new File("/mnt/sdcard-ext").canRead()) {
			external_sd_path = "/mnt/sdcard-ext";
		} else if(new File(Environment.getExternalStorageDirectory() + "/external_sd").exists()) {
			external_sd_path = Environment.getExternalStorageDirectory() + "/external_sd";
		}  else if(new File("/mnt/emmc").exists() && new File("/mnt/emmc").canRead()) {
			external_sd_path = "/mnt/emmc";
		} else if(new File("/emmc").exists() && new File("/emmc").canRead()) {
			external_sd_path = "/emmc";
		} else if(new File("/mnt/sdcard/_ExternalSD").exists() && new File("/mnt/sdcard/_ExternalSD").canRead()) {
			external_sd_path = "/mnt/sdcard/_ExternalSD";
		} else if(new File(Environment.getExternalStorageDirectory() + "/sd").exists() && new File(Environment.getExternalStorageDirectory() + "/sd").canRead()) {
			external_sd_path = Environment.getExternalStorageDirectory() + "/sd";
		} else if(new File("/mnt/sdcard2").exists() && new File("/mnt/sdcard2").canRead()) {
			external_sd_path = "/mnt/sdcard2";
		} else {
			external_sd_path = "";
		}
		Log.d(mContext.getPackageName(), "External SD found at " + external_sd_path);
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
        Log.d(mContext.getPackageName(), "Configuration changed, reloading...");
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
		        //Log.d(mContext.getPackageName(), "Screen is on, drawing...");
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
		    		
		    		
			        Log.d(mContext.getPackageName(), "reset done");
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
		        //Log.d(mContext.getPackageName(), "Screen is off");
	        }
		    if(haltoverlay) {
		    	haltoverlay = false;
		        Log.d(mContext.getPackageName(), "halted");
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
		    	//<item>Apps >70%CPU</item>
				case 1:
					toset.append(prepHighest(ALERT_BARRIER_HIGH));
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
					toset.append("CPU " + Math.round(data.getCpu().usage) + "%");
					break;
		        //<item>Cpu usage USR</item>
				case 5:
					toset.append("user " + data.getCpu().user + "%");
					break;
		        //<item>Cpu usage SYS</item>
				case 6:
					toset.append("sys " + data.getCpu().system + "%");
					break;
		        //<item>Cpu usage IDLE</item>
				case 7:
					toset.append("idl " + data.getCpu().idle + "%");
					break;
		        //<item>Cpu usage IO</item>
				case 8:
					toset.append("io " + data.getCpu().io + "%");
					break;
		        //<item>Cpu usage NICE</item>
				case 9:
					toset.append("nic " + data.getCpu().nice + "%");
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
					toset.append("DL " + Formatter.formatFileSize(mContext, data.getNet().rate_down) + "/s");
					break;
		        //<item>Upload</item>
				case 17: //Upload
					toset.append("UL " + Formatter.formatFileSize(mContext, data.getNet().rate_up) + "/s");
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
					toset.append((data.getFreq().cpu_frequency/1000) + "Mhz");
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
