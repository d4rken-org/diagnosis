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
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

public class DGstats extends SherlockFragment {

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
		private CpuTabInfo cpuinfo = null;
		private TextView avg_user, avg_sys, avg_io;
		private TextView max_apps, avg_apps;

		private TableLayout freq_table;
		private FreqTabInfo freqinfo = null;
		private TextView cpu_avg, cpu_max, cpu_min, poss_cpu_max, poss_cpu_min;

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
			max_apps = (TextView) mView.findViewById(R.id.cpu_max_apps);
			avg_apps = (TextView) mView.findViewById(R.id.cpu_avg_apps);
			avg_user = (TextView) mView.findViewById(R.id.cpu_avg_user);
			avg_sys = (TextView) mView.findViewById(R.id.cpu_avg_system);
			avg_io = (TextView) mView.findViewById(R.id.cpu_avg_io);

			freq_table = (TableLayout) mView.findViewById(R.id.freq_table);
			cpu_avg = (TextView) mView.findViewById(R.id.observed_cpu_avg);
			cpu_max = (TextView) mView.findViewById(R.id.observed_cpu_max);
			cpu_min = (TextView) mView.findViewById(R.id.observed_cpu_min);
			poss_cpu_max = (TextView) mView.findViewById(R.id.possible_cpu_max);
			poss_cpu_min = (TextView) mView.findViewById(R.id.possible_cpu_min);

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
			dialog.setMessage("Loading data, please wait.");
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
				avg_apps.setText(String.valueOf((double) (Math.round(cpuinfo.act_apps_avg * 1000)) / 1000));
				avg_user.setText(String.valueOf((double) (Math.round(cpuinfo.cpu_avg_user * 100)) / 100));
				avg_sys.setText(String.valueOf((double) (Math.round(cpuinfo.cpu_avg_system * 100)) / 100));
				avg_io.setText(String.valueOf((double) (Math.round(cpuinfo.cpu_avg_io * 100)) / 100));
			} else {
				cpu_table.setVisibility(View.GONE);
			}

			if (pull_freq) {
				freq_table.setVisibility(View.VISIBLE);
				cpu_avg.setText(String.valueOf((int) (freqinfo.avg_cpu_freq / 1000)) + " MHZ");
				cpu_max.setText(String.valueOf(freqinfo.max_obs_cpu_freq / 1000) + " MHZ");
				cpu_min.setText(String.valueOf(freqinfo.min_obs_cpu_freq / 1000) + " MHZ");
				poss_cpu_max.setText(String.valueOf(freqinfo.cpu_max_frequency / 1000) + " MHZ");
				poss_cpu_min.setText(String.valueOf(freqinfo.cpu_min_frequency / 1000) + " MHZ");
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
				
				if (DGmain.checkPro(mActivity, false)) {
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
				extern_used.setText(Formatter.formatFileSize(mActivity, spaceinfo.extern_used));
				externalspacebar.setMax(100);
				if (spaceinfo.extern_total != 0) {
					externalspacebar.setProgress(Math.round((spaceinfo.extern_used * 100 / spaceinfo.extern_total)));
				}

				sdcard_total.setText(Formatter.formatFileSize(mActivity, spaceinfo.sdcard_total));
				sdcard_used.setText(Formatter.formatFileSize(mActivity, spaceinfo.sdcard_used));
				internalspacebar.setMax(100);
				if (spaceinfo.sdcard_total != 0) {
					internalspacebar.setProgress(Math.round((spaceinfo.sdcard_used * 100 / spaceinfo.sdcard_total)));
				}

				system_total.setText(Formatter.formatFileSize(mActivity, spaceinfo.system_total));
				system_used.setText(Formatter.formatFileSize(mActivity, spaceinfo.system_used));
				systemspacebar.setMax(100);
				if (spaceinfo.system_total != 0) {
					systemspacebar.setProgress(Math.round((spaceinfo.system_used * 100 / spaceinfo.system_total)));
				}

				data_total.setText(Formatter.formatFileSize(mActivity, spaceinfo.data_total));
				data_used.setText(Formatter.formatFileSize(mActivity, spaceinfo.data_used));
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