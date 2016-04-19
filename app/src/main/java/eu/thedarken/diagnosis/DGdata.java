package eu.thedarken.diagnosis;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.thedarken.diagnosis.DGoverlay.Line;
import eu.thedarken.diagnosis.InfoClass.AppInfo;
import eu.thedarken.diagnosis.InfoClass.BattInfo;
import eu.thedarken.diagnosis.InfoClass.CpuInfo;
import eu.thedarken.diagnosis.InfoClass.DiskInfo;
import eu.thedarken.diagnosis.InfoClass.FreqInfo;
import eu.thedarken.diagnosis.InfoClass.LoadInfo;
import eu.thedarken.diagnosis.InfoClass.MemInfo;
import eu.thedarken.diagnosis.InfoClass.NetInfo;
import eu.thedarken.diagnosis.InfoClass.PhoneInfo;
import eu.thedarken.diagnosis.InfoClass.PingInfo;
import eu.thedarken.diagnosis.InfoClass.SpaceInfo;
import eu.thedarken.diagnosis.InfoClass.WlanInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DGdata {
    private static final Pattern CPU_PATTERN2 = Pattern.compile("^" + // start
            "(CPU\\d:)" + // cpu (group = 1)
            "\\s+" + // blanks
            "([0-9]+\\.[0-9]+%)" + // usr nr(group = 2)
            "\\s+" + // blanks
            "(usr)" + // user (group = 3)
            "\\s+" + // blanks
            "([0-9]+\\.[0-9]+%)" + // sys nr (group = 4)
            "\\s+" + // blanks
            "(sys)" + // sys (group = 5)
            "\\s+" + // blanks
            "([0-9]+\\.[0-9]+%)" + // nice nr (group = 6)
            "\\s+" + // blanks
            "(nic)" + // nice (group = 7)
            "\\s+" + // blanks
            "([0-9]+\\.[0-9]+%)" + // idle nr(group = 8)
            "\\s+" + // blanks
            "(idle)" + // idle (group = 9)
            "\\s+" + // blanks
            "([0-9]+\\.[0-9]+%)" + // io nr (group = 10)
            "\\s+" + // blanks
            "(io)" + // nice (group = 11)
            "\\s+" + // blanks
            "([0-9]+\\.[0-9]+%)" + // irq nr (group = 12)
            "\\s+" + // blanks
            "(irq)" + // irq (group = 13)
            "\\s+" + // blanks
            "([0-9]+\\.[0-9]+%)" + // sirq nr (group = 14)
            "\\s*" + // blanks
            "(sirq)$"); // sirq (group = 15)

    private static final Pattern MEMORY_PATTERN = Pattern.compile("^" + // start
            "\\S*" +
            "(Mem:)" + //G1
            "\\s" +
            "([0-9]+)" + //G2 used
            "(K used,)" +
            "\\s" +
            "([0-9]+)" + //G4 free
            "(K free,)" +
            "\\s" +
            "([0-9]+)" + //G6 shrd
            "(K shrd,)" +
            "\\s" +
            "([0-9]+)" + //G7 buff
            "(K buff,)" +
            "\\s" +
            "([0-9]+)" + //G10 cached
            "(K cached)" +
            "$"); // end of the line
    private static final Pattern APP_PATTERN = Pattern.compile("^" +
            "\\s*" +
            "([0-9]+)" + //pid
            "\\s*" +
            "([0-9]+)" + //ppid 
            "\\s*" +
            "([0-9]+)" +  //user
            "\\s*" +
            "([A-Z]+)" + //Stat
            "\\s*" +
            "([0-9]+[a-z]*)" + //VSZ 
            "\\s*" +
            "([0-9]+.[0-9]+)" + //Mem 
            "\\s*" +
            "([0-9]+.[0-9]+)" + //Cpu
            "\\s*" +
            "(.*?)" + //cmd
            "$"); // end of the line
    private static final Pattern LOAD_PATTERN = Pattern.compile("^" +
            "(Load average: )" + //G1
            "([0-9]+.[0-9]*)" + //G2
            "\\s" +
            "([0-9]+.[0-9]*)" + //G3
            "\\s" +
            "([0-9]+.[0-9]*)" + //G4
            "\\s" +
            "([0-9]+/[0-9]+)" + //G5
            "\\s" +
            "([0-9]+)" +
            "$"); // end of the line

    private static final Pattern IOSTAT_PATTERN = Pattern.compile("^" + // start
            "([a-zA-Z0-9_-]+)" + // device (group = 1)
            "\\s+" + // blanks
            "([0-9]+\\.[0-9]+)" + // tps (group = 2)
            "\\s+" + // blanks
            "([0-9]+\\.[0-9]+)" + // kb_read/s (group = 3)
            "\\s+" + // blanks
            "([0-9]+\\.[0-9]+)" + // kb_write/s (group = 4)
            "\\s+" + // blanks
            "([0-9]+)" + // kb_read(group = 5)
            "\\s+" + // blanks
            "([0-9]+$)");  // kb_write (group = 6)
    public static String BUSYBOX = "";
    public static int DB_CACHE_SIZE = 0;
    public static int CORES = 4;
    public static boolean db_cache_reset_required = false;
    private final String TAG = "eu.thedarken.diagnosis.DGdata";
    private final AtomicBoolean dbTask_runs = new AtomicBoolean(false);
    int highest_app = 0;
    boolean updateTask_runs = false;
    long global_last_down = TrafficStats.getTotalRxBytes();
    long global_last_up = TrafficStats.getTotalTxBytes();
    long mobile_global_last_down = TrafficStats.getMobileRxBytes();
    long mobile_global_last_up = TrafficStats.getMobileTxBytes();
    long net_duration = 0;
    long global_write = 0;
    long global_read = 0;
    long disk_duration = 0;
    private Context mContext;
    private long current_time = 0;
    private List<CpuInfo> cpulist = Collections.synchronizedList(new LinkedList<CpuInfo>());
    private List<FreqInfo> freqlist = Collections.synchronizedList(new LinkedList<FreqInfo>());
    private List<MemInfo> memlist = Collections.synchronizedList(new LinkedList<MemInfo>());
    private List<LoadInfo> loadlist = Collections.synchronizedList(new LinkedList<LoadInfo>());
    private List<BattInfo> battlist = Collections.synchronizedList(new LinkedList<BattInfo>());
    BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        BattInfo temp = new BattInfo();

        @Override
        public void onReceive(Context context, Intent intent) {
            temp.level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            temp.scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            temp.batt_temp_cur = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            temp.voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            temp.health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            temp.status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            temp.power = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            temp.tech = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            temp.system_time = current_time;
            if (current_time != 0) {
                battlist.add(temp);
            }
            Log.d(TAG, "level is " + temp.level + "/" + temp.scale + ", temp is " + temp.batt_temp_cur + ", voltage is " +
                    temp.voltage + ",health is " + intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
                    + ",status is " + intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    + ",tech is " + intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
                    + ",AC pow is " + intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1));
        }

    };
    private List<NetInfo> netlist = Collections.synchronizedList(new LinkedList<NetInfo>());
    private List<AppInfo> active_apps = Collections.synchronizedList(new LinkedList<AppInfo>());
    private List<AppInfo> apps = Collections.synchronizedList(new LinkedList<AppInfo>());
    private List<SpaceInfo> spacelist = Collections.synchronizedList(new LinkedList<SpaceInfo>());
    private List<DiskInfo> disklist = Collections.synchronizedList(new LinkedList<DiskInfo>());
    private List<PingInfo> pinglist = Collections.synchronizedList(new LinkedList<PingInfo>());
    private List<PhoneInfo> phonelist = Collections.synchronizedList(new LinkedList<PhoneInfo>());
    ;
    private List<WlanInfo> wlanlist = Collections.synchronizedList(new LinkedList<WlanInfo>());
    private int biggest_data_count = 0;
    private SharedPreferences settings;
    private DGdatabase mDB;
    private TelephonyManager telman;
    private PhoneInfoListener phoneinfolistener;
    private WifiManager wifiman;

    public DGdata(Context c) {
        mContext = c;

        settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        BUSYBOX = settings.getString("BUSYBOX", "/data/data/eu.thedarken.diagnosis/files/busybox");

        mDB = DGdatabase.getInstance(mContext.getApplicationContext());
        DB_CACHE_SIZE = settings.getInt("database.cachesize", 24);

        DGdata.CORES = 4;//detectCores();

        IntentFilter batfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        mContext.registerReceiver(batteryReceiver, batfilter);

        telman = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        phoneinfolistener = new PhoneInfoListener();
        telman.listen(phoneinfolistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        wifiman = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    public void close() {
        mContext.unregisterReceiver(batteryReceiver);
        telman.listen(phoneinfolistener, PhoneStateListener.LISTEN_NONE);
    }

    private int detectCores() {
        try {
            int core_count = 0;
            for (int i = 0; i < 8; i++) {
                File core = new File("/sys/devices/system/cpu/cpu" + i + "/");
                if (core.exists() && core.canRead() && core.isDirectory())
                    core_count++;
            }
            Log.d(TAG, core_count + " cpu cores detected.");
            return core_count;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error defaulting to 1 cpu core");
            return 1;
        }
    }

    public CpuInfo getCpu() {
        if (cpulist.isEmpty()) {
            return new CpuInfo();
        }
        CpuInfo ret = new CpuInfo();
        synchronized (cpulist) {
            ret = cpulist.get(cpulist.size() - 1);
        }
        return ret;
    }

    public FreqInfo getFreq() {
        if (freqlist.isEmpty()) {
            return new FreqInfo();
        }
        FreqInfo ret = new FreqInfo();
        synchronized (freqlist) {
            ret = freqlist.get(freqlist.size() - 1);
        }
        return ret;
    }

    public MemInfo getMem() {
        if (memlist.isEmpty()) {
            return new MemInfo();
        }
        MemInfo ret = new MemInfo();
        synchronized (memlist) {
            ret = memlist.get(memlist.size() - 1);
        }
        return ret;
    }

    public LoadInfo getLoad() {
        if (loadlist.isEmpty()) {
            return new LoadInfo();
        }
        LoadInfo ret = new LoadInfo();
        synchronized (loadlist) {
            ret = loadlist.get(loadlist.size() - 1);
        }
        return ret;
    }

    public BattInfo getBatt() {
        if (battlist.isEmpty()) {
            return new BattInfo();
        }
        BattInfo ret = new BattInfo();
        synchronized (battlist) {
            ret = battlist.get(battlist.size() - 1);
        }
        return ret;
    }

    public NetInfo getNet() {
        if (netlist.isEmpty()) {
            return new NetInfo();
        }
        NetInfo ret = new NetInfo();
        synchronized (netlist) {
            ret = netlist.get(netlist.size() - 1);
        }
        return ret;
    }

    public SpaceInfo getSpace() {
        if (spacelist.isEmpty()) {
            return new SpaceInfo();
        }
        SpaceInfo ret = new SpaceInfo();
        synchronized (spacelist) {
            ret = spacelist.get(spacelist.size() - 1);
        }
        return ret;
    }

    public DiskInfo getDisk() {
        if (disklist.isEmpty()) {
            return new DiskInfo();
        }
        DiskInfo ret = new DiskInfo();
        synchronized (disklist) {
            ret = disklist.get(disklist.size() - 1);
        }
        return ret;
    }

    public PingInfo getPing() {
        if (pinglist.isEmpty()) {
            return new PingInfo();
        }
        PingInfo ret = new PingInfo();
        synchronized (pinglist) {
            ret = pinglist.get(pinglist.size() - 1);
        }
        return ret;
    }

    public ArrayList<AppInfo> getAppsByCpu(float barrier) {
        ArrayList<AppInfo> ret = new ArrayList<AppInfo>();
        synchronized (active_apps) {
            for (AppInfo a : active_apps) {
                if (a.cpu > barrier) ret.add(a);
            }
        }
        return ret;
    }

    public PhoneInfo getPhone() {
        if (phonelist.isEmpty()) {
            return new PhoneInfo();
        }
        PhoneInfo ret = new PhoneInfo();
        synchronized (phonelist) {
            ret = phonelist.get(phonelist.size() - 1);
        }
        return ret;
    }

    public WlanInfo getWlan() {
        if (wlanlist.isEmpty()) {
            return new WlanInfo();
        }
        WlanInfo ret = new WlanInfo();
        synchronized (wlanlist) {
            ret = wlanlist.get(wlanlist.size() - 1);
        }
        return ret;
    }

    public boolean update() {
        if (!updateTask_runs) {
            updateTask_runs = true;
            new updateTask().execute();

        }
        //TODO skip redraw if update not done???
        return true;
    }

    private void doPing() {
        PingInfo ping = new PingInfo();
        try {
            URL url;
            try {
                url = new URL("http://" + settings.getString("general.internet.ping.target", "www.google.com"));
            } catch (Exception e) {
                url = new URL("http://www.google.com");
            }
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            // urlc.setRequestProperty("User-Agent",
            // "Android Application:"+Z.APP_VERSION);
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(500); // mTimeout is in seconds
            long time = System.currentTimeMillis();
            ping.system_time = time;
            urlc.connect();
            ping.ping = (int) (System.currentTimeMillis() - time);
            // Log.d(mContext.getPackageName(), "ping response:" +
            // urlc.getResponseCode());
        } catch (MalformedURLException e1) {
            ping.ping = 1000;
        } catch (IOException e) {
            ping.ping = 1000;
        }
        pinglist.add(ping);
    }

    private void doSpace() {
        SpaceInfo ret = new SpaceInfo();
        ret.data_total = getSpace(Environment.getDataDirectory()).total;
        ret.data_used = getSpace(Environment.getDataDirectory()).used;
        ret.sdcard_total = getSpace(Environment.getExternalStorageDirectory()).total;
        ret.sdcard_used = getSpace(Environment.getExternalStorageDirectory()).used;
        ret.system_total = getSpace(Environment.getRootDirectory()).total;
        ret.system_used = getSpace(Environment.getRootDirectory()).used;
        if (DGoverlay.getExternalSDs().size() > 0) {
            ret.extern_total = getSpace(new File(DGoverlay.getExternalSDs().get(0).path.getAbsolutePath())).total;
            ret.extern_used = getSpace(new File(DGoverlay.getExternalSDs().get(0).path.getAbsolutePath())).used;
        }
        ret.system_time = current_time;
        spacelist.add(ret);
    }

    private Retval getSpace(File p) {
        StatFs stat;
        Retval ret = new Retval();
        try {
            stat = new StatFs(p.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "There was a problem getting Stats for " + p.getName());
            return ret;
        }
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long totalavailableBlocks = stat.getBlockCount();
        ret.total = totalavailableBlocks * blockSize;
        ret.used = totalavailableBlocks * blockSize - availableBlocks * blockSize;
        return ret;
    }

    private void doWifi() {
        WifiInfo wi = wifiman.getConnectionInfo();
        if (wi != null) {
            WlanInfo wlan = new WlanInfo();
            wlan.system_time = current_time;
            wlan.signal = wi.getRssi();
            wlan.ip = wi.getIpAddress();
            wlan.linkspeed = wi.getLinkSpeed();
            if (wi.getSSID() != null)
                wlan.name = wi.getSSID();
            wlanlist.add(wlan);
//			Log.d(TAG, "wifi signal " + wlan.signal);
        }
    }

    private void doNet() {
        NetInfo ret = new NetInfo();

        if (global_last_down == TrafficStats.UNSUPPORTED || global_last_up == TrafficStats.UNSUPPORTED) {
            ret.traffic_up = TrafficStats.UNSUPPORTED;
            ret.traffic_down = TrafficStats.UNSUPPORTED;
            ret.rate_up = TrafficStats.UNSUPPORTED;
            ret.rate_down = TrafficStats.UNSUPPORTED;
        } else {
            long current_down = TrafficStats.getTotalRxBytes();
            long current_up = TrafficStats.getTotalTxBytes();

            //Rates
            long intervall = System.currentTimeMillis() - net_duration;
            ret.rate_down = Math.round((current_down - global_last_down) * 1000 / intervall);
            ret.rate_up = Math.round((current_up - global_last_up) * 1000 / intervall);
            if (ret.rate_down < 0) ret.rate_down = 0;
            if (ret.rate_up < 0) ret.rate_up = 0;
            //Traffic
            ret.traffic_down = current_down - global_last_down;
            ret.traffic_up = current_up - global_last_up;
            if (ret.traffic_down < 0) ret.traffic_down = 0;
            if (ret.traffic_up < 0) ret.traffic_up = 0;

            global_last_down = current_down;
            global_last_up = current_up;
        }

        if (mobile_global_last_down == TrafficStats.UNSUPPORTED || mobile_global_last_down == TrafficStats.UNSUPPORTED) {
            ret.mobile_traffic_up = TrafficStats.UNSUPPORTED;
            ret.mobile_traffic_down = TrafficStats.UNSUPPORTED;
            ret.mobile_rate_up = TrafficStats.UNSUPPORTED;
            ret.mobile_rate_down = TrafficStats.UNSUPPORTED;
        } else {
            long mobile_current_down = TrafficStats.getMobileRxBytes();
            long mobile_current_up = TrafficStats.getMobileTxBytes();

            //Rates
            long intervall = System.currentTimeMillis() - net_duration;
            ret.mobile_rate_down = Math.round((mobile_current_down - mobile_global_last_down) * 1000 / intervall);
            ret.mobile_rate_up = Math.round((mobile_current_up - mobile_global_last_up) * 1000 / intervall);
            if (ret.mobile_rate_down < 0) ret.mobile_rate_down = 0;
            if (ret.mobile_rate_up < 0) ret.mobile_rate_up = 0;
            //Traffic
            ret.mobile_traffic_down = mobile_current_down - mobile_global_last_down;
            ret.mobile_traffic_up = mobile_current_up - mobile_global_last_up;
            if (ret.mobile_traffic_down < 0) ret.mobile_traffic_down = 0;
            if (ret.mobile_traffic_up < 0) ret.mobile_traffic_up = 0;

            mobile_global_last_down = mobile_current_down;
            mobile_global_last_up = mobile_current_up;
        }

        net_duration = System.currentTimeMillis();
        ret.system_time = current_time;
        netlist.add(ret);
    }

    private void doDisk() {
        DiskInfo ret = new DiskInfo();
        long current_written = 0;
        long current_read = 0;

        Cmd c = new Cmd();
        c.addCommand("BUSYBOX=" + BUSYBOX);
        c.addCommand("$BUSYBOX iostat -d -z -k");
        c.execute();
        if (c.getOutput().size() > 3) {
            Matcher match;
            //First three lines are kerneltext, empty line and column desc
            for (int i = 3; i < c.getOutput().size(); i++) {
                match = IOSTAT_PATTERN.matcher(c.getOutput().get(i));
                if (match.matches()) {
                    current_read += Long.parseLong(match.group(5)) * 1000;
                    current_written += Long.parseLong(match.group(6)) * 1000;
                } else {
                    break;
                }
            }
        } else {
            Log.d(TAG, "Error while getting disk IO");
        }

        //Rates
        long intervall = System.currentTimeMillis() - disk_duration;
        if (intervall == 0)
            intervall++;
        ret.write_rate = Math.round((current_written - global_write) * 1000 / intervall);
        ret.read_rate = Math.round((current_read - global_read) * 1000 / intervall);
        if (ret.write_rate < 0 || global_write == 0) ret.write_rate = 0;
        if (ret.read_rate < 0 || global_read == 0) ret.read_rate = 0;
        //Traffic
        ret.written = current_written - global_write;
        ret.read = current_read - global_read;
        if (ret.written < 0 || global_write == 0) ret.written = 0;
        if (ret.read < 0 || global_read == 0) ret.read = 0;


        global_write = current_written;
        global_read = current_read;


        disk_duration = System.currentTimeMillis();
        ret.system_time = current_time;

        disklist.add(ret);
    }

    private long[] getCpuFrequency() {
        long[] rets = new long[DGdata.CORES];
        Cmd c = new Cmd();
        c.addCommand("BUSYBOX=" + DGdata.BUSYBOX + "");
        for (int i = 0; i < rets.length; i++)
            c.addCommand("$BUSYBOX cat /sys/devices/system/cpu/cpu" + i + "/cpufreq/scaling_cur_freq");
        c.execute();

        int i = 0;
        for (String freq : c.getOutput()) {
            rets[i++] = Long.parseLong(freq);
            if (i == rets.length)
                break;
        }
        return rets;
    }

    private void doTop(boolean domem, boolean docpu, boolean doload, boolean doapps) {
        Cmd c = new Cmd();
        c.addCommand("BUSYBOX=" + DGdata.BUSYBOX + "");
        c.addCommand("$BUSYBOX top -n1");
        c.execute();
        for (String err : c.getErrors())
            Log.d(TAG, err);
        if (c.getOutput().size() > 4) {
            int readline = 0;
            Matcher matcher;
            if (domem) {
                //Memory
                matcher = MEMORY_PATTERN.matcher(c.getOutput().get(readline));
                MemInfo mem = new MemInfo();
                if (matcher.matches()) {
                    mem.used = Integer.parseInt(matcher.group(2)) * 1024;
                    mem.free = Integer.parseInt(matcher.group(4)) * 1024;
                    mem.shared = Integer.parseInt(matcher.group(6)) * 1024;
                    mem.buff = Integer.parseInt(matcher.group(8)) * 1024;
                    mem.cached = Integer.parseInt(matcher.group(10)) * 1024;
                    mem.total = mem.used + mem.free;
                    mem.total_free = mem.free + mem.buff + mem.cached;
                    mem.usage = 100 - ((float) (mem.total_free * 100) / (float) (mem.total_free + mem.used + mem.shared));
                    mem.system_time = current_time;
                }
                memlist.add(mem);
            }
            readline++;

            CpuInfo cpu = new CpuInfo();
            cpu.system_time = current_time;
            if (docpu) {
                for (int i = 0; i < cpu.usage.length; i++) {
                    matcher = CPU_PATTERN2.matcher(c.getOutput().get(readline));
                    if (matcher.matches()) {
                        readline++;
                        cpu.user[i] = Float.parseFloat(matcher.group(2).substring(0, (matcher.group(2).length() - 1)));
                        cpu.system[i] = Float.parseFloat(matcher.group(4).substring(0, (matcher.group(4).length() - 1)));
                        cpu.nice[i] = Float.parseFloat(matcher.group(6).substring(0, (matcher.group(6).length() - 1)));
                        cpu.idle[i] = Float.parseFloat(matcher.group(8).substring(0, (matcher.group(8).length() - 1)));
                        cpu.io[i] = Float.parseFloat(matcher.group(10).substring(0, (matcher.group(10).length() - 1)));
                        cpu.usage[i] = 100 - cpu.idle[i];
                    } else {
                        cpulist.add(cpu);
                        break;
                    }
                }
            }

            if (doload) {
                //Load
                matcher = LOAD_PATTERN.matcher(c.getOutput().get(readline));
                LoadInfo load = new LoadInfo();
                if (matcher.matches()) {
                    load.first = Float.parseFloat(matcher.group(2));
                    load.second = Float.parseFloat(matcher.group(3));
                    load.third = Float.parseFloat(matcher.group(4));
                    load.system_time = current_time;
                }
                loadlist.add(load);
            }

            //Column description = 3
            readline++;
            active_apps.clear();
            if (doapps) {
                for (; readline < c.getOutput().size(); readline++) {
                    String te = c.getOutput().get(readline);
                    matcher = APP_PATTERN.matcher(te);
                    if (matcher.matches()) {
                        AppInfo temp = new AppInfo();
                        temp.cpu = Float.parseFloat(matcher.group(7));
                        if (temp.cpu > 0) {

                            temp.mem = Float.parseFloat(matcher.group(6));
                            temp.vsz = matcher.group(5);
                            temp.system_time = current_time;
                            temp.command = matcher.group(8);

                            apps.add(temp);
                            active_apps.add(temp);
                        } else {
                            break;
                        }
                    }
                }
                cpu.act_apps_cur = active_apps.size();
            }
        } else {
            Log.d(TAG, "Error while getting TOP data");
        }
    }

    public void doFreq() {
        FreqInfo freq = new FreqInfo();
        freq.cpu_frequency = getCpuFrequency();
        freq.system_time = current_time;
        freqlist.add(freq);
    }

    private class PhoneInfoListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            PhoneInfo p = new PhoneInfo();
            p.system_time = System.currentTimeMillis();
            p.gsm_signal = signalStrength.getGsmSignalStrength();
            phonelist.add(p);
        }
    }

    private class updateTask extends AsyncTask<String, Void, Boolean> {
        ArrayList<ArrayList<Integer>> layouts = new ArrayList<ArrayList<Integer>>();

        @SuppressWarnings("unchecked")
        public updateTask() {
            for (Line line : DGoverlay.getLines()) {
                layouts.add((ArrayList<Integer>) line.layout.clone());
            }
        }

        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(final Boolean ok) {
            if (db_cache_reset_required) {
                biggest_data_count = 0;
                while (cpulist.size() > 1) cpulist.remove(0);
                while (memlist.size() > 1) memlist.remove(0);
                while (loadlist.size() > 1) loadlist.remove(0);
                while (battlist.size() > 1) battlist.remove(0);
                while (netlist.size() > 1) netlist.remove(0);
                while (apps.size() > 2) apps.remove(0);
                while (spacelist.size() > 1) spacelist.remove(0);
                while (disklist.size() > 1) disklist.remove(0);
                while (pinglist.size() > 1) pinglist.remove(0);
                while (wlanlist.size() > 1) wlanlist.remove(0);
                while (phonelist.size() > 1) phonelist.remove(0);
                db_cache_reset_required = false;
            }
            if (biggest_data_count >= DB_CACHE_SIZE && dbTask_runs.getAndSet(true)) {
                boolean haveData = false;
                List<CpuInfo> dbcpulist = new LinkedList<CpuInfo>();
                List<FreqInfo> dbfreqlist = new LinkedList<FreqInfo>();
                List<MemInfo> dbmemlist = new LinkedList<MemInfo>();
                List<LoadInfo> dbloadlist = new LinkedList<LoadInfo>();
                List<BattInfo> dbbattlist = new LinkedList<BattInfo>();
                List<NetInfo> dbnetlist = new LinkedList<NetInfo>();
                List<AppInfo> dbapps = new LinkedList<AppInfo>();
                List<SpaceInfo> dbspacelist = new LinkedList<SpaceInfo>();
                List<DiskInfo> dbdisklist = new LinkedList<DiskInfo>();
                List<WlanInfo> dbwlanlist = new LinkedList<WlanInfo>();
                List<PhoneInfo> dbphonelist = new LinkedList<PhoneInfo>();
                List<PingInfo> dbpinglist = new LinkedList<PingInfo>();

                int fix = 0;
                int density = settings.getInt("database.density", 6);
                if (density == 1) fix = 1;

                if (settings.getBoolean("general.database.dobatt", false)) {
                    int battlistsize = battlist.size() - 1;
                    if (battlistsize < 0) battlistsize = 0;
                    dbbattlist = new LinkedList<BattInfo>(battlist.subList(0, battlistsize));
                    while (--battlistsize > 0) battlist.remove(0);
                    haveData = true;
                } else {
                    while (battlist.size() > 1) battlist.remove(0);
                }


                if (settings.getBoolean("general.database.docpu", false)) {
                    int size = cpulist.size();
                    if (((float) size / density) > 1) {
                        int cnt = (int) Math.floor(size / density);
                        int cpulistsize = cnt * density - fix;
                        dbcpulist = new LinkedList<CpuInfo>(cpulist.subList(0, cpulistsize));
                        while (cpulistsize-- > 0) cpulist.remove(0);
                        haveData = true;
                    }
                } else {
                    while (cpulist.size() > 1) cpulist.remove(0);
                }

                if (settings.getBoolean("general.database.dofreq", false)) {
                    int size = freqlist.size();
                    if (((float) size / density) > 1) {
                        int cnt = (int) Math.floor(size / density);
                        int freqlistsize = cnt * density - fix;
                        dbfreqlist = new LinkedList<FreqInfo>(freqlist.subList(0, freqlistsize));
                        while (freqlistsize-- > 0) freqlist.remove(0);
                        haveData = true;
                    }
                } else {
                    while (freqlist.size() > 1) freqlist.remove(0);
                }

                if (settings.getBoolean("general.database.domem", false)) {
                    int size = memlist.size();
                    if (((float) size / density) > 1) {
                        int cnt = (int) Math.floor(size / density);
                        int memlistsize = cnt * density - fix;
                        dbmemlist = new LinkedList<MemInfo>(memlist.subList(0, memlistsize));
                        while (memlistsize-- > 0) memlist.remove(0);
                        haveData = true;
                    }
                } else {
                    while (memlist.size() > 1) memlist.remove(0);
                }

                if (settings.getBoolean("general.database.doload", false)) {
                    int size = loadlist.size();
                    if (((float) size / density) > 1) {
                        int cnt = (int) Math.floor(size / density);
                        int loadlistsize = cnt * density - fix;
                        dbloadlist = new LinkedList<LoadInfo>(loadlist.subList(0, loadlistsize));
                        while (loadlistsize-- > 0) loadlist.remove(0);
                        haveData = true;
                    }
                } else {
                    while (loadlist.size() > 1) loadlist.remove(0);
                }

                if (settings.getBoolean("general.database.doapps", false)) {
                    int appsize = apps.size() - 2;
                    if (appsize < 0) appsize = 0;
                    dbapps = new LinkedList<AppInfo>(apps.subList(0, appsize));
                    while (--appsize > 0) apps.remove(0);
                    haveData = true;
                } else {
                    while (apps.size() > 2) apps.remove(0);
                }

                if (settings.getBoolean("general.database.donet", false)) {
                    int size = netlist.size();
                    if (((float) size / density) > 1) {
                        int cnt = (int) Math.floor(size / density);
                        int netlistsize = cnt * density - fix;
                        dbnetlist = new LinkedList<NetInfo>(netlist.subList(0, netlistsize));
                        while (netlistsize-- > 0) netlist.remove(0);
                        haveData = true;
                    }
                } else {
                    while (netlist.size() > 1) netlist.remove(0);
                }

                if (settings.getBoolean("general.database.dospace", false)) {
                    int size = spacelist.size();
                    if (((float) size / density) > 1) {
                        int cnt = (int) Math.floor(size / density);
                        int spacelistsize = cnt * density - fix;
                        dbspacelist = new LinkedList<SpaceInfo>(spacelist.subList(0, spacelistsize));
                        while (spacelistsize-- > 0) spacelist.remove(0);
                        haveData = true;
                    }
                } else {
                    while (spacelist.size() > 1) spacelist.remove(0);
                }

                if (settings.getBoolean("general.database.dodisk", false)) {
                    int size = disklist.size();
                    if (((float) size / density) > 1) {
                        int cnt = (int) Math.floor(size / density);
                        int disklistsize = cnt * density - fix;
                        dbdisklist = new LinkedList<DiskInfo>(disklist.subList(0, disklistsize));
                        while (disklistsize-- > 0) disklist.remove(0);
                        haveData = true;
                    }
                } else {
                    while (disklist.size() > 1) disklist.remove(0);
                }

                if (settings.getBoolean("general.database.dowifi", false)) {
                    int size = wlanlist.size();
                    if (((float) size / density) > 1) {
                        int cnt = (int) Math.floor(size / density);
                        int wlanlistsize = cnt * density - fix;
                        dbwlanlist = new LinkedList<WlanInfo>(wlanlist.subList(0, wlanlistsize));
                        while (wlanlistsize-- > 0) wlanlist.remove(0);
                        haveData = true;
                    }
                } else {
                    while (wlanlist.size() > 1) wlanlist.remove(0);
                }

                if (settings.getBoolean("general.database.dophone", false)) {
                    int size = phonelist.size();
                    if (((float) size / density) > 1) {
                        int cnt = (int) Math.floor(size / density);
                        int phonelistsize = cnt * density - fix;
                        dbphonelist = new LinkedList<PhoneInfo>(phonelist.subList(0, phonelistsize));
                        while (--phonelistsize > 0) phonelist.remove(0);
                        haveData = true;
                    }
                } else {
                    while (phonelist.size() > 1) phonelist.remove(0);
                }

                if (settings.getBoolean("general.database.doping", false)) {
                    int size = pinglist.size();
                    if (((float) size / density) > 1) {
                        int cnt = (int) Math.floor(size / density);
                        int pinglistsize = cnt * density - fix;
                        dbpinglist = new LinkedList<PingInfo>(pinglist.subList(0, pinglistsize));
                        while (pinglistsize-- > 0) pinglist.remove(0);
                        haveData = true;
                    }
                } else {
                    while (pinglist.size() > 1) pinglist.remove(0);
                }
                if (haveData) new dbTask(dbcpulist,
                        dbfreqlist,
                        dbmemlist,
                        dbloadlist,
                        dbbattlist,
                        dbnetlist,
                        dbapps,
                        dbspacelist,
                        dbdisklist,
                        dbwlanlist,
                        dbphonelist,
                        dbpinglist,
                        density).execute();
            }

            biggest_data_count = 0;

            if (cpulist.size() > biggest_data_count) biggest_data_count = cpulist.size();
            if (freqlist.size() > biggest_data_count) biggest_data_count = freqlist.size();
            if (memlist.size() > biggest_data_count) biggest_data_count = memlist.size();
            if (loadlist.size() > biggest_data_count) biggest_data_count = loadlist.size();
            if (battlist.size() > biggest_data_count) biggest_data_count = battlist.size();
            if (netlist.size() > biggest_data_count) biggest_data_count = netlist.size();
            if (apps.size() > biggest_data_count) biggest_data_count = apps.size();
            if (spacelist.size() > biggest_data_count) biggest_data_count = spacelist.size();
            if (disklist.size() > biggest_data_count) biggest_data_count = disklist.size();
            if (pinglist.size() > biggest_data_count) biggest_data_count = pinglist.size();
            if (phonelist.size() > biggest_data_count) biggest_data_count = phonelist.size();
            if (wlanlist.size() > biggest_data_count) biggest_data_count = wlanlist.size();

            updateTask_runs = false;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean domem = settings.getBoolean("general.database.domem", false);
            boolean docpu = settings.getBoolean("general.database.docpu", false);
            boolean dofreq = settings.getBoolean("general.database.dofreq", false);
            boolean doload = settings.getBoolean("general.database.doload", false);
            boolean doapps = settings.getBoolean("general.database.doapps", false);
            boolean donet = settings.getBoolean("general.database.donet", false);
            boolean dospace = settings.getBoolean("general.database.dospace", false);
            boolean dodisk = settings.getBoolean("general.database.dodisk", false);
            boolean doping = settings.getBoolean("general.database.doping", false);
            boolean dowifi = settings.getBoolean("general.database.dowifi", false);
            if (DGoverlay.screenON) {
                for (ArrayList<Integer> i : layouts) {
                    for (Integer x : i) {
                        if (x == 0) {

                        } else if (x == 1 || x == 2 || x == 3) {
                            doapps = true;
                        } else if (x == 4 || x == 5 || x == 6 || x == 7 || x == 8 || x == 9 || (x >= 44 && x <= 67)) {
                            docpu = true;
                        } else if (x == 10 || x == 11 || x == 12 || x == 13 || x == 14) {
                            domem = true;
                        } else if (x == 16 || x == 17 || x == 38 || x == 39) {
                            donet = true;
                        } else if (x == 18) {
                            doload = true;
                        } else if (x == 19) {
                            doapps = true;
                        } else if (x == 20 || x == 21 || x == 22) {
                            //mContext.registerReceiver(batteryReceiver, filter);
                            //mContext.unregisterReceiver(batteryReceiver);
                        } else if (x == 25 || x == 26) {
                            dodisk = true;
                        } else if (x == 27 || x == 40 || x == 41 || x == 42 || x == 43) {
                            dofreq = true;
                        } else if (x == 28 || x == 29 || x == 30 || x == 31) {
                            dospace = true;
                        } else if (x == 32) {
                            doping = true;
                        } else if (x == 33 || x == 35 || x == 36 || x == 37) {
                            dowifi = true;
                        } else if (x == 34) {
                            //do cell?
                        }
                    }
                }
            }

			/*if(domem) Log.d("eu.thedarken.diagnosis", "domem");
            if(docpu) Log.d("eu.thedarken.diagnosis", "docpu");
			if(dofreq) Log.d("eu.thedarken.diagnosis", "dofreq");
			if(doload) Log.d("eu.thedarken.diagnosis", "doload");
			if(doapps) Log.d("eu.thedarken.diagnosis", "doapps");
			if(donet) Log.d("eu.thedarken.diagnosis", "donet");
			if(dospace) Log.d("eu.thedarken.diagnosis", "dospace");
			if(dodisk) Log.d("eu.thedarken.diagnosis", "dodisk");
			if(doping) Log.d("eu.thedarken.diagnosis", "doping");
			if(dowifi) Log.d("eu.thedarken.diagnosis", "dowifi");*/
            current_time = System.currentTimeMillis();
            if (domem || docpu || doload || doapps) doTop(domem, docpu, doload, doapps);
            if (dofreq) doFreq();
            if (donet) doNet();
            if (dospace) doSpace();
            if (dodisk) doDisk();
            if (doping) doPing();
            if (dowifi) doWifi();
            //long global_duration = (System.currentTimeMillis() - current_time);
            //Log.d("eu.thedarken.diagnosis", String.valueOf(global_duration));
            return true;
        }
    }

    private class dbTask extends AsyncTask<String, Void, Boolean> {
        private LinkedList<CpuInfo> dbcpulist = new LinkedList<CpuInfo>();
        private LinkedList<FreqInfo> dbfreqlist = new LinkedList<FreqInfo>();
        private LinkedList<MemInfo> dbmemlist = new LinkedList<MemInfo>();
        private LinkedList<LoadInfo> dbloadlist = new LinkedList<LoadInfo>();
        private LinkedList<BattInfo> dbbattlist = new LinkedList<BattInfo>();
        private LinkedList<NetInfo> dbnetlist = new LinkedList<NetInfo>();
        private LinkedList<AppInfo> dbapps = new LinkedList<AppInfo>();
        private LinkedList<SpaceInfo> dbspacelist = new LinkedList<SpaceInfo>();
        private LinkedList<DiskInfo> dbdisklist = new LinkedList<DiskInfo>();
        private LinkedList<WlanInfo> dbwlanlist = new LinkedList<WlanInfo>();
        private LinkedList<PhoneInfo> dbphonelist = new LinkedList<PhoneInfo>();
        private LinkedList<PingInfo> dbpinglist = new LinkedList<PingInfo>();
        private int density = 1;

        public dbTask(List<CpuInfo> cpuinfolist, List<FreqInfo> freqinfolist, List<MemInfo> meminfolist, List<LoadInfo> loadinfolist,
                      List<BattInfo> battinfolist, List<NetInfo> netinfolist,
                      List<AppInfo> appinfolist, List<SpaceInfo> spaceinfolist, List<DiskInfo> diskinfolist,
                      List<WlanInfo> wlaninfolist, List<PhoneInfo> phoneinfolist, List<PingInfo> pinginfolist, int density) {
            dbcpulist.addAll(cpuinfolist);
            dbfreqlist.addAll(freqinfolist);
            dbmemlist.addAll(meminfolist);
            dbloadlist.addAll(loadinfolist);
            dbbattlist.addAll(battinfolist);
            dbnetlist.addAll(netinfolist);
            dbapps.addAll(appinfolist);
            dbspacelist.addAll(spaceinfolist);
            dbdisklist.addAll(diskinfolist);
            dbwlanlist.addAll(wlaninfolist);
            dbphonelist.addAll(phoneinfolist);
            dbpinglist.addAll(pinginfolist);
            this.density = density;
        }

        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(final Boolean ok) {
            dbTask_runs.set(false);
        }

        @Override
        protected Boolean doInBackground(String... params) {
//			Log.d(mContext.getPackageName(), "Writing to DB...");
            //Cpu data
            LinkedList<CpuInfo> c_inserts = new LinkedList<CpuInfo>();
            while (dbcpulist.size() >= this.density) {
                int cnt = this.density;
                CpuInfo avg = new CpuInfo();
                while (cnt != 0) {
                    CpuInfo temp = dbcpulist.removeFirst();
                    avg.act_apps_cur += temp.act_apps_cur;
                    for (int i = 0; i < avg.usage.length; i++) {
                        avg.idle[i] += temp.idle[i];
                        avg.nice[i] += temp.nice[i];
                        avg.system[i] += temp.system[i];
                        avg.usage[i] += temp.usage[i];
                        avg.user[i] += temp.user[i];
                        avg.io[i] += temp.io[i];
                    }
                    avg.system_time += temp.system_time;
                    avg.governor = temp.governor;
                    cnt--;
                }
                for (int i = 0; i < avg.usage.length; i++) {
                    avg.usage[i] /= this.density;
                    avg.user[i] /= this.density;
                    avg.io[i] /= this.density;
                    avg.idle[i] /= this.density;
                    avg.nice[i] /= this.density;
                    avg.system[i] /= this.density;
                }

                avg.act_apps_cur /= this.density;
                avg.system_time /= this.density;

                c_inserts.add(avg);
            }
            if (c_inserts.size() > 0) mDB.addCpus(c_inserts, false);

            //Freq data
            LinkedList<FreqInfo> f_inserts = new LinkedList<FreqInfo>();
            while (dbfreqlist.size() >= this.density) {
                int cnt = this.density;
                FreqInfo avg = new FreqInfo();
                while (cnt != 0) {
                    FreqInfo temp = dbfreqlist.removeFirst();
                    avg.system_time += temp.system_time;
                    for (int i = 0; i < avg.cpu_frequency.length; i++)
                        avg.cpu_frequency[i] += temp.cpu_frequency[i];
                    cnt--;
                }
                avg.system_time /= this.density;
                for (int i = 0; i < avg.cpu_frequency.length; i++)
                    avg.cpu_frequency[i] /= this.density;
                f_inserts.add(avg);
            }
            if (f_inserts.size() > 0) mDB.addFreqs(f_inserts, false);

            //Memory data
            LinkedList<MemInfo> m_inserts = new LinkedList<MemInfo>();
            while (dbmemlist.size() >= this.density) {
                int cnt = this.density;
                MemInfo avg = new MemInfo();
                while (cnt != 0) {
                    MemInfo temp = dbmemlist.removeFirst();
                    avg.free += temp.free;
                    avg.used += temp.used;
                    avg.shared += temp.shared;
                    avg.buff += temp.buff;
                    avg.cached += temp.cached;
                    avg.total_free += temp.total_free;
                    avg.system_time += temp.system_time;
                    avg.usage += temp.usage;
                    avg.total += temp.total;
                    cnt--;
                }
                avg.free /= this.density;
                avg.used /= this.density;
                avg.shared /= this.density;
                avg.buff /= this.density;
                avg.cached /= this.density;
                avg.total_free /= this.density;
                avg.system_time /= this.density;
                avg.usage /= this.density;
                avg.total /= this.density;
                m_inserts.add(avg);
            }
            if (m_inserts.size() > 0) mDB.addMems(m_inserts, false);

            //Load data
            LinkedList<LoadInfo> l_inserts = new LinkedList<LoadInfo>();
            while (dbloadlist.size() >= this.density) {
                int cnt = this.density;
                LoadInfo avg = new LoadInfo();
                while (cnt != 0) {
                    LoadInfo temp = dbloadlist.removeFirst();
                    avg.active_apps += temp.active_apps;
                    avg.first += temp.first;
                    avg.second += temp.second;
                    avg.system_time += temp.system_time;
                    cnt--;
                }
                avg.active_apps /= this.density;
                avg.first /= this.density;
                avg.second /= this.density;
                avg.third /= this.density;
                l_inserts.add(avg);
            }
            if (l_inserts.size() > 0) mDB.addLoads(l_inserts, false);

            //Battery data
            LinkedList<BattInfo> b_inserts = new LinkedList<BattInfo>();
            b_inserts.addAll(dbbattlist);
            if (b_inserts.size() > 0) mDB.addBats(b_inserts, false);

            //Net data
            LinkedList<NetInfo> n_inserts = new LinkedList<NetInfo>();
            while (dbnetlist.size() >= this.density) {
                int cnt = this.density;
                NetInfo avg = new NetInfo();
                while (cnt != 0) {
                    NetInfo temp = dbnetlist.removeFirst();
                    avg.rate_down += temp.rate_down;
                    avg.rate_up += temp.rate_up;
                    avg.traffic_down += temp.traffic_down;
                    avg.traffic_up += temp.traffic_up;
                    avg.mobile_rate_down += temp.mobile_rate_down;
                    avg.mobile_rate_up += temp.mobile_rate_up;
                    avg.mobile_traffic_down += temp.mobile_traffic_down;
                    avg.mobile_traffic_up += temp.mobile_traffic_up;
                    avg.system_time += temp.system_time;
                    cnt--;
                }
                avg.rate_down /= this.density;
                avg.rate_up /= this.density;
                avg.mobile_rate_down /= this.density;
                avg.mobile_rate_up /= this.density;
                avg.system_time /= this.density;
                n_inserts.add(avg);
            }
            if (n_inserts.size() > 0) mDB.addNets(n_inserts, false);

            //App data
            LinkedList<AppInfo> a_inserts = new LinkedList<AppInfo>();
            for (AppInfo a : dbapps) {
                if (a.command.contains(mContext.getPackageName())) continue;
                a_inserts.add(a);
            }
            if (a_inserts.size() > 0) mDB.addApps(a_inserts, false);

            //Space data
            LinkedList<SpaceInfo> s_inserts = new LinkedList<SpaceInfo>();
            while (dbspacelist.size() >= this.density) {
                int cnt = this.density;
                SpaceInfo avg = new SpaceInfo();
                while (cnt != 0) {
                    SpaceInfo temp = dbspacelist.removeFirst();
                    avg.system_time += temp.system_time;
                    avg.extern_total += temp.extern_total;
                    avg.extern_used += temp.extern_used;
                    avg.sdcard_total += temp.sdcard_total;
                    avg.sdcard_used += temp.sdcard_used;
                    avg.system_total += temp.system_total;
                    avg.system_used += temp.system_used;
                    avg.data_total += temp.data_total;
                    avg.data_used += temp.data_used;
                    cnt--;
                }
                avg.system_time /= this.density;
                avg.extern_total /= this.density;
                avg.extern_used /= this.density;
                avg.sdcard_total /= this.density;
                avg.sdcard_used /= this.density;
                avg.system_total /= this.density;
                avg.system_used /= this.density;
                avg.data_total /= this.density;
                avg.data_used /= this.density;
                s_inserts.add(avg);
            }
            if (s_inserts.size() > 0) mDB.addSpaces(s_inserts, false);

            //Disk data
            LinkedList<DiskInfo> d_inserts = new LinkedList<DiskInfo>();
            while (dbdisklist.size() >= this.density) {
                int cnt = this.density;
                DiskInfo avg = new DiskInfo();
                while (cnt != 0) {
                    DiskInfo temp = dbdisklist.removeFirst();
                    avg.system_time += temp.system_time;
                    avg.read += temp.read;
                    avg.written += temp.written;
                    avg.read_rate += temp.read_rate;
                    avg.write_rate += temp.write_rate;
                    cnt--;
                }
                avg.system_time /= this.density;
                avg.read_rate /= this.density;
                avg.write_rate /= this.density;
                d_inserts.add(avg);
            }
            if (d_inserts.size() > 0) mDB.addDisks(d_inserts, false);

            //Phone data
            LinkedList<PhoneInfo> p_inserts = new LinkedList<PhoneInfo>();
            p_inserts.addAll(dbphonelist);
            if (p_inserts.size() > 0) mDB.addPhones(p_inserts, false);

            //Wlan data
            LinkedList<WlanInfo> w_inserts = new LinkedList<WlanInfo>();
            while (dbwlanlist.size() >= this.density) {
                int cnt = this.density;
                WlanInfo avg = new WlanInfo();
                while (cnt != 0) {
                    WlanInfo temp = dbwlanlist.removeFirst();
                    avg.system_time += temp.system_time;
                    avg.signal += temp.signal;
                    cnt--;
                }
                avg.system_time /= this.density;
                avg.signal /= this.density;
                w_inserts.add(avg);
            }
            if (w_inserts.size() > 0) mDB.addWlans(w_inserts, false);

            //Ping data
            LinkedList<PingInfo> pi_inserts = new LinkedList<PingInfo>();
            while (dbpinglist.size() >= this.density) {
                int cnt = this.density;
                PingInfo avg = new PingInfo();
                while (cnt != 0) {
                    PingInfo temp = dbpinglist.removeFirst();
                    avg.system_time += temp.system_time;
                    avg.ping += temp.ping;
                    cnt--;
                }
                avg.system_time /= this.density;
                avg.ping /= this.density;
                pi_inserts.add(avg);
            }
            if (pi_inserts.size() > 0) mDB.addPings(pi_inserts, false);

//			Log.d(mContext.getPackageName(), "...done writing to DB!");
            return true;
        }
    }

    public class Retval {
        long total = 0;
        long used = 0;
    }
}
