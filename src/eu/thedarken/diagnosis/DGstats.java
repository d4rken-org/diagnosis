package eu.thedarken.diagnosis;

import com.actionbarsherlock.app.SherlockFragment;

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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class DGstats extends SherlockFragment {
	private final String TAG = "eu.thedarken.diagnosis.DGstats";
	private SharedPreferences settings;
	private View mView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment and save it for the fragment to
		// use
		mView = inflater.inflate(R.layout.stats, container, false);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		settings = PreferenceManager.getDefaultSharedPreferences(this.getSherlockActivity());
	}

	@Override
	public void onResume() {
		super.onResume();
		new updateTask(this.getSherlockActivity()).execute();
	}

	private class updateTask extends AsyncTask<String, Void, Boolean> {
		private Activity mActivity;
		private ProgDialog dialog;

		private TableLayout batt_table;
		private BattTabInfo battinfo = null;
		private TextView health, chargelevel_cur, chargelevel_avg, voltage_cur, voltage_avg, voltage_max, voltage_min, action, tech, temp_cur, temp_avg, temp_max,
				temp_min;

		private TableLayout cpu_table;
		private TableLayout core_usage_table;
		private CpuTabInfo cpuinfo = null;
		private TextView max_apps, avg_apps;

		private TableLayout freq_table;
		private TableLayout core_table;
		private FreqTabInfo freqinfo = null;

		private TableLayout mem_table;
		private MemTabInfo meminfo = null;
		private TextView mem_avg;

		
		private NetTabInfo netinfo = null;
		private TableLayout net_table;
		private TextView latest_down_rate, latest_up_rate, max_down, max_up, yes_down, yes_up, traffic_last_3h_down, traffic_last_3h_up, traffic_last_day_down,
				traffic_last_day_up, traffic_last_week_down, traffic_last_week_up;
		
		private TableLayout mobile_traffic_rates_table;
		private TableLayout mobile_traffic_total_table;
		private TextView mobile_latest_down_rate, mobile_latest_up_rate, mobile_max_down, mobile_max_up, mobile_yes_down, mobile_yes_up, mobile_traffic_last_3h_down, mobile_traffic_last_3h_up, mobile_traffic_last_day_down,
		mobile_traffic_last_day_up, mobile_traffic_last_week_down, mobile_traffic_last_week_up;

		private TableLayout space_table;
		private SpaceTabInfo spaceinfo = null;
		private TextView extern_total, extern_used, sdcard_total, sdcard_used, system_total, system_used, data_total, data_used;
		private ProgressBar externalspacebar, internalspacebar, systemspacebar, dataspacebar;

		private TableLayout wlan_table;
		private WlanTabInfo wlaninfo = null;
		private TextView wlan_avg, wlan_min, wlan_max;

		private TableLayout cell_table;
		private PhoneTabInfo phoneinfo = null;
		private TextView cell_avg, cell_min, cell_max;

		private TableLayout ping_table;
		private PingTabInfo pinginfo = null;
		private TextView ping_avg, ping_min, ping_max;

		private TableLayout disk_table;
		private DiskTabInfo diskinfo = null;
		private TextView disk_write_avg, disk_read_avg, disk_write_max, disk_read_max;

		private boolean pull_batt;
		private boolean pull_cpu;
		private boolean pull_freq;
		private boolean pull_mem;
		private boolean pull_net;
		private boolean pull_space;
		private boolean pull_ping;
		private boolean pull_wlan;
		private boolean pull_phone;
		private boolean pull_disk;

		private DGdatabase db;
		
		public updateTask(Activity a) {
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

			mActivity = a;

			batt_table = (TableLayout) mView.findViewById(R.id.batt_table);
			health = (TextView) mView.findViewById(R.id.batteryhealth);
			chargelevel_cur = (TextView) mView.findViewById(R.id.chargelevel_cur);
			chargelevel_avg = (TextView) mView.findViewById(R.id.chargelevel_avg);
			voltage_cur = (TextView) mView.findViewById(R.id.batteryvoltage_cur);
			voltage_avg = (TextView) mView.findViewById(R.id.batteryvoltage_avg);
			voltage_max = (TextView) mView.findViewById(R.id.batteryvoltage_max);
			voltage_min = (TextView) mView.findViewById(R.id.batteryvoltage_min);
			action = (TextView) mView.findViewById(R.id.batteryaction);
			tech = (TextView) mView.findViewById(R.id.batterytech);
			temp_cur = (TextView) mView.findViewById(R.id.batterytemp_cur);
			temp_avg = (TextView) mView.findViewById(R.id.batterytemp_avg);
			temp_max = (TextView) mView.findViewById(R.id.batterytemp_max);
			temp_min = (TextView) mView.findViewById(R.id.batterytemp_min);

			cpu_table = (TableLayout) mView.findViewById(R.id.cpu_table);
			core_usage_table = (TableLayout) mView.findViewById(R.id.core_usage_table);
			max_apps = (TextView) mView.findViewById(R.id.cpu_max_apps);
			avg_apps = (TextView) mView.findViewById(R.id.cpu_avg_apps);
			
			freq_table = (TableLayout) mView.findViewById(R.id.freq_table);
			core_table = (TableLayout) mView.findViewById(R.id.core_table);

			mem_table = (TableLayout) mView.findViewById(R.id.mem_table);
			mem_avg = (TextView) mView.findViewById(R.id.mem_avg);

			net_table = (TableLayout) mView.findViewById(R.id.net_table);
			latest_down_rate = (TextView) mView.findViewById(R.id.latest_download_rate);
			latest_up_rate = (TextView) mView.findViewById(R.id.latest_upload_rate);
			max_down = (TextView) mView.findViewById(R.id.max_obs_download);
			max_up = (TextView) mView.findViewById(R.id.max_obs_upload);
			yes_down = (TextView) mView.findViewById(R.id.yesterday_max_download);
			yes_up = (TextView) mView.findViewById(R.id.yesterday_max_upload);
			traffic_last_3h_down = (TextView) mView.findViewById(R.id.traffic_last_threehours_down);
			traffic_last_3h_up = (TextView) mView.findViewById(R.id.traffic_last_threehours_up);
			traffic_last_day_down = (TextView) mView.findViewById(R.id.traffic_last_day_down);
			traffic_last_day_up = (TextView) mView.findViewById(R.id.traffic_last_day_up);
			traffic_last_week_down = (TextView) mView.findViewById(R.id.traffic_last_week_down);
			traffic_last_week_up = (TextView) mView.findViewById(R.id.traffic_last_week_up);

			mobile_traffic_rates_table = (TableLayout) mView.findViewById(R.id.mobile_traffic_rates);
			mobile_traffic_total_table = (TableLayout) mView.findViewById(R.id.mobile_traffic_total);
			mobile_latest_down_rate = (TextView) mView.findViewById(R.id.mobile_latest_download_rate);
			mobile_latest_up_rate = (TextView) mView.findViewById(R.id.mobile_latest_upload_rate);
			mobile_max_down = (TextView) mView.findViewById(R.id.mobile_max_obs_download);
			mobile_max_up = (TextView) mView.findViewById(R.id.mobile_max_obs_upload);
			mobile_yes_down = (TextView) mView.findViewById(R.id.mobile_yesterday_max_download);
			mobile_yes_up = (TextView) mView.findViewById(R.id.mobile_yesterday_max_upload);
			mobile_traffic_last_3h_down = (TextView) mView.findViewById(R.id.mobile_traffic_last_threehours_down);
			mobile_traffic_last_3h_up = (TextView) mView.findViewById(R.id.mobile_traffic_last_threehours_up);
			mobile_traffic_last_day_down = (TextView) mView.findViewById(R.id.mobile_traffic_last_day_down);
			mobile_traffic_last_day_up = (TextView) mView.findViewById(R.id.mobile_traffic_last_day_up);
			mobile_traffic_last_week_down = (TextView) mView.findViewById(R.id.mobile_traffic_last_week_down);
			mobile_traffic_last_week_up = (TextView) mView.findViewById(R.id.mobile_traffic_last_week_up);

			space_table = (TableLayout) mView.findViewById(R.id.space_table);
			extern_total = (TextView) mView.findViewById(R.id.extern_total);
			extern_used = (TextView) mView.findViewById(R.id.extern_used);
			sdcard_total = (TextView) mView.findViewById(R.id.sdcard_total);
			sdcard_used = (TextView) mView.findViewById(R.id.sdcard_used);
			system_total = (TextView) mView.findViewById(R.id.system_total);
			system_used = (TextView) mView.findViewById(R.id.system_used);
			data_total = (TextView) mView.findViewById(R.id.data_total);
			data_used = (TextView) mView.findViewById(R.id.data_used);
			externalspacebar = (ProgressBar) mView.findViewById(R.id.externalspacebar);
			internalspacebar = (ProgressBar) mView.findViewById(R.id.internalspacebar);
			systemspacebar = (ProgressBar) mView.findViewById(R.id.systemspacebar);
			dataspacebar = (ProgressBar) mView.findViewById(R.id.dataspacebar);

			wlan_table = (TableLayout) mView.findViewById(R.id.wlan_table);
			wlan_avg = (TextView) mView.findViewById(R.id.wlan_avg);
			wlan_min = (TextView) mView.findViewById(R.id.wlan_min);
			wlan_max = (TextView) mView.findViewById(R.id.wlan_max);

			cell_table = (TableLayout) mView.findViewById(R.id.cell_table);
			cell_avg = (TextView) mView.findViewById(R.id.cell_avg);
			cell_max = (TextView) mView.findViewById(R.id.cell_max);
			cell_min = (TextView) mView.findViewById(R.id.cell_min);

			ping_table = (TableLayout) mView.findViewById(R.id.ping_table);
			ping_avg = (TextView) mView.findViewById(R.id.ping_avg);
			ping_max = (TextView) mView.findViewById(R.id.ping_max);
			ping_min = (TextView) mView.findViewById(R.id.ping_min);

			disk_table = (TableLayout) mView.findViewById(R.id.disk_table);
			disk_write_avg = (TextView) mView.findViewById(R.id.disk_write_avg);
			disk_read_avg = (TextView) mView.findViewById(R.id.disk_read_avg);
			disk_write_max = (TextView) mView.findViewById(R.id.disk_write_max);
			disk_read_max = (TextView) mView.findViewById(R.id.disk_read_max);

			db = DGdatabase.getInstance(mActivity);
		}

		protected void onPreExecute() {
			dialog = new ProgDialog(mActivity);
			dialog.setMessage(mActivity.getString(R.string.loading_data_please_wait));
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
		}

		@Override
		protected void onPostExecute(final Boolean ok) {
			if(ok) {
				TextView nodata = (TextView) mView.findViewById(R.id.nodata);
				nodata.setVisibility(View.GONE);
			} else {
				TextView nodata = (TextView) mView.findViewById(R.id.nodata);
				nodata.setVisibility(View.VISIBLE);
			}
			
			if (pull_batt) {
				batt_table.setVisibility(View.VISIBLE);
				health.setText(battinfo.getHealth());
				chargelevel_cur.setText(String.valueOf(battinfo.level));
				chargelevel_avg.setText(String.valueOf(battinfo.batt_level_avg));
				voltage_cur.setText(String.valueOf(battinfo.voltage) + "mV");
				voltage_avg.setText(String.valueOf(Math.round(battinfo.voltage_avg)) + "mV");
				voltage_min.setText(String.valueOf(battinfo.voltage_min) + "mV");
				voltage_max.setText(String.valueOf(battinfo.voltage_max) + "mV");
				action.setText(battinfo.getStatus());
				tech.setText(battinfo.tech);
				temp_cur.setText(" " + battinfo.formatTemp(battinfo.batt_temp_cur, settings.getBoolean("layout.usefahrenheit", false)));
				temp_avg.setText(" " + battinfo.formatTemp((int) battinfo.batt_temp_avg, settings.getBoolean("layout.usefahrenheit", false)));
				temp_min.setText(" " + battinfo.formatTemp(battinfo.batt_temp_min, settings.getBoolean("layout.usefahrenheit", false)));
				temp_max.setText(" " + battinfo.formatTemp(battinfo.batt_temp_max, settings.getBoolean("layout.usefahrenheit", false)));
			} else {
				batt_table.setVisibility(View.GONE);
			}

			if (pull_cpu) {
				cpu_table.setVisibility(View.VISIBLE);
				
				max_apps.setText(String.valueOf(cpuinfo.act_apps_max));
				avg_apps.setText(String.valueOf((float) (Math.round(cpuinfo.act_apps_avg * 1000)) / 1000));
				
				core_usage_table.removeAllViews();
				
				TableRow r = new TableRow(mActivity);
				TextView label = new TextView(mActivity);
				label.setTextColor(Color.BLACK);
				label.setText(mActivity.getString(R.string.all_cores));
				label.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 9f));
				label.setGravity(Gravity.LEFT);
				r.addView(label);

				TextView total = new TextView(mActivity);
				total.setTextColor(Color.BLACK);
				total.setText(CpuTabInfo.calcAvgCpu(cpuinfo.cpu_avg_total) + "%");
				total.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
				total.setGravity(Gravity.LEFT);
				r.addView(total);
				
				TextView user = new TextView(mActivity);
				user.setTextColor(Color.BLACK);
				user.setText(CpuTabInfo.calcAvgCpu(cpuinfo.cpu_avg_user) + "%");
				user.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
				user.setGravity(Gravity.LEFT);
				r.addView(user);
				
				TextView system = new TextView(mActivity);
				system.setTextColor(Color.BLACK);
				system.setText(CpuTabInfo.calcAvgCpu(cpuinfo.cpu_avg_system) + "%");
				system.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
				system.setGravity(Gravity.LEFT);
				r.addView(system);
				
				TextView io = new TextView(mActivity);
				io.setTextColor(Color.BLACK);
				io.setText(CpuTabInfo.calcAvgCpu(cpuinfo.cpu_avg_io) + "%");
				io.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
				io.setGravity(Gravity.LEFT);
				r.addView(io);
				core_usage_table.addView(r);
				
				Log.d(TAG, ""+cpuinfo.cpu_avg_total[0]);
				if(DGmain.checkPro(mActivity)) {
					for(int i=0;i<cpuinfo.cpu_avg_total.length;i++) {
						if(cpuinfo.cpu_avg_io[i] == 0 && cpuinfo.cpu_avg_system[i] == 0 && cpuinfo.cpu_avg_user[i] == 0 && cpuinfo.cpu_avg_total[i] == 0)
							break;
						TableRow corerow = new TableRow(mActivity);
						TextView corelabel = new TextView(mActivity);
						corelabel.setTextColor(Color.BLACK);
						corelabel.setText(mActivity.getString(R.string.core)+(i+1));
						corelabel.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 9f));
						corelabel.setGravity(Gravity.LEFT);
						corerow.addView(corelabel);
						
						TextView coretotal = new TextView(mActivity);
						coretotal.setTextColor(Color.BLACK);
						coretotal.setText(String.valueOf((float)(Math.round(cpuinfo.cpu_avg_total[i]  * 10)) / 10)+"%");
						coretotal.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
						coretotal.setGravity(Gravity.LEFT);
						corerow.addView(coretotal);
						
						TextView coreuser = new TextView(mActivity);
						coreuser.setTextColor(Color.BLACK);
						coreuser.setText(String.valueOf((float)(Math.round(cpuinfo.cpu_avg_user[i]  * 10)) / 10)+"%");
						coreuser.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
						coreuser.setGravity(Gravity.LEFT);
						corerow.addView(coreuser);
						
						TextView coresystem = new TextView(mActivity);
						coresystem.setTextColor(Color.BLACK);
						coresystem.setText(String.valueOf((float)(Math.round(cpuinfo.cpu_avg_system[i]  * 10)) / 10)+"%");
						coresystem.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
						coresystem.setGravity(Gravity.LEFT);
						corerow.addView(coresystem);
						
						TextView coreio = new TextView(mActivity);
						coreio.setTextColor(Color.BLACK);
						coreio.setText(String.valueOf((float)(Math.round(cpuinfo.cpu_avg_io[i]  * 10)) / 10)+"%");
						coreio.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
						coreio.setGravity(Gravity.LEFT);
						corerow.addView(coreio);
						
						core_usage_table.addView(corerow);
					}
				}
			} else {
				cpu_table.setVisibility(View.GONE);
			}

			if (pull_freq) {
				freq_table.setVisibility(View.VISIBLE);
				core_table.removeAllViews();
				
				TableRow r = new TableRow(mActivity);
				TextView label = new TextView(mActivity);
				label.setTextColor(Color.BLACK);
				label.setText(mActivity.getString(R.string.all_cores));
				label.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 7f));
				r.addView(label);
				
				TextView min = new TextView(mActivity);
				min.setTextColor(Color.BLACK);
				min.setText(FreqTabInfo.calcAvgCoreFrequency(freqinfo.min_obs_cpu_freq ) / 1000 + " MHZ");
				min.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2f));
				r.addView(min);
				
				TextView max = new TextView(mActivity);
				max.setTextColor(Color.BLACK);
				max.setText(FreqTabInfo.calcAvgCoreFrequency(freqinfo.max_obs_cpu_freq ) / 1000 + " MHZ");
				max.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2f));
				r.addView(max);
				
				TextView avg = new TextView(mActivity);
				avg.setTextColor(Color.BLACK);
				avg.setText(FreqTabInfo.calcAvgCoreFrequency(freqinfo.avg_cpu_freq ) / 1000 + " MHZ");
				avg.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2f));
				r.addView(avg);
				core_table.addView(r);
				if(DGmain.checkPro(mActivity)) {
					for(int i=0;i<freqinfo.cpu_frequency.length;i++) {
						if(freqinfo.cpu_frequency[i] == 0)
							break;
						TableRow corerow = new TableRow(mActivity);
						TextView corelabel = new TextView(mActivity);
						corelabel.setTextColor(Color.BLACK);
						corelabel.setText(mActivity.getString(R.string.core)+(i+1));
						corelabel.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 7f));
						corerow.addView(corelabel);
						
						TextView coremin = new TextView(mActivity);
						coremin.setTextColor(Color.BLACK);
						coremin.setText(freqinfo.min_obs_cpu_freq[i] / 1000 + " MHZ");
						coremin.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2f));
						corerow.addView(coremin);
						
						TextView coremax = new TextView(mActivity);
						coremax.setTextColor(Color.BLACK);
						coremax.setText(freqinfo.max_obs_cpu_freq[i] / 1000 + " MHZ");
						coremax.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2f));
						corerow.addView(coremax);
						
						TextView coreavg = new TextView(mActivity);
						coreavg.setTextColor(Color.BLACK);
						coreavg.setText((long)(freqinfo.avg_cpu_freq[i] / 1000) + " MHZ");
						coreavg.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2f));
						corerow.addView(coreavg);
						core_table.addView(corerow);
					}
				}
			} else {
				freq_table.setVisibility(View.GONE);
			}

			if (pull_mem) {
				mem_table.setVisibility(View.VISIBLE);
				mem_avg.setText(Formatter.formatFileSize(mActivity, meminfo.avg_free_mem));
			} else {
				mem_table.setVisibility(View.GONE);
			}

			if (pull_net) {
				net_table.setVisibility(View.VISIBLE);
				latest_down_rate.setText(Formatter.formatFileSize(mActivity, netinfo.rate_down) + "/s");
				latest_up_rate.setText(Formatter.formatFileSize(mActivity, netinfo.rate_up) + "/s");
				max_down.setText(Formatter.formatFileSize(mActivity, netinfo.peak_rate_down_last_3_hours) + "/s");
				max_up.setText(Formatter.formatFileSize(mActivity, netinfo.peak_rate_up_last_3_hours) + "/s");
				yes_down.setText(Formatter.formatFileSize(mActivity, netinfo.peak_rate_down_last_24_hours) + "/s");
				yes_up.setText(Formatter.formatFileSize(mActivity, netinfo.peak_rate_up_last_24_hours) + "/s");

				traffic_last_3h_down.setText(Formatter.formatFileSize(mActivity, netinfo.traffic_last_threehour_down));
				traffic_last_3h_up.setText(Formatter.formatFileSize(mActivity, netinfo.traffic_last_threehour_up));
				traffic_last_day_down.setText(Formatter.formatFileSize(mActivity, netinfo.traffic_last_day_down));
				traffic_last_day_up.setText(Formatter.formatFileSize(mActivity, netinfo.traffic_last_day_up));
				traffic_last_week_down.setText(Formatter.formatFileSize(mActivity, netinfo.traffic_last_week_down));
				traffic_last_week_up.setText(Formatter.formatFileSize(mActivity, netinfo.traffic_last_week_up));
				
				if (DGmain.checkPro(mActivity)) {
					if (netinfo.mobile_rate_down > 0 || netinfo.mobile_rate_up > 0 || netinfo.mobile_peak_rate_down_last_3_hours > 0
							|| netinfo.mobile_peak_rate_up_last_3_hours > 0 || netinfo.mobile_peak_rate_down_last_24_hours > 0
							|| netinfo.mobile_peak_rate_up_last_24_hours > 0)
						mobile_traffic_rates_table.setVisibility(View.VISIBLE);
					else
						mobile_traffic_rates_table.setVisibility(View.GONE);
					if (netinfo.mobile_traffic_last_threehour_down > 0 || netinfo.mobile_traffic_last_threehour_up > 0
							|| netinfo.mobile_traffic_last_day_down > 0 || netinfo.mobile_traffic_last_day_up > 0 || netinfo.mobile_traffic_last_week_down > 0
							|| netinfo.mobile_traffic_last_week_up > 0)
						mobile_traffic_total_table.setVisibility(View.VISIBLE);
					else
						mobile_traffic_total_table.setVisibility(View.GONE);
					mobile_latest_down_rate.setText(Formatter.formatFileSize(mActivity, netinfo.mobile_rate_down) + "/s");
					mobile_latest_up_rate.setText(Formatter.formatFileSize(mActivity, netinfo.mobile_rate_up) + "/s");
					mobile_max_down.setText(Formatter.formatFileSize(mActivity, netinfo.mobile_peak_rate_down_last_3_hours) + "/s");
					mobile_max_up.setText(Formatter.formatFileSize(mActivity, netinfo.mobile_peak_rate_up_last_3_hours) + "/s");
					mobile_yes_down.setText(Formatter.formatFileSize(mActivity, netinfo.mobile_peak_rate_down_last_24_hours) + "/s");
					mobile_yes_up.setText(Formatter.formatFileSize(mActivity, netinfo.mobile_peak_rate_up_last_24_hours) + "/s");

					mobile_traffic_last_3h_down.setText(Formatter.formatFileSize(mActivity, netinfo.mobile_traffic_last_threehour_down));
					mobile_traffic_last_3h_up.setText(Formatter.formatFileSize(mActivity, netinfo.mobile_traffic_last_threehour_up));
					mobile_traffic_last_day_down.setText(Formatter.formatFileSize(mActivity, netinfo.mobile_traffic_last_day_down));
					mobile_traffic_last_day_up.setText(Formatter.formatFileSize(mActivity, netinfo.mobile_traffic_last_day_up));
					mobile_traffic_last_week_down.setText(Formatter.formatFileSize(mActivity, netinfo.mobile_traffic_last_week_down));
					mobile_traffic_last_week_up.setText(Formatter.formatFileSize(mActivity, netinfo.mobile_traffic_last_week_up));
				} else {
					mobile_traffic_rates_table.setVisibility(View.GONE);
					mobile_traffic_total_table.setVisibility(View.GONE);
				}
			} else {
				net_table.setVisibility(View.GONE);
			}

			if (pull_space) {
				space_table.setVisibility(View.VISIBLE);
				extern_total.setText(Formatter.formatFileSize(mActivity, spaceinfo.extern_total));
				extern_used.setText(Formatter.formatFileSize(mActivity, spaceinfo.extern_used) + " ("+mActivity.getString(R.string.avg)+" " + Formatter.formatFileSize(mActivity, spaceinfo.avg_extern_diff) + ")");
				externalspacebar.setMax(100);
				if (spaceinfo.extern_total != 0) {
					externalspacebar.setProgress(Math.round((spaceinfo.extern_used * 100 / spaceinfo.extern_total)));
				}

				sdcard_total.setText(Formatter.formatFileSize(mActivity, spaceinfo.sdcard_total));
				sdcard_used.setText(Formatter.formatFileSize(mActivity, spaceinfo.sdcard_used) + " ("+mActivity.getString(R.string.avg)+" " + Formatter.formatFileSize(mActivity, spaceinfo.avg_sdcard_diff) + ")");
				internalspacebar.setMax(100);
				if (spaceinfo.sdcard_total != 0) {
					internalspacebar.setProgress(Math.round((spaceinfo.sdcard_used * 100 / spaceinfo.sdcard_total)));
				}

				system_total.setText(Formatter.formatFileSize(mActivity, spaceinfo.system_total));
				system_used.setText(Formatter.formatFileSize(mActivity, spaceinfo.system_used) + " ("+mActivity.getString(R.string.avg)+" " + Formatter.formatFileSize(mActivity, spaceinfo.avg_system_diff) + ")");
				systemspacebar.setMax(100);
				if (spaceinfo.system_total != 0) {
					systemspacebar.setProgress(Math.round((spaceinfo.system_used * 100 / spaceinfo.system_total)));
				}

				data_total.setText(Formatter.formatFileSize(mActivity, spaceinfo.data_total));
				data_used.setText(Formatter.formatFileSize(mActivity, spaceinfo.data_used) + " ("+mActivity.getString(R.string.avg)+" " + Formatter.formatFileSize(mActivity, spaceinfo.avg_data_diff) + ")");
				dataspacebar.setMax(100);
				if (spaceinfo.data_total != 0) {
					dataspacebar.setProgress(Math.round((spaceinfo.data_used * 100 / spaceinfo.data_total)));
				}
			} else {
				space_table.setVisibility(View.GONE);
			}

			if (pull_wlan) {
				wlan_table.setVisibility(View.VISIBLE);
				wlan_avg.setText(wlaninfo.formatSignal((Math.round(wlaninfo.avg_signal * 100)) / 100));
				wlan_max.setText(wlaninfo.formatSignal(wlaninfo.max_signal));
				wlan_min.setText(wlaninfo.formatSignal(wlaninfo.min_signal));
			} else {
				wlan_table.setVisibility(View.GONE);
			}

			if (pull_phone) {
				cell_table.setVisibility(View.VISIBLE);
				cell_avg.setText(phoneinfo.formatSignal((Math.round(phoneinfo.avg_gsm_signal * 100)) / 100));
				cell_max.setText(phoneinfo.formatSignal(phoneinfo.max_gsm_signal));
				cell_min.setText(phoneinfo.formatSignal(phoneinfo.min_gsm_signal));
			} else {
				cell_table.setVisibility(View.GONE);
			}

			if (pull_ping) {
				ping_table.setVisibility(View.VISIBLE);
				ping_avg.setText(pinginfo.avg_ping + "ms");
				ping_max.setText(pinginfo.max_ping + "ms");
				ping_min.setText(pinginfo.min_ping + "ms");
			} else {
				ping_table.setVisibility(View.GONE);
			}

			if (pull_disk) {
				disk_table.setVisibility(View.VISIBLE);
				disk_write_avg.setText(Formatter.formatFileSize(mActivity, diskinfo.avg_write_rate));
				disk_read_avg.setText(Formatter.formatFileSize(mActivity, diskinfo.avg_read_rate));
				disk_write_max.setText(Formatter.formatFileSize(mActivity, diskinfo.max_write_rate));
				disk_read_max.setText(Formatter.formatFileSize(mActivity, diskinfo.max_read_rate));
			} else {
				disk_table.setVisibility(View.GONE);
			}

			dialog.dismiss();

		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean gotdata = false;
			battinfo = db.getBattTabInfo();
			if (battinfo == null) 
				pull_batt = false;
			else
				gotdata = true;


			cpuinfo = db.getCpuTabInfo();
			if (cpuinfo == null) 
				pull_cpu = false;
			else
				gotdata = true;

			freqinfo = db.getFreqTabInfo();
			if (freqinfo == null)
				pull_freq = false;
			else
				gotdata = true;

			meminfo = db.getMemTabInfo();
			if (meminfo == null)
				pull_mem = false;
			else
				gotdata = true;

			wlaninfo = db.getWlanTabInfo();
			if (wlaninfo == null)
				pull_wlan = false;
			else
				gotdata = true;

			phoneinfo = db.getPhoneTabInfo();
			if (phoneinfo == null)
				pull_phone = false;
			else
				gotdata = true;

			netinfo = db.getNetTabInfo();
			if (netinfo == null)
				pull_net = false;
			else
				gotdata = true;

			spaceinfo = db.getSpaceTabInfo();
			if (spaceinfo == null)
				pull_space = false;
			else
				gotdata = true;

			pinginfo = db.getPingTabInfo();
			if (pinginfo == null)
				pull_ping = false;
			else
				gotdata = true;

			diskinfo = db.getDiskTabInfo();
			if (diskinfo == null)
				pull_disk = false;
			else
				gotdata = true;

			return gotdata;
		}
	}
}