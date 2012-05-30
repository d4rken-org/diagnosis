package eu.thedarken.diagnosis;

import java.util.ArrayList;
import java.util.LinkedList;
import eu.thedarken.diagnosis.InfoClass.AppInfo;
import eu.thedarken.diagnosis.InfoClass.AppTabInfo;
import eu.thedarken.diagnosis.InfoClass.BattInfo;
import eu.thedarken.diagnosis.InfoClass.BattTabInfo;
import eu.thedarken.diagnosis.InfoClass.CpuInfo;
import eu.thedarken.diagnosis.InfoClass.CpuTabInfo;
import eu.thedarken.diagnosis.InfoClass.DiskTabInfo;
import eu.thedarken.diagnosis.InfoClass.FreqInfo;
import eu.thedarken.diagnosis.InfoClass.FreqTabInfo;
import eu.thedarken.diagnosis.InfoClass.DiskInfo;
import eu.thedarken.diagnosis.InfoClass.LoadInfo;
import eu.thedarken.diagnosis.InfoClass.MemInfo;
import eu.thedarken.diagnosis.InfoClass.MemTabInfo;
import eu.thedarken.diagnosis.InfoClass.NetInfo;
import eu.thedarken.diagnosis.InfoClass.NetTabInfo;
import eu.thedarken.diagnosis.InfoClass.PhoneInfo;
import eu.thedarken.diagnosis.InfoClass.PhoneTabInfo;
import eu.thedarken.diagnosis.InfoClass.PingTabInfo;
import eu.thedarken.diagnosis.InfoClass.SpaceInfo;
import eu.thedarken.diagnosis.InfoClass.SpaceTabInfo;
import eu.thedarken.diagnosis.InfoClass.WlanInfo;
import eu.thedarken.diagnosis.InfoClass.WlanTabInfo;
import eu.thedarken.diagnosis.InfoClass.PingInfo;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class DGdatabase {
	static final String CPU_TABLE = "cpu_data";
	static final String FREQ_TABLE = "freq_data";
	static final String MEM_TABLE = "mem_data";
	static final String LOAD_TABLE = "load_data";
	static final String NET_TABLE = "net_data";
	static final String BATT_TABLE = "batt_data";
	static final String APP_TABLE = "app_data";
	static final String SPACE_TABLE = "space_data";
	static final String DISK_TABLE = "disk_data";
	static final String WLAN_TABLE = "wlan_data";
	static final String PHONE_TABLE = "phone_data";
	static final String PING_TABLE = "ping_data";
	
	static String create_cpu_data;
	
	static String create_freq_data;
	
	static final String create_mem_data = "create table IF NOT EXISTS "+MEM_TABLE+" " +
	    "(system_time long, " +
	    "free long, " +
	    "total_free long, " +
	    "used long, " +
	    "shared long, " +
	    "buff long, " +
	    "cached long, " +
	    "total long, " +
	    "usage float); ";
	static final String create_load_data = "create table IF NOT EXISTS "+LOAD_TABLE+" " +
	    "(system_time long, " +
	    "first float, " +
	    "second float, " +
	    "third float, " +
	    "fourth string, " +
	    "fifth integer); ";
	static final String create_net_data = "create table IF NOT EXISTS "+NET_TABLE+" " +
	    "(system_time long, " +
	    "traffic_up long, " +
	    "traffic_down long, " +
	    "rate_up long, " +
	    "rate_down long, " +
	    "mobile_traffic_up long, " +
	    "mobile_traffic_down long, " +
	    "mobile_rate_up long, " +
	    "mobile_rate_down long); ";
	static final String create_batt_data = "create table IF NOT EXISTS "+BATT_TABLE+" " +
	    "(system_time long, " +
	    "scale integer, " +
	    "level integer, " +
	    "voltage integer, " +
	    "health integer, " +
	    "status integer, " +
	    "power integer, " +
	    "tech string, " +
	    "temp integer); ";
	static final String create_app_data = "create table IF NOT EXISTS " + APP_TABLE + " " +
	    "(command string, " +
	    "system_time long, " +
	    "cpu float, " +
	    "mem float, " +
	    "vsz string); ";
	static final String create_space_data = "create table IF NOT EXISTS " + SPACE_TABLE + " " +
	    "(system_time long, " +
	    "extern_total long, " +
	    "extern_used long, " +
	    "sdcard_total long, " +
	    "sdcard_used long, " +
	    "system_total long, " +
	    "system_used long, " +
	    "data_total long, " +
	    "data_used long); ";
	static final String create_disk_data = "create table IF NOT EXISTS " + DISK_TABLE + " " +
	    "(system_time long, " +
	    "read long, " +
	    "written long, " +
	    "read_rate long, " +
	    "write_rate); ";
	static final String create_wlan_data = "create table IF NOT EXISTS "+WLAN_TABLE+" " +
	    "(system_time long, " +
	    "signal int); ";
	static final String create_phone_data = "create table IF NOT EXISTS "+PHONE_TABLE+" " +
	    "(system_time long, " +
	    "gsm_signal int); ";
	static final String create_ping_data = "create table IF NOT EXISTS "+PING_TABLE+" " +
    "(system_time long, " +
    "ping int); ";
    public static int graph_data_points = 50;
    
    private static Context mContext = null;
    //MEMBER VARIABLES
    private SQLiteDatabase mDB;
    private DatabaseHelper mDBhelper;
    private final String TAG = "eu.thedarken.diagnosis.DGdatabase";
    //SINGLETON
    private static final DGdatabase instance = new DGdatabase();
    
    public DGdatabase() {
        //open the DB for read and write
        //mDB = mDBhelper.getWritableDatabase();
    	StringBuilder freqtablebuilder = new StringBuilder();
    	freqtablebuilder.append("create table IF NOT EXISTS "+FREQ_TABLE+" ");
    	freqtablebuilder.append("(system_time long");
    	for(int i=0;i<DGdata.CORES;i++) {
    		freqtablebuilder.append(", cpu"+i+"_frequency integer,");
			freqtablebuilder.append("cpu"+i+"_max_frequency integer, ");
			freqtablebuilder.append("cpu"+i+"_min_frequency integer ");
    	}
    	freqtablebuilder.append(");");
    	create_freq_data = freqtablebuilder.toString();
    	
    	StringBuilder cputablebuilder = new StringBuilder();
    	cputablebuilder.append("create table IF NOT EXISTS "+CPU_TABLE+" ");
    	cputablebuilder.append("(system_time long");
 	    for(int i=0;i<DGdata.CORES;i++) {
 	    	cputablebuilder.append(", core"+i+"_usage float, ");
 	    	cputablebuilder.append("core"+i+"_user float, ");
 	    	cputablebuilder.append("core"+i+"_nice float, ");
 	    	cputablebuilder.append("core"+i+"_system float, ");
 	    	cputablebuilder.append("core"+i+"_idle float, ");
 	    	cputablebuilder.append("core"+i+"_io float");
 	    }
 	    cputablebuilder.append(", active_apps integer); ");
 	    create_cpu_data = cputablebuilder.toString();
    }
    
    public static DGdatabase getInstance(Context context) {
    	mContext = context;
        return instance;
    }

    
    private static class DatabaseHelper extends SQLiteOpenHelper {
        private ArrayList<String> create_tables = new ArrayList<String>();
        private ArrayList<String> tables = new ArrayList<String>();
        public static String DBPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/eu.thedarken.diagnosis/databases/database.db";
        DatabaseHelper(Context context) {
            super(context, DBPATH, null, 1);
            tables.add(DGdatabase.CPU_TABLE);
            tables.add(DGdatabase.FREQ_TABLE);
            tables.add(DGdatabase.MEM_TABLE);
            tables.add(DGdatabase.LOAD_TABLE);
            tables.add(DGdatabase.NET_TABLE);
            tables.add(DGdatabase.BATT_TABLE);    
            tables.add(DGdatabase.APP_TABLE);
            tables.add(DGdatabase.SPACE_TABLE);
            tables.add(DGdatabase.DISK_TABLE);
            tables.add(DGdatabase.WLAN_TABLE);
            tables.add(DGdatabase.PHONE_TABLE);
            tables.add(DGdatabase.PING_TABLE);

            create_tables.add(create_cpu_data);
            create_tables.add(create_freq_data);
            create_tables.add(create_mem_data);
            create_tables.add(create_load_data);
            create_tables.add(create_net_data);
            create_tables.add(create_batt_data);
            create_tables.add(create_app_data);
            create_tables.add(create_space_data);
            create_tables.add(create_disk_data);
            create_tables.add(create_wlan_data);
            create_tables.add(create_phone_data);
            create_tables.add(create_ping_data);
        }


        //this is called for first time db is created.
        // put all CREATE TABLE here
        @Override
        public void onCreate(SQLiteDatabase db) {
        	createTables(db);
        }

        //this is called when an existing user updates to a newer version of the app
        // add CREATE TABLE and ALTER TABLE here
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
        
        private void createTables(SQLiteDatabase db) {
        	if(db != null) {
            	for(String t : create_tables) {
            		db.execSQL(t);
            	}
        	}
        }
        
        private ArrayList<String> getTables() {
        	return tables;
        }
    }
 
    private synchronized boolean openWrite() {
		if(mDB != null && mDB.isOpen()) {
			return true;
		} else {
			try {
				if(mDBhelper == null) mDBhelper = new DatabaseHelper(mContext);
		    	mDB = mDBhelper.getWritableDatabase();
		    	//Log.d(mContext.getPackageName(), "DB write open");
		    	return mDB.isOpen();
			} catch (SQLiteException e) {
				e.printStackTrace();
				return false;
			}
    	}
    }
    
    private synchronized boolean openRead() {
		if(mDB != null && mDB.isOpen()) {
			return true;
		} else {
			if(mDBhelper == null) mDBhelper = new DatabaseHelper(mContext);
			try {
		    	mDB = mDBhelper.getReadableDatabase();
		    	//Log.d(mContext.getPackageName(), "DB read open");
		    	return mDB.isOpen();
			} catch (SQLiteException e) {
				e.printStackTrace();
				return false;
			}
    	}
    }
    
    private synchronized boolean tryClose() {
    	if(mDB != null && mDB.isOpen()){
	    	mDB.close();
	    	SQLiteDatabase.releaseMemory();
        	//Log.d(mContext.getPackageName(), "DB close");
        	if(mDB == null || !mDB.isOpen()) {
        		return true;
        	} else {
        		return false;
        	}
    	}	
    	return true;
    }
    
	public synchronized void clean(long time, ProgDialog p) {
		if (openWrite()) {
			try {
				Log.d(TAG, "Cleaning old entries");
				for (String t : mDBhelper.getTables()) {
					p.updateMessage("Cleaning " + t);
					mDB.execSQL("DELETE FROM " + t + " WHERE system_time<" + (System.currentTimeMillis() - time) + ";");
					Log.d(TAG, "Done cleaning " + t);
					p.incrProgress();
				}
			} catch (SQLiteException e) {
				tryClose();
			}
		}
		tryClose();
	}

	public void init() {
		if(openRead())
			tryClose();
	}
	
	public int getTableSize() {
		if (openRead()) {
			int size = 0;
			size = mDBhelper.getTables().size();
			tryClose();
			return size;
		}
		return 0;
	}
    
    public synchronized void addCpus(LinkedList<CpuInfo> c, boolean keepopen) {
    	if(openWrite()) {
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(mDB,CPU_TABLE);
            int[] usage = new int[DGdata.CORES];
            int[] user = new int[DGdata.CORES];
            int[] nice = new int[DGdata.CORES];
            int[] system = new int[DGdata.CORES];
            int[] idle = new int[DGdata.CORES];
            int[] io = new int[DGdata.CORES];
            for(int i=0;i<DGdata.CORES;i++) {
                usage[i] = ih.getColumnIndex("core"+i+"_usage");
                user[i] = ih.getColumnIndex("core"+i+"_user");
                nice[i] = ih.getColumnIndex("core"+i+"_nice");
                system[i] = ih.getColumnIndex("core"+i+"_system");
                idle[i] = ih.getColumnIndex("core"+i+"_idle");
                io[i] = ih.getColumnIndex("core"+i+"_io");
            }
            int act_apps_cur = ih.getColumnIndex("active_apps");
            int system_time = ih.getColumnIndex("system_time");
            for(CpuInfo ci : c) {
                ih.prepareForInsert();
            	for(int i=0;i<ci.usage.length;i++) {
	            	ih.bind(usage[i], ci.usage[i]);
	            	ih.bind(user[i], ci.user[i]);
	            	ih.bind(nice[i], ci.nice[i]);
	            	ih.bind(system[i], ci.system[i]);
	            	ih.bind(idle[i], ci.idle[i]);
	            	ih.bind(io[i], ci.io[i]);
            	}
            	ih.bind(act_apps_cur, ci.act_apps_cur);
            	ih.bind(system_time, ci.system_time);
            	ih.execute();
            }
            ih.close();
    	}
    	if(!keepopen) {
    		tryClose();
    	}
    }
    
    public synchronized CpuTabInfo getCpuTabInfo() {
    	CpuTabInfo ret = new CpuTabInfo();
    	if(openRead()) {
	    	Cursor c = null;
	    	try {
	    		StringBuilder querrybuilder = new StringBuilder();
	    		querrybuilder.append("SELECT ");
	    		querrybuilder.append("system_time, active_apps, AVG(Cast(active_apps AS Float)) AS avg_act_apps, MAX(active_apps) AS max_act_apps");
	    		for(int i=0;i<ret.usage.length;i++)
	    			querrybuilder.append(",  AVG(Cast(core"+i+"_usage AS Float)) AS core"+i+"_avg_total, AVG(Cast(core"+i+"_user AS Float)) AS core"+i+"_avg_user, AVG(Cast(core"+i+"_system AS Float)) AS core"+i+"_avg_system, AVG(Cast(core"+i+"_io AS Float)) AS core"+i+"_avg_io ");
	    		querrybuilder.append(" FROM " + CPU_TABLE + " ");
	    		querrybuilder.append("ORDER BY system_time DESC ");
	    		querrybuilder.append("LIMIT 1;");
	    		c = mDB.rawQuery(querrybuilder.toString(),null);
	    	} catch (SQLiteException e) {
	    		tryClose();
	    		e.printStackTrace();
	    		return null;
	    	}
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				ret.system_time = c.getLong(c.getColumnIndex("system_time"));
				if(ret.system_time == 0) return null;
				for(int i=0;i<ret.usage.length;i++) {
					ret.cpu_avg_user[i] = c.getFloat(c.getColumnIndex("core"+i+"_avg_user"));
					ret.cpu_avg_system[i] = c.getFloat(c.getColumnIndex("core"+i+"_avg_system"));
					ret.cpu_avg_io[i] = c.getFloat(c.getColumnIndex("core"+i+"_avg_io"));
					ret.cpu_avg_total[i] = c.getFloat(c.getColumnIndex("core"+i+"_avg_total"));
				}
				ret.act_apps_avg = c.getFloat(c.getColumnIndex("avg_act_apps"));
				ret.act_apps_cur = c.getInt(c.getColumnIndex("active_apps"));
				ret.act_apps_max = c.getInt(c.getColumnIndex("max_act_apps"));
				c.close();
			} else {
				Log.d(TAG,"getCpuTabInfo query1 resulted in null cursor");
				tryClose();
				return null;
			}
    	}
       	tryClose();
    	return ret;
    }
    
    public synchronized void addMems(LinkedList<MemInfo> m, boolean keepopen) {
    	if(openWrite()) {
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(mDB,MEM_TABLE);
            int system_time = ih.getColumnIndex("system_time");
            int free = ih.getColumnIndex("free");
            int total_free = ih.getColumnIndex("total_free");
            int used = ih.getColumnIndex("used");
            int shared = ih.getColumnIndex("shared");
            int buff = ih.getColumnIndex("buff");
            int cached = ih.getColumnIndex("cached");
            int total = ih.getColumnIndex("total");
            int usage = ih.getColumnIndex("usage");
            for(MemInfo mi : m) {
                ih.prepareForInsert();
            	ih.bind(system_time, mi.system_time);
            	ih.bind(free, mi.free);
            	ih.bind(total_free, mi.total_free);
            	ih.bind(used, mi.used);
            	ih.bind(shared, mi.shared);
            	ih.bind(buff, mi.buff);
            	ih.bind(cached, mi.cached);
            	ih.bind(total, mi.total);
            	ih.bind(usage, mi.usage);
            	ih.execute();
            }
            ih.close();
		}
    	if(!keepopen) {
    		tryClose();
    	}
    }
        
    public synchronized MemTabInfo getMemTabInfo() {
    	MemTabInfo ret = new MemTabInfo();
    	if(openRead()) {
	    	Cursor c = null;
	    	try {
	    		c = mDB.rawQuery("SELECT *, AVG(total_free) AS avg_free_mem, " +
	    				"AVG(shared) AS avg_shared_mem, " +
	    				"AVG(buff) AS avg_buff_mem, " +
	    				"AVG(cached) AS avg_cached_mem " +
	    				"FROM " + MEM_TABLE + " " +
	    				"ORDER BY system_time DESC " +
	    				"LIMIT 1;",null);
	    	} catch (SQLiteException e) {
	    		tryClose();
	    		e.printStackTrace();
	    		return null;
	    	}
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				ret.system_time = c.getLong(c.getColumnIndex("system_time"));
				if(ret.system_time == 0) return null;
				ret.free = c.getLong(c.getColumnIndex("free"));
				ret.total_free = c.getLong(c.getColumnIndex("total_free"));
				ret.used = c.getLong(c.getColumnIndex("used"));
				ret.shared= c.getLong(c.getColumnIndex("shared"));
				ret.buff = c.getLong(c.getColumnIndex("buff"));
				ret.cached = c.getLong(c.getColumnIndex("cached"));
				ret.total = c.getLong(c.getColumnIndex("total"));
				ret.usage = c.getLong(c.getColumnIndex("usage"));

				
				ret.avg_free_mem = c.getLong(c.getColumnIndex("avg_free_mem"));
				ret.avg_shared_mem = c.getLong(c.getColumnIndex("avg_shared_mem"));
				ret.avg_buff_mem = c.getLong(c.getColumnIndex("avg_buff_mem"));
				ret.avg_cached_mem = c.getLong(c.getColumnIndex("avg_cached_mem"));
				c.close();
			} else {
				Log.d(TAG,"getMemTabInfo query1 resulted in null cursor");
				return null;
			}
		}
	   	tryClose();
    	return ret;
    }
    
    public synchronized void addLoads(LinkedList<LoadInfo> l, boolean keepopen) {
    	if(openWrite()) {
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(mDB,LOAD_TABLE);
            int first = ih.getColumnIndex("first");
            int second = ih.getColumnIndex("second");
            int third = ih.getColumnIndex("third");
            int system_time = ih.getColumnIndex("system_time");
            for(LoadInfo li : l) {
                ih.prepareForInsert();
            	ih.bind(first, li.first);
            	ih.bind(second, li.second);
            	ih.bind(third, li.third);
            	ih.bind(system_time, li.system_time);
            	ih.execute();
            }
            ih.close();
		}
    	if(!keepopen) {
    		tryClose();
    	}
    }
    
    public synchronized void addNets(LinkedList<NetInfo> n, boolean keepopen) {
    	if(openWrite()) {
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(mDB,NET_TABLE);
            int traffic_up = ih.getColumnIndex("traffic_up");
            int traffic_down = ih.getColumnIndex("traffic_down");
            int rate_up = ih.getColumnIndex("rate_up");
            int rate_down = ih.getColumnIndex("rate_down");
            int mobile_traffic_up = ih.getColumnIndex("mobile_traffic_up");
            int mobile_traffic_down = ih.getColumnIndex("mobile_traffic_down");
            int mobile_rate_up = ih.getColumnIndex("mobile_rate_up");
            int mobile_rate_down = ih.getColumnIndex("mobile_rate_down");
            int system_time = ih.getColumnIndex("system_time");       
            for(NetInfo ni : n) {
                ih.prepareForInsert();
            	ih.bind(traffic_up, ni.traffic_up);
            	ih.bind(traffic_down, ni.traffic_down);
            	ih.bind(rate_up, ni.rate_up);
            	ih.bind(rate_down, ni.rate_down);
            	ih.bind(mobile_traffic_up, ni.mobile_traffic_up);
            	ih.bind(mobile_traffic_down, ni.mobile_traffic_down);
            	ih.bind(mobile_rate_up, ni.mobile_rate_up);
            	ih.bind(mobile_rate_down, ni.mobile_rate_down);
               	ih.bind(system_time, ni.system_time);
            	ih.execute();
            }
            ih.close();
		}
    	if(!keepopen) {
    		tryClose();
    	}
    }
    
        
    public synchronized NetTabInfo getNetTabInfo() {
    	NetTabInfo ret = new NetTabInfo();
    	if(openRead()) {
	    	Cursor c = null;
	    	try {
	    		c = mDB.rawQuery("SELECT traffic_up, traffic_down, rate_up, rate_down, mobile_traffic_up, mobile_traffic_down, mobile_rate_up, mobile_rate_down,system_time " +
	    				"FROM " + NET_TABLE + " " +
	    				"ORDER BY system_time DESC " +
	    				"LIMIT 1;",null);
	    	} catch (SQLiteException e) {
	    		tryClose();
	    		e.printStackTrace();
	    		return null;
	    	}
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				ret.traffic_up = c.getLong(c.getColumnIndex("traffic_up"));
				ret.traffic_down = c.getLong(c.getColumnIndex("traffic_down"));
				ret.rate_up = c.getLong(c.getColumnIndex("rate_up"));
				ret.rate_down= c.getLong(c.getColumnIndex("rate_down"));
				ret.mobile_traffic_up = c.getLong(c.getColumnIndex("mobile_traffic_up"));
				ret.mobile_traffic_down = c.getLong(c.getColumnIndex("mobile_traffic_down"));
				ret.mobile_rate_up = c.getLong(c.getColumnIndex("mobile_rate_up"));
				ret.mobile_rate_down= c.getLong(c.getColumnIndex("mobile_rate_down"));
				ret.system_time = c.getLong(c.getColumnIndex("system_time"));
				c.close();
			} else {
	    		tryClose();
				Log.d(TAG,"getNetTabInfo query1 resulted in null cursor");
				return null;
			}
			//3 hours
			c = null;
	    	try {
	    		c = mDB.rawQuery("SELECT MAX(rate_up) AS peak_rate_up_last_3_hours, MAX(rate_down) AS peak_rate_down_last_3_hours, SUM(traffic_down) AS traffic_last_threehour_down, SUM(traffic_up) AS traffic_last_threehour_up, MAX(mobile_rate_up) AS mobile_peak_rate_up_last_3_hours, MAX(mobile_rate_down) AS mobile_peak_rate_down_last_3_hours, SUM(mobile_traffic_down) AS mobile_traffic_last_threehour_down, SUM(mobile_traffic_up) AS mobile_traffic_last_threehour_up,  " +
	    				"system_time " +
	    				"FROM " + NET_TABLE + " " +
	    				"WHERE system_time > " + (System.currentTimeMillis()-10800000) + ";",null);
	    	} catch (SQLiteException e) {
	    		tryClose();
	    		e.printStackTrace();
	    		return null;
	    	}
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				ret.peak_rate_down_last_3_hours = c.getLong(c.getColumnIndex("peak_rate_down_last_3_hours"));
				ret.peak_rate_up_last_3_hours = c.getLong(c.getColumnIndex("peak_rate_up_last_3_hours"));
				ret.traffic_last_threehour_down = c.getLong(c.getColumnIndex("traffic_last_threehour_down"));
				ret.traffic_last_threehour_up = c.getLong(c.getColumnIndex("traffic_last_threehour_up"));
				ret.mobile_peak_rate_down_last_3_hours = c.getLong(c.getColumnIndex("mobile_peak_rate_down_last_3_hours"));
				ret.mobile_peak_rate_up_last_3_hours = c.getLong(c.getColumnIndex("mobile_peak_rate_up_last_3_hours"));
				ret.mobile_traffic_last_threehour_down = c.getLong(c.getColumnIndex("mobile_traffic_last_threehour_down"));
				ret.mobile_traffic_last_threehour_up = c.getLong(c.getColumnIndex("mobile_traffic_last_threehour_up"));
				c.close();
			} else {
				Log.d(TAG,"getNetTabInfo query2 resulted in null cursor");
				tryClose();
				return null;
			}
			//24 hours
			c = null;
	    	try {
	    		c = mDB.rawQuery("SELECT MAX(rate_up) AS peak_rate_up_last_24_hours, MAX(rate_down) AS peak_rate_down_last_24_hours, SUM(traffic_down) AS traffic_last_day_down, SUM(traffic_up) AS traffic_last_day_up, MAX(mobile_rate_up) AS mobile_peak_rate_up_last_24_hours, MAX(mobile_rate_down) AS mobile_peak_rate_down_last_24_hours, SUM(mobile_traffic_down) AS mobile_traffic_last_day_down, SUM(mobile_traffic_up) AS mobile_traffic_last_day_up," +
	    				"system_time " +
	    				"FROM " + NET_TABLE + " " +
	    				"WHERE system_time > " + (System.currentTimeMillis()-86400000) + ";",null);
	    	} catch (SQLiteException e) {
	    		tryClose();
	    		e.printStackTrace();
	    		return null;
	    	}
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				ret.peak_rate_down_last_24_hours = c.getLong(c.getColumnIndex("peak_rate_down_last_24_hours"));
				ret.peak_rate_up_last_24_hours = c.getLong(c.getColumnIndex("peak_rate_up_last_24_hours"));
				ret.traffic_last_day_down = c.getLong(c.getColumnIndex("traffic_last_day_down"));
				ret.traffic_last_day_up = c.getLong(c.getColumnIndex("traffic_last_day_up"));
				ret.mobile_peak_rate_down_last_24_hours = c.getLong(c.getColumnIndex("mobile_peak_rate_down_last_24_hours"));
				ret.mobile_peak_rate_up_last_24_hours = c.getLong(c.getColumnIndex("mobile_peak_rate_up_last_24_hours"));
				ret.mobile_traffic_last_day_down = c.getLong(c.getColumnIndex("mobile_traffic_last_day_down"));
				ret.mobile_traffic_last_day_up = c.getLong(c.getColumnIndex("mobile_traffic_last_day_up"));
				c.close();
			} else {
				Log.d(TAG,"getNetTabInfo query3 resulted in null cursor");
				tryClose();
				return null;
			}
			//1 week
			c = null;
	    	try {
	    		c = mDB.rawQuery("SELECT MAX(rate_up) AS peak_rate_up_last_24_hours, MAX(rate_down) AS peak_rate_down_last_24_hours, SUM(traffic_down) AS traffic_last_week_down, SUM(traffic_up) AS traffic_last_week_up, MAX(mobile_rate_up) AS mobile_peak_rate_up_last_24_hours, MAX(mobile_rate_down) AS mobile_peak_rate_down_last_24_hours, SUM(mobile_traffic_down) AS mobile_traffic_last_week_down, SUM(mobile_traffic_up) AS mobile_traffic_last_week_up, " +
	    				"system_time " +
	    				"FROM " + NET_TABLE + " " +
	    				"WHERE system_time > " + (System.currentTimeMillis()-604800000) + ";",null);
	    	} catch (SQLiteException e) {
	    		tryClose();
	    		e.printStackTrace();
	    		return null;
	    	}
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				ret.peak_rate_down_last_24_hours = c.getLong(c.getColumnIndex("peak_rate_down_last_24_hours"));
				ret.peak_rate_up_last_24_hours = c.getLong(c.getColumnIndex("peak_rate_up_last_24_hours"));
				ret.traffic_last_week_down = c.getLong(c.getColumnIndex("traffic_last_week_down"));
				ret.traffic_last_week_up = c.getLong(c.getColumnIndex("traffic_last_week_up"));
				ret.mobile_peak_rate_down_last_24_hours = c.getLong(c.getColumnIndex("mobile_peak_rate_down_last_24_hours"));
				ret.mobile_peak_rate_up_last_24_hours = c.getLong(c.getColumnIndex("mobile_peak_rate_up_last_24_hours"));
				ret.mobile_traffic_last_week_down = c.getLong(c.getColumnIndex("mobile_traffic_last_week_down"));
				ret.mobile_traffic_last_week_up = c.getLong(c.getColumnIndex("mobile_traffic_last_week_up"));
				c.close();
			} else {
				Log.d(TAG,"getNetTabInfo query3 resulted in null cursor");
				tryClose();
				return null;
			}
		}
	   	tryClose();
    	return ret;
    }
    
    public synchronized void addBats(LinkedList<BattInfo> b, boolean keepopen) {
    	if(openWrite()) {
        
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(mDB,BATT_TABLE);
            int scale = ih.getColumnIndex("scale");
            int level = ih.getColumnIndex("level");
            int voltage = ih.getColumnIndex("voltage");
            int temp = ih.getColumnIndex("temp");
            int health = ih.getColumnIndex("health"); 
            int status = ih.getColumnIndex("status");
            int power = ih.getColumnIndex("power");    
            int tech = ih.getColumnIndex("tech");
            int system_time = ih.getColumnIndex("system_time");    
            
            for(BattInfo bi : b) {
                ih.prepareForInsert();
            	ih.bind(scale, bi.scale);
            	ih.bind(level, bi.level);
            	ih.bind(voltage, bi.voltage);
            	ih.bind(temp, bi.batt_temp_cur);
               	ih.bind(health, bi.health);
            	ih.bind(status, bi.status);
               	ih.bind(power, bi.power);
            	ih.bind(tech, bi.tech);
               	ih.bind(system_time, bi.system_time);
            	ih.execute();
            }
            ih.close();
		}
    	if(!keepopen) {
    		tryClose();
    	}
    }

        
    public synchronized BattTabInfo getBattTabInfo() {
    	BattTabInfo ret = new BattTabInfo();
    	if(openRead()) {
	    	Cursor c = null;
	    	try {
	    		c = mDB.rawQuery("SELECT *, AVG(level) AS batt_level_avg, " +
	    				"AVG(voltage) AS voltage_avg, " +
	       				"MIN(voltage) AS voltage_min, " +
	       				"MAX(voltage) AS voltage_max, " +
	    				"AVG(temp) AS batt_temp_avg, " +
	    				"MIN(temp) AS batt_temp_min, " +
	    				"MAX(temp) AS batt_temp_max " +
	    				"FROM " + BATT_TABLE + " " +
	    				"ORDER BY system_time DESC " +
	    				"LIMIT 1;",null);
	    	} catch (SQLiteException e) {
	    		e.printStackTrace();
	    		tryClose();
	    		return null;
	    	}
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				ret.system_time = c.getLong(c.getColumnIndex("system_time"));
				if(ret.system_time == 0) return null;
				ret.scale = c.getInt(c.getColumnIndex("scale"));
				ret.level = c.getInt(c.getColumnIndex("level"));
				ret.voltage = c.getInt(c.getColumnIndex("voltage"));
				ret.batt_temp_cur = c.getInt(c.getColumnIndex("temp"));
				ret.tech = c.getString(c.getColumnIndex("tech"));
				ret.health = c.getInt(c.getColumnIndex("health"));
				ret.status = c.getInt(c.getColumnIndex("status"));
				ret.power = c.getInt(c.getColumnIndex("power"));
				
				ret.batt_level_avg = c.getFloat(c.getColumnIndex("batt_level_avg"));
				ret.voltage_avg = c.getFloat(c.getColumnIndex("voltage_avg"));
				ret.voltage_min = c.getInt(c.getColumnIndex("voltage_min"));
				ret.voltage_max = c.getInt(c.getColumnIndex("voltage_max"));
				ret.batt_temp_avg = c.getFloat(c.getColumnIndex("batt_temp_avg"));
				ret.batt_temp_min = c.getInt(c.getColumnIndex("batt_temp_min"));
				ret.batt_temp_max = c.getInt(c.getColumnIndex("batt_temp_max"));
				c.close();
			} else {
				Log.d(TAG,"getBattTabInfo query1 resulted in null cursor");
				tryClose();
				return null;
			}
		}
	   	tryClose();
    	return ret;
    }
    
    public synchronized void addApps(LinkedList<AppInfo> a, boolean keepopen) {
    	if(openWrite()) {
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(mDB,APP_TABLE);
            int command = ih.getColumnIndex("command");
            int system_time = ih.getColumnIndex("system_time");
            int cpu = ih.getColumnIndex("cpu");
            int mem = ih.getColumnIndex("mem");
            int vsz = ih.getColumnIndex("vsz"); 

            for(AppInfo ai : a) {
                ih.prepareForInsert();
            	ih.bind(command, ai.command);
            	ih.bind(system_time , ai.system_time);
            	ih.bind(cpu, ai.cpu);
            	ih.bind(mem, ai.mem);
               	ih.bind(vsz, ai.vsz);
            	ih.execute();
            }
            ih.close();
		}
    	if(!keepopen) {
    		tryClose();
    	}
    }
    
    
    public synchronized ArrayList<AppTabInfo> getAppTabInfo(int sortmode, boolean hidesys) {
    	ArrayList<AppTabInfo> ret = new ArrayList<AppTabInfo>();
    	if(openRead()) {
	    	Cursor c = null;
	    	String sorter = "";
	    	switch(sortmode) {
		    	case 0:
		    		sorter = "seen";
		    		break;
		    	case 1:
		    		sorter = "avg_mem";
		    		break;
		    	case 2:
		    		sorter = "avg_cpu";
		    		break;
		    	case 3:
		    		sorter = "system_time";
		    		break;
		    	case 4:
		    		sorter = "command";
		    		break;	    			    		
		    	default:
		    		sorter = "seen";
		    		break;
	    	}
	    	try {
	    		c = mDB.rawQuery("SELECT command,cpu,mem,system_time,vsz,COUNT(*) AS seen,AVG(mem) AS avg_mem,AVG(cpu) AS avg_cpu " +
						"FROM " + APP_TABLE + " " +
						"GROUP BY command " +
						"ORDER BY " + sorter + " DESC;",null);
	    	} catch (SQLiteException e) {
	    		tryClose();
	    		e.printStackTrace();
	    		return null;
	    	}
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				for(int i=0;i<c.getCount();i++) {
					boolean skip = false;
					if(hidesys) {
						String cmd = c.getString(c.getColumnIndex("command"));
						if((cmd.startsWith("[") && cmd.endsWith("]")) || cmd.equalsIgnoreCase("system_server") || cmd.contains("eu.thedarken.diagnosis")) {
							skip = true;
						}
					}
					if(!skip) {
						AppTabInfo ai = new AppTabInfo();
						ai.command = c.getString(c.getColumnIndex("command"));
						ai.cpu = c.getFloat(c.getColumnIndex("cpu"));
						ai.mem = c.getFloat(c.getColumnIndex("mem"));
						ai.system_time = c.getLong(c.getColumnIndex("system_time"));
						ai.vsz = c.getString(c.getColumnIndex("vsz"));
						ai.seen = c.getInt(c.getColumnIndex("seen"));
						ai.avg_mem = c.getFloat(c.getColumnIndex("avg_mem"));
						ai.avg_cpu = c.getFloat(c.getColumnIndex("avg_cpu"));
						ret.add(ai);
					}
					if(!c.moveToNext()) {
						break;
					}
				}
				c.close();
			} else {
				Log.d(TAG,"getAppTabInfo query1 resulted in null cursor");
				tryClose();
				return null;
			}
		}
	   	tryClose();
    	return ret;
    }
    
    public synchronized void addSpaces(LinkedList<SpaceInfo> s, boolean keepopen) {
    	if(openWrite()) {
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(mDB,SPACE_TABLE);
            int system_time = ih.getColumnIndex("system_time");
            int extern_total = ih.getColumnIndex("extern_total");
            int extern_used = ih.getColumnIndex("extern_used");
            int sdcard_total = ih.getColumnIndex("sdcard_total");
            int sdcard_used = ih.getColumnIndex("sdcard_used"); 
            int system_total = ih.getColumnIndex("system_total");
            int system_used = ih.getColumnIndex("system_used");
            int data_total = ih.getColumnIndex("data_total"); 
            int data_used = ih.getColumnIndex("data_used"); 
            for(SpaceInfo si : s) {
                ih.prepareForInsert();
            	ih.bind(system_time, si.system_time);
            	ih.bind(extern_total , si.extern_total);
            	ih.bind(extern_used, si.extern_used);
            	ih.bind(sdcard_total, si.sdcard_total);
               	ih.bind(sdcard_used, si.sdcard_used);
            	ih.bind(system_total, si.system_total);
            	ih.bind(system_used, si.system_used);
               	ih.bind(data_total, si.data_total);
              	ih.bind(data_used, si.data_used);
            	ih.execute();
            }
            ih.close();
		}
    	if(!keepopen) {
    		tryClose();
    	}
    }
    
    public synchronized SpaceTabInfo getSpaceTabInfo() {
    	SpaceTabInfo ret = new SpaceTabInfo();
    	if(openRead()) {
	    	Cursor c = null;
	    	try {
	    		c = mDB.rawQuery("SELECT *, AVG(extern_used) AS avg_extern_diff, " +
	    				"AVG(sdcard_used) AS avg_sdcard_diff, " +
	    				"AVG(system_used) AS avg_system_diff, " +
	    				"AVG(data_used) AS avg_data_diff " +
	    				"FROM " + SPACE_TABLE + " " +
	    				"ORDER BY system_time DESC " +
	    				"LIMIT 1;",null);
	    	} catch (SQLiteException e) {
	    		tryClose();
	    		e.printStackTrace();
	    		return null;
	    	}
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				ret.system_time = c.getLong(c.getColumnIndex("system_time"));
				if(ret.system_time == 0) return null;
				ret.extern_total = c.getLong(c.getColumnIndex("extern_total"));
				ret.extern_used = c.getLong(c.getColumnIndex("extern_used"));
				ret.sdcard_total = c.getLong(c.getColumnIndex("sdcard_total"));
				ret.sdcard_used = c.getLong(c.getColumnIndex("sdcard_used"));
				ret.system_total = c.getLong(c.getColumnIndex("system_total"));
				ret.system_used = c.getLong(c.getColumnIndex("system_used"));
				ret.data_total = c.getLong(c.getColumnIndex("data_total"));
				ret.data_used = c.getLong(c.getColumnIndex("data_used"));
				ret.avg_extern_diff = c.getLong(c.getColumnIndex("avg_extern_diff"));
				ret.avg_sdcard_diff = c.getLong(c.getColumnIndex("avg_sdcard_diff"));
				ret.avg_system_diff = c.getLong(c.getColumnIndex("avg_system_diff"));
				ret.avg_data_diff = c.getLong(c.getColumnIndex("avg_data_diff"));
				c.close();
			} else {
				Log.d(TAG,"getSpaceTabInfo query1 resulted in null cursor");
	    		tryClose();
				return null;
			}
		}
	   	tryClose();
    	return ret;
    }
    
    public synchronized void addDisks(LinkedList<DiskInfo> s, boolean keepopen) {
    	if(openWrite()) {
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(mDB,DISK_TABLE);
            int system_time = ih.getColumnIndex("system_time");
            int read = ih.getColumnIndex("read");
            int written = ih.getColumnIndex("written");
            int read_rate = ih.getColumnIndex("read_rate");
            int write_rate = ih.getColumnIndex("write_rate"); 
            for(DiskInfo ds : s) {
                ih.prepareForInsert();
            	ih.bind(system_time, ds.system_time);
            	ih.bind(read , ds.read);
            	ih.bind(written, ds.written);
            	ih.bind(read_rate, ds.read_rate);
               	ih.bind(write_rate, ds.write_rate);
            	ih.execute();
            }
            ih.close();
		}
    	if(!keepopen) {
    		tryClose();
    	}
    }
    
    public synchronized void addWlans(LinkedList<WlanInfo> wi, boolean keepopen) {
    	if(openWrite()) {
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(mDB,WLAN_TABLE);
            int system_time = ih.getColumnIndex("system_time");
            int signal = ih.getColumnIndex("signal");
            for(WlanInfo wis : wi) {
                ih.prepareForInsert();
            	ih.bind(system_time, wis.system_time);
            	ih.bind(signal , wis.signal);
            	ih.execute();
            }
            ih.close();
		}
    	if(!keepopen) {
    		tryClose();
    	}
    }

    public synchronized WlanTabInfo getWlanTabInfo() {
    	WlanTabInfo ret = new WlanTabInfo();
    	if(openRead()) {
	    	Cursor c = null;
	    	try {
	    		c = mDB.rawQuery("SELECT system_time, signal, AVG(signal) AS avg_signal, MAX(signal) AS max_signal, MIN(signal) AS min_signal " +
	    				"FROM " + WLAN_TABLE + " " +
	    				"ORDER BY system_time DESC " +
	    				"LIMIT 1;",null);
	    	} catch (SQLiteException e) {
	    		e.printStackTrace();
	    		tryClose();
	    		return null;
	    	}
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				ret.system_time = c.getLong(c.getColumnIndex("system_time"));
				if(ret.system_time == 0) return null;
				ret.signal = c.getInt(c.getColumnIndex("signal"));
				ret.avg_signal = c.getFloat(c.getColumnIndex("avg_signal"));
				ret.min_signal = c.getInt(c.getColumnIndex("min_signal"));
				ret.max_signal = c.getInt(c.getColumnIndex("max_signal"));
				//Log.d("MIIIN","MIN"+ret.min_signal);
				//Log.d("MAAAX","MAX"+ret.max_signal);
				c.close();
			} else {
				Log.d(TAG,"getWlanTabInfo query1 resulted in null cursor");
				tryClose();
				return null;
			}
			tryClose();
    	}
    	return ret;
    }
    
    public synchronized void addPhones(LinkedList<PhoneInfo> pi, boolean keepopen) {
    	if(openWrite()) {
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(mDB,PHONE_TABLE);
            int system_time = ih.getColumnIndex("system_time");
            int gsm_signal = ih.getColumnIndex("gsm_signal");
            for(PhoneInfo pis : pi) {
                ih.prepareForInsert();
            	ih.bind(system_time, pis.system_time);
            	ih.bind(gsm_signal , pis.gsm_signal);
            	ih.execute();
            }
            ih.close();
		}
    	if(!keepopen) {
    		tryClose();
    	}
    }
    
    public synchronized PhoneTabInfo getPhoneTabInfo() {
    	PhoneTabInfo ret = new PhoneTabInfo();
    	if(openRead()) {
	    	Cursor c = null;
	    	try {
	    		c = mDB.rawQuery("SELECT system_time, gsm_signal, AVG(gsm_signal) AS avg_gsm_signal, MAX(gsm_signal) AS max_gsm_signal, MIN(gsm_signal) AS min_gsm_signal " +
	    				"FROM " + PHONE_TABLE + " " +
	    				"ORDER BY system_time DESC " +
	    				"LIMIT 1;",null);
	    	} catch (SQLiteException e) {
	    		e.printStackTrace();
	    		tryClose();
	    		return null;
	    	}
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				ret.system_time = c.getLong(c.getColumnIndex("system_time"));
				if(ret.system_time == 0) return null;
				ret.gsm_signal = c.getInt(c.getColumnIndex("gsm_signal"));
				ret.avg_gsm_signal = c.getFloat(c.getColumnIndex("avg_gsm_signal"));
				ret.min_gsm_signal = c.getInt(c.getColumnIndex("min_gsm_signal"));
				ret.max_gsm_signal = c.getInt(c.getColumnIndex("max_gsm_signal"));
				c.close();
			} else {
				Log.d(TAG,"getPhoneTabInfo query1 resulted in null cursor");
				tryClose();
				return null;
			}
			tryClose();
    	}
    	return ret;
    }
    
	public synchronized void addFreqs(LinkedList<FreqInfo> fi, boolean keepopen) {
		if (openWrite()) {
			DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(mDB, FREQ_TABLE);
			int system_time = ih.getColumnIndex("system_time");
			int[] cpu_frequency = new int[DGdata.CORES];
			for (int i = 0; i < cpu_frequency.length; i++)
				cpu_frequency[i] = ih.getColumnIndex("cpu"+i+"_frequency");
			for (FreqInfo fis : fi) {
				ih.prepareForInsert();
				ih.bind(system_time, fis.system_time);
				for(int i=0;i<fis.cpu_frequency.length;i++)
					ih.bind(cpu_frequency[i], fis.cpu_frequency[i]);
				ih.execute();
			}
			ih.close();
		}
		if (!keepopen) {
			tryClose();
		}
	}
    
    public synchronized FreqTabInfo getFreqTabInfo() {
    	FreqTabInfo ret = new FreqTabInfo();
    	if(openRead()) {
	    	Cursor c = null;
	    	try {
	    		StringBuilder querybuilder = new StringBuilder();
	    		querybuilder.append("SELECT system_time");
	    		for(int i=0;i<ret.cpu_frequency.length;i++)
	    			querybuilder.append(", cpu"+i+"_frequency, AVG(Cast(cpu"+i+"_frequency AS Double)) AS avg_cpu"+i+"_freq, MAX(cpu"+i+"_frequency) AS max_obs_cpu"+i+"_freq, MIN(cpu"+i+"_frequency) AS min_obs_cpu"+i+"_freq ");
	    		querybuilder.append(" FROM " + FREQ_TABLE + " ");
	    		querybuilder.append("ORDER BY system_time DESC ");
	    		querybuilder.append(";");
	    		c = mDB.rawQuery(querybuilder.toString(),null);
	    	} catch (SQLiteException e) {
	    		e.printStackTrace();
	    		tryClose();
	    		return null;
	    	}
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				ret.system_time = c.getLong(c.getColumnIndex("system_time"));
				if(ret.system_time == 0) return null;
				for(int i=0;i<ret.cpu_frequency.length;i++) {
					ret.cpu_frequency[i] = c.getLong(c.getColumnIndex("cpu"+i+"_frequency"));
					ret.max_obs_cpu_freq[i] = c.getLong(c.getColumnIndex("max_obs_cpu"+i+"_freq"));
					ret.min_obs_cpu_freq[i] = c.getLong(c.getColumnIndex("min_obs_cpu"+i+"_freq"));
					ret.avg_cpu_freq[i] = c.getDouble(c.getColumnIndex("avg_cpu"+i+"_freq"));
				}
				c.close();
			} else {
				Log.d(TAG,"getFreqTab query1 resulted in null cursor");
				tryClose();
				return null;
			}
			tryClose();
    	}
    	return ret;
    }
    
    
    public synchronized void addPings(LinkedList<PingInfo> pinglist, boolean keepopen) {
    	if(openWrite()) {
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(mDB,PING_TABLE);
            int system_time = ih.getColumnIndex("system_time");
            int ping = ih.getColumnIndex("ping");
            for(PingInfo pings : pinglist) {
                ih.prepareForInsert();
            	ih.bind(system_time, pings.system_time);
            	ih.bind(ping , pings.ping);
            	ih.execute();
            }
            ih.close();
		}
    	if(!keepopen) {
    		tryClose();
    	}
    }
    
    public synchronized PingTabInfo getPingTabInfo() {
    	PingTabInfo ret = new PingTabInfo();
    	if(openRead()) {
	    	Cursor c = null;
	    	try {
	    		c = mDB.rawQuery("SELECT system_time, AVG(ping) AS avg_ping, MAX(ping) AS max_ping, MIN(ping) AS min_ping " +
	    				"FROM " + PING_TABLE + " " +
	    				"ORDER BY system_time DESC " +
	    				"LIMIT 1;",null);
	    	} catch (SQLiteException e) {
	    		e.printStackTrace();
	    		tryClose();
	    		return null;
	    	}
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				ret.system_time = c.getLong(c.getColumnIndex("system_time"));
				if(ret.system_time == 0) return null;
				ret.avg_ping = c.getInt(c.getColumnIndex("avg_ping"));
				ret.max_ping = c.getInt(c.getColumnIndex("max_ping"));
				ret.min_ping = c.getInt(c.getColumnIndex("min_ping"));
				c.close();
			} else {
				Log.d(TAG,"getPingTab query1 resulted in null cursor");
				tryClose();
				return null;
			}
			tryClose();
    	}
    	return ret;
    }
    
    public synchronized DiskTabInfo getDiskTabInfo() {
    	DiskTabInfo ret = new DiskTabInfo();
    	if(openRead()) {
	    	Cursor c = null;
	    	try {
	    		c = mDB.rawQuery("SELECT system_time, write_rate, read_rate, AVG(write_rate) AS avg_write_rate, MAX(write_rate) AS max_write_rate, AVG(read_rate) AS avg_read_rate, MAX(read_rate) AS max_read_rate " +
	    				"FROM " + DISK_TABLE + " " +
	    				"ORDER BY system_time DESC " +
	    				"LIMIT 1;",null);
	    	} catch (SQLiteException e) {
	    		e.printStackTrace();
	    		tryClose();
	    		return null;
	    	}
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				ret.system_time = c.getLong(c.getColumnIndex("system_time"));
				if(ret.system_time == 0) return null;
				ret.write_rate = c.getLong(c.getColumnIndex("write_rate"));
				ret.read_rate = c.getLong(c.getColumnIndex("read_rate"));
				ret.avg_write_rate = c.getLong(c.getColumnIndex("avg_write_rate"));
				ret.avg_read_rate = c.getLong(c.getColumnIndex("avg_read_rate"));
				ret.max_write_rate = c.getLong(c.getColumnIndex("max_write_rate"));
				ret.max_read_rate = c.getLong(c.getColumnIndex("max_read_rate"));
				c.close();
			} else {
				Log.d(TAG,"getDiskTabInfo query1 resulted in null cursor");
				tryClose();
				return null;
			}
			tryClose();
    	}
    	return ret;
    }
}
