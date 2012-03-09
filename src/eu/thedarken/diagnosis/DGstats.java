package eu.thedarken.diagnosis;

import eu.thedarken.diagnosis.InfoClass.BattTabInfo;
import eu.thedarken.diagnosis.InfoClass.CpuTabInfo;
import eu.thedarken.diagnosis.InfoClass.DiskTabInfo;
import eu.thedarken.diagnosis.InfoClass.FreqTabInfo;
import eu.thedarken.diagnosis.InfoClass.MemTabInfo;
import eu.thedarken.diagnosis.InfoClass.NetTabInfo;
import eu.thedarken.diagnosis.InfoClass.PhoneTabInfo;
import eu.thedarken.diagnosis.InfoClass.PingTabInfo;
import eu.thedarken.diagnosis.InfoClass.SpaceTabInfo;
import eu.thedarken.diagnosis.InfoClass.WlanTabInfo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DGstats extends Activity {
    private Context mContext;
    private SharedPreferences settings;
    private DGdatabase db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        db = DGdatabase.getInstance(this.getApplicationContext());
        setContentView(R.layout.stats);
        //showGraph(null);
        Toast.makeText(mContext, "Enable choosen statistics in the settings!",Toast.LENGTH_LONG).show();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	new updateTask(this).execute();
    }
    
    
    private class updateTask extends AsyncTask<String, Void, Boolean> {
    	Activity mActivity;
        private ProgDialog dialog;
        
        TableLayout batt_table;
        BattTabInfo battinfo = null;
        TextView health,chargelevel_cur,chargelevel_avg,voltage_cur,voltage_avg,voltage_max,voltage_min,action,tech,temp_cur,temp_avg,temp_max,temp_min;
        
        TableLayout cpu_table;
    	CpuTabInfo cpuinfo = null;
    	TextView avg_user,avg_sys,avg_io;
    	TextView max_apps,avg_apps;
    	
        TableLayout freq_table;
    	FreqTabInfo freqinfo = null;
        TextView cpu_avg,cpu_max,cpu_min,poss_cpu_max,poss_cpu_min;
        
        TableLayout mem_table;
    	MemTabInfo meminfo = null;
    	TextView mem_avg;

        TableLayout net_table;
    	NetTabInfo netinfo = null;
    	TextView latest_down_rate,latest_up_rate,max_down,max_up,yes_down,yes_up,traffic_last_3h_down,traffic_last_3h_up,traffic_last_day_down,traffic_last_day_up,traffic_last_week_down,traffic_last_week_up;
    	
        TableLayout space_table;
    	SpaceTabInfo spaceinfo = null;
        TextView extern_total,extern_used,sdcard_total,sdcard_used,system_total,system_used,data_total,data_used;
        ProgressBar externalspacebar, internalspacebar, systemspacebar, dataspacebar;
        
        TableLayout wlan_table;
    	WlanTabInfo wlaninfo = null;
    	TextView wlan_avg,wlan_min,wlan_max;
    	
        TableLayout cell_table;
    	PhoneTabInfo phoneinfo = null;
    	TextView cell_avg,cell_min,cell_max;
    	
        TableLayout ping_table;
    	PingTabInfo pinginfo = null;
    	TextView ping_avg,ping_min,ping_max;
    	
        TableLayout disk_table;
    	DiskTabInfo diskinfo = null;
    	TextView disk_write_avg,disk_read_avg,disk_write_max,disk_read_max;
        
        boolean pull_batt;
        boolean pull_cpu;
        boolean pull_freq;
        boolean pull_mem;
        boolean pull_net;
        boolean pull_space;
        boolean pull_ping;
        boolean pull_wlan;
        boolean pull_phone;
        boolean pull_disk;
  

        public updateTask(Activity activity) {
        	pull_batt = settings.getBoolean("general.database.dobatt", false);
        	pull_cpu = settings.getBoolean("general.database.docpu", false);
        	pull_freq = settings.getBoolean("general.database.dofreq", false);
        	pull_mem = settings.getBoolean("general.database.domem", false);
        	pull_wlan = settings.getBoolean("general.database.dowifi", false);
        	pull_phone = settings.getBoolean("general.database.dophone", false);
        	pull_net = settings.getBoolean("general.database.donet", false);
        	pull_space = settings.getBoolean("general.database.dospace", false);
        	pull_ping = settings.getBoolean("general.database.doping", false);
        	pull_disk = settings.getBoolean("general.database.dodisk", false);
        	
        	mActivity = activity;
        	
            batt_table = (TableLayout)findViewById(R.id.batt_table);
        	health = (TextView) findViewById(R.id.batteryhealth);
        	chargelevel_cur = (TextView) findViewById(R.id.chargelevel_cur);
        	chargelevel_avg = (TextView) findViewById(R.id.chargelevel_avg);
        	voltage_cur = (TextView) findViewById(R.id.batteryvoltage_cur);
        	voltage_avg = (TextView) findViewById(R.id.batteryvoltage_avg);
        	voltage_max = (TextView) findViewById(R.id.batteryvoltage_max);
        	voltage_min = (TextView) findViewById(R.id.batteryvoltage_min);
        	action = (TextView) findViewById(R.id.batteryaction);
        	tech = (TextView) findViewById(R.id.batterytech);
        	temp_cur = (TextView) findViewById(R.id.batterytemp_cur);
        	temp_avg = (TextView) findViewById(R.id.batterytemp_avg);
        	temp_max = (TextView) findViewById(R.id.batterytemp_max);
        	temp_min = (TextView) findViewById(R.id.batterytemp_min);
        	
            cpu_table = (TableLayout)findViewById(R.id.cpu_table);
            max_apps = (TextView) findViewById(R.id.cpu_max_apps);
            avg_apps = (TextView) findViewById(R.id.cpu_avg_apps);
            avg_user = (TextView) findViewById(R.id.cpu_avg_user);
            avg_sys = (TextView) findViewById(R.id.cpu_avg_system);
            avg_io = (TextView) findViewById(R.id.cpu_avg_io);
            
            freq_table = (TableLayout)findViewById(R.id.freq_table);
            cpu_avg = (TextView) findViewById(R.id.observed_cpu_avg);
            cpu_max = (TextView) findViewById(R.id.observed_cpu_max);
            cpu_min = (TextView) findViewById(R.id.observed_cpu_min);
            poss_cpu_max = (TextView) findViewById(R.id.possible_cpu_max);
            poss_cpu_min = (TextView) findViewById(R.id.possible_cpu_min);
            
            mem_table = (TableLayout)findViewById(R.id.mem_table);
            mem_avg = (TextView) findViewById(R.id.mem_avg);
            
            net_table = (TableLayout)findViewById(R.id.net_table);
            latest_down_rate = (TextView) findViewById(R.id.latest_download_rate);
            latest_up_rate = (TextView) findViewById(R.id.latest_upload_rate);
            max_down = (TextView) findViewById(R.id.max_obs_download);
            max_up = (TextView) findViewById(R.id.max_obs_upload);
            yes_down = (TextView) findViewById(R.id.yesterday_max_download);
            yes_up = (TextView) findViewById(R.id.yesterday_max_upload);
            traffic_last_3h_down = (TextView) findViewById(R.id.traffic_last_threehours_down);
            traffic_last_3h_up = (TextView) findViewById(R.id.traffic_last_threehours_up);
            traffic_last_day_down = (TextView) findViewById(R.id.traffic_last_day_down);
            traffic_last_day_up = (TextView) findViewById(R.id.traffic_last_day_up);
            traffic_last_week_down = (TextView) findViewById(R.id.traffic_last_week_down);
            traffic_last_week_up = (TextView) findViewById(R.id.traffic_last_week_up);
            
            space_table = (TableLayout)findViewById(R.id.space_table);
            extern_total = (TextView) findViewById(R.id.extern_total);
            extern_used = (TextView) findViewById(R.id.extern_used);
            sdcard_total = (TextView) findViewById(R.id.sdcard_total);
            sdcard_used = (TextView) findViewById(R.id.sdcard_used);
            system_total = (TextView) findViewById(R.id.system_total);
            system_used = (TextView) findViewById(R.id.system_used);
            data_total = (TextView) findViewById(R.id.data_total);
            data_used = (TextView) findViewById(R.id.data_used);
            externalspacebar = (ProgressBar) findViewById(R.id.externalspacebar);
            internalspacebar = (ProgressBar) findViewById(R.id.internalspacebar);
            systemspacebar = (ProgressBar) findViewById(R.id.systemspacebar);
            dataspacebar = (ProgressBar) findViewById(R.id.dataspacebar);

            wlan_table = (TableLayout)findViewById(R.id.wlan_table);
            wlan_avg = (TextView) findViewById(R.id.wlan_avg);
            wlan_min = (TextView) findViewById(R.id.wlan_min);
            wlan_max = (TextView) findViewById(R.id.wlan_max);
            
            cell_table = (TableLayout)findViewById(R.id.cell_table);
            cell_avg = (TextView) findViewById(R.id.cell_avg);
            cell_max = (TextView) findViewById(R.id.cell_max);
            cell_min = (TextView) findViewById(R.id.cell_min);
            
            ping_table = (TableLayout)findViewById(R.id.ping_table);
            ping_avg = (TextView) findViewById(R.id.ping_avg);
            ping_max = (TextView) findViewById(R.id.ping_max);
            ping_min = (TextView) findViewById(R.id.ping_min);
            
            disk_table = (TableLayout)findViewById(R.id.disk_table);
            disk_write_avg = (TextView) findViewById(R.id.disk_write_avg);
            disk_read_avg = (TextView) findViewById(R.id.disk_read_avg);
            disk_write_max = (TextView) findViewById(R.id.disk_write_max);
            disk_read_max = (TextView) findViewById(R.id.disk_read_max);
            
        }

        protected void onPreExecute() {
          	dialog = new ProgDialog(mActivity);
            dialog.setMessage("Loading data, please wait.");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean ok) {
            if(pull_batt) {
	    		batt_table.setVisibility(View.VISIBLE);
	    		health.setText(battinfo.getHealth());
	    		chargelevel_cur.setText(String.valueOf(battinfo.level));
	    		chargelevel_avg.setText(String.valueOf(battinfo.batt_level_avg));
	    		voltage_cur.setText(String.valueOf(battinfo.voltage)+"mV");
	    		voltage_avg.setText(String.valueOf(Math.round(battinfo.voltage_avg))+"mV");
	    		voltage_min.setText(String.valueOf(battinfo.voltage_min)+"mV");
	    		voltage_max.setText(String.valueOf(battinfo.voltage_max)+"mV");
	    		action.setText(battinfo.getStatus());
	    		tech.setText(battinfo.tech);
	    		temp_cur.setText(" "+battinfo.formatTemp(battinfo.batt_temp_cur, settings.getBoolean("layout.usefahrenheit", false)));
	    		temp_avg.setText(" "+battinfo.formatTemp((int)battinfo.batt_temp_avg, settings.getBoolean("layout.usefahrenheit", false)));
	    		temp_min.setText(" "+battinfo.formatTemp(battinfo.batt_temp_min, settings.getBoolean("layout.usefahrenheit", false)));
	    		temp_max.setText(" "+battinfo.formatTemp(battinfo.batt_temp_max, settings.getBoolean("layout.usefahrenheit", false)));
            } else {
            	batt_table.setVisibility(View.GONE);
            }
        	
            if(pull_cpu) {
        		cpu_table.setVisibility(View.VISIBLE);
        		max_apps.setText(String.valueOf(cpuinfo.act_apps_max));
        		avg_apps.setText(String.valueOf((double)(Math.round(cpuinfo.act_apps_avg*1000))/1000));
        		avg_user.setText(String.valueOf((double)(Math.round(cpuinfo.cpu_avg_user*100))/100));
        		avg_sys.setText(String.valueOf((double)(Math.round(cpuinfo.cpu_avg_system*100))/100));
        		avg_io.setText(String.valueOf((double)(Math.round(cpuinfo.cpu_avg_io*100))/100));
            } else {
            	cpu_table.setVisibility(View.GONE);
            }
            
            if(pull_freq) {
            	freq_table.setVisibility(View.VISIBLE);
        		cpu_avg.setText(String.valueOf((int)(freqinfo.avg_cpu_freq/1000)) + " MHZ");
        		cpu_max.setText(String.valueOf(freqinfo.max_obs_cpu_freq/1000) + " MHZ");
        		cpu_min.setText(String.valueOf(freqinfo.min_obs_cpu_freq/1000) + " MHZ");
        		poss_cpu_max.setText(String.valueOf(freqinfo.cpu_max_frequency/1000) + " MHZ");
        		poss_cpu_min.setText(String.valueOf(freqinfo.cpu_min_frequency/1000) + " MHZ");
            } else {
            	freq_table.setVisibility(View.GONE);
            }
            
            if(pull_mem) {
            	mem_table.setVisibility(View.VISIBLE);
        		mem_avg.setText(Formatter.formatFileSize(mContext,meminfo.avg_free_mem));
            } else {
            	mem_table.setVisibility(View.GONE);
            }
            
            if(pull_net) {
    			net_table.setVisibility(View.VISIBLE);
    			latest_down_rate.setText(Formatter.formatFileSize(mContext,netinfo.rate_down)+"/s");
    			latest_up_rate.setText(Formatter.formatFileSize(mContext,netinfo.rate_up)+"/s");
    			max_down.setText(Formatter.formatFileSize(mContext,netinfo.peak_rate_down_last_3_hours)+"/s");
    			max_up.setText(Formatter.formatFileSize(mContext,netinfo.peak_rate_up_last_3_hours)+"/s");
    			yes_down.setText(Formatter.formatFileSize(mContext,netinfo.peak_rate_down_last_24_hours)+"/s");
    			yes_up.setText(Formatter.formatFileSize(mContext,netinfo.peak_rate_up_last_24_hours)+"/s");
    			
    			traffic_last_3h_down.setText(Formatter.formatFileSize(mContext,netinfo.traffic_last_threehour_down));
    			traffic_last_3h_up.setText(Formatter.formatFileSize(mContext,netinfo.traffic_last_threehour_up));
    			traffic_last_day_down.setText(Formatter.formatFileSize(mContext,netinfo.traffic_last_day_down));
    			traffic_last_day_up.setText(Formatter.formatFileSize(mContext,netinfo.traffic_last_day_up));
    			traffic_last_week_down.setText(Formatter.formatFileSize(mContext,netinfo.traffic_last_week_down));
    			traffic_last_week_up.setText(Formatter.formatFileSize(mContext,netinfo.traffic_last_week_up));
            } else {
            	net_table.setVisibility(View.GONE);
            }

            if(pull_space) {
            	space_table.setVisibility(View.VISIBLE);
    			extern_total.setText(Formatter.formatFileSize(mContext,spaceinfo.extern_total));
    			extern_used.setText(Formatter.formatFileSize(mContext,spaceinfo.extern_used));
    			externalspacebar.setMax(100);
    			if(spaceinfo.extern_total != 0) {
    				externalspacebar.setProgress(Math.round((spaceinfo.extern_used*100/spaceinfo.extern_total)));
    			}
    			
    			sdcard_total.setText(Formatter.formatFileSize(mContext,spaceinfo.sdcard_total));
    			sdcard_used.setText(Formatter.formatFileSize(mContext,spaceinfo.sdcard_used));
    			internalspacebar.setMax(100);
    			if(spaceinfo.sdcard_total != 0) {
    				internalspacebar.setProgress(Math.round((spaceinfo.sdcard_used*100/spaceinfo.sdcard_total)));
    			}
    			
    			system_total.setText(Formatter.formatFileSize(mContext,spaceinfo.system_total));
    			system_used.setText(Formatter.formatFileSize(mContext,spaceinfo.system_used));
    			systemspacebar.setMax(100);
    			if(spaceinfo.system_total != 0) {
    				systemspacebar.setProgress(Math.round((spaceinfo.system_used*100/spaceinfo.system_total)));
    			}
    			
    			
    			data_total.setText(Formatter.formatFileSize(mContext,spaceinfo.data_total));
    			data_used.setText(Formatter.formatFileSize(mContext,spaceinfo.data_used));
    			dataspacebar.setMax(100);
    			if(spaceinfo.data_total != 0) {
    				dataspacebar.setProgress(Math.round((spaceinfo.data_used*100/spaceinfo.data_total)));
    			}
            } else {
            	space_table.setVisibility(View.GONE);
            }
            
            if(pull_wlan) {
            	wlan_table.setVisibility(View.VISIBLE);
        		wlan_avg.setText(wlaninfo.formatSignal((Math.round(wlaninfo.avg_signal*100))/100));
        		wlan_max.setText(wlaninfo.formatSignal(wlaninfo.max_signal));
        		wlan_min.setText(wlaninfo.formatSignal(wlaninfo.min_signal));
            } else {
            	wlan_table.setVisibility(View.GONE);
            }
            
            if(pull_phone) {
            	cell_table.setVisibility(View.VISIBLE);
        		cell_avg.setText(phoneinfo.formatSignal((Math.round(phoneinfo.avg_gsm_signal*100))/100));
        		cell_max.setText(phoneinfo.formatSignal(phoneinfo.max_gsm_signal));
        		cell_min.setText(phoneinfo.formatSignal(phoneinfo.min_gsm_signal));
            } else {
            	cell_table.setVisibility(View.GONE);
            }
            
            if(pull_ping) {
            	ping_table.setVisibility(View.VISIBLE);
        		ping_avg.setText(pinginfo.avg_ping+"ms");
        		ping_max.setText(pinginfo.max_ping+"ms");
        		ping_min.setText(pinginfo.min_ping+"ms");
            } else {
            	ping_table.setVisibility(View.GONE);
            }

            if(pull_disk) {
            	disk_table.setVisibility(View.VISIBLE);
        		disk_write_avg.setText(Formatter.formatFileSize(mContext,diskinfo.avg_write_rate));
        		disk_read_avg.setText(Formatter.formatFileSize(mContext,diskinfo.avg_read_rate));
        		disk_write_max.setText(Formatter.formatFileSize(mContext,diskinfo.max_write_rate));
        		disk_read_max.setText(Formatter.formatFileSize(mContext,diskinfo.max_read_rate));
            } else {
            	disk_table.setVisibility(View.GONE);
            }
            
        	try {
	            if(dialog.isShowing()) {
	                dialog.dismiss();
	            }
	        } catch (Exception e) { }
        }
        
		@Override
		protected Boolean doInBackground(String... params) {
	    	battinfo = db.getBattTabInfo();
	    	if(battinfo == null) pull_batt = false;
	    	
	        cpuinfo = db.getCpuTabInfo();
	        if(cpuinfo == null) pull_cpu = false;
	        
	        freqinfo = db.getFreqTabInfo();
	        if(freqinfo == null) pull_freq = false;
	        
	        meminfo = db.getMemTabInfo();
	        if(meminfo == null) pull_mem = false;
	        
	        wlaninfo = db.getWlanTabInfo();
	        if(wlaninfo == null) pull_wlan = false; 
	        
	        phoneinfo = db.getPhoneTabInfo();
	        if(phoneinfo == null) pull_phone = false;
	    	
			netinfo = db.getNetTabInfo();
	    	if(netinfo == null) pull_net = false;
	    	
		    spaceinfo = db.getSpaceTabInfo();
	    	if(spaceinfo == null) pull_space = false;
	    		    	
	    	pinginfo = db.getPingTabInfo();
	    	if(pinginfo == null) pull_ping = false;
	    	
	    	diskinfo = db.getDiskTabInfo();
	    	if(diskinfo == null) pull_disk = false;
	    	
	    	return true;
		}
    }
}