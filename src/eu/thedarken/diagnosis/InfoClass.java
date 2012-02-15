package eu.thedarken.diagnosis;

public class InfoClass {
	public static class BattInfo {
		int scale = 100;
		int level = 0;
		int voltage = 0;
		int batt_temp_cur = 0;
		String tech = "";
		long system_time = 0;
		int health = 0;
		
		String formatTemp(int temp, boolean use_fahrenheit) {
				StringBuilder ret = new StringBuilder();
				//Log.d("eu.thedarken.diagnosis", "temp" + temp);
				float ctemp = 0;
				if(!use_fahrenheit) {
					ctemp = ((float)temp/10);
					ret.append(String.valueOf((float)Math.round(ctemp*1000)/1000));
					ret.append("°C");
				} else {
					ctemp = (((((float)temp*90)/50)+320)/10);
					ret.append(String.valueOf((float)Math.round(ctemp*1000)/1000));
					ret.append("°F");
				}
				return ret.toString();
		}
		
		String getHealth() {
			switch(health) {
				default:
					return "UNKOWN";
				case 1:
					return "UNKOWN";
				case 2:
					return "GOOD";
				case 3:
					return "OVERHEAT";
				case 4:
					return "DEAD";
				case 5:
					return "OVER VOLTAGE";
				case 6:
					return "UNSPECIFIED FAILURE";
				case 7:
					return "COLD";
			}
		}
	
		int status = 0;
		String getStatus() {
			switch(status) {
				default:
					return "UNKNOWN";
				case 1:
					return "UNKNOWN";
				case 2:
					return "CHARGING";
				case 3:
					return "DISCHARGING";
				case 4:
					return "NOT CHARGING";
				case 5:
					return "FULL";
			}
		}
		int power = 0;
		String getPower() {
			switch(power) {
				default:
					return "UNKNOWN";
				case 1:
					return "AC";
				case 2:
					return "USB";
			}
		}
	}

	public static class BattTabInfo extends BattInfo {
		float batt_level_avg = 0;
		float voltage_avg = 0;
		int voltage_min = 0;
		int voltage_max = 0;
		float batt_temp_avg = 0;
		int batt_temp_min = 0;
		int batt_temp_max = 0;
	}
	public static class AppInfo {
		String vsz = "";
		float mem = 0;
		float cpu = 0;
		String command = "";
		long system_time = 0;
	}
	
	public static class AppTabInfo extends AppInfo {
		float avg_mem = 0;
		float avg_cpu = 0;
		int seen = 0;
		String command = "";
	}
	
	public static class MemInfo {
		long free = 0;
		long total_free = 0;
		long used = 0;
		long shared = 0;
		long buff = 0;
		long cached = 0;
		long total = 0;
		float usage = 0;
		long system_time = 0;
	}
	
	public static class MemTabInfo extends MemInfo {
		long avg_free_mem = 0;
		long avg_shared_mem = 0;
		long avg_buff_mem = 0;
		long avg_cached_mem = 0;
	}
	
	public static class CpuInfo {
		float usage = 0;
		float user = 0;
		float nice = 0;
		float system = 0;
		float idle = 0;
		float io = 0;
		int act_apps_cur = 0;
		long system_time = 0;
		String governor = "";
	}
	
	public static class CpuTabInfo extends CpuInfo {
		float cpu_avg_user = 0;
		float cpu_avg_system = 0;
		float cpu_avg_io = 0;
		float act_apps_avg = 0;
		int act_apps_max = 0;
		float cpu_avg_total = 0;
	}
	
	public static class FreqInfo {
		int cpu_frequency = 0;
		int cpu_max_frequency = 0;
		int cpu_min_frequency = 0;
		long system_time = 0;
	}
	
	public static class FreqTabInfo extends FreqInfo {
		int max_obs_cpu_freq = 0;
		int min_obs_cpu_freq = 0;
		float avg_cpu_freq = 0;
	}
	
	public static class LoadInfo {
		float first = 0;
		float second = 0;
		float third = 0;
		int active_apps = 0;
		long system_time = 0;
	}
	
	public static class NetInfo {
		long traffic_up = 0;
		long traffic_down = 0;
		long rate_up = 0;
		long rate_down = 0;
		long system_time = 0;
	}
	
	public static class NetTabInfo extends NetInfo {
		long peak_rate_down_last_3_hours = 0;
		long peak_rate_up_last_3_hours = 0;
		long peak_rate_down_last_24_hours = 0;
		long peak_rate_up_last_24_hours = 0;
		long traffic_last_threehour_down = 0;
		long traffic_last_threehour_up = 0;
		long traffic_last_day_down = 0;
		long traffic_last_day_up = 0;
		long traffic_last_week_down = 0;
		long traffic_last_week_up = 0;
	}
	
	public static class SpaceInfo {
		long extern_total = 0;
		long extern_used = 0;
		long sdcard_total = 0;
		long sdcard_used = 0;
		long system_total = 0;
		long system_used = 0;
		long data_total = 0;
		long data_used = 0;
		long system_time = 0;
	}
	
	public static class SpaceTabInfo extends SpaceInfo {
		long avg_extern_diff = 0;
		long avg_sdcard_diff = 0;
		long avg_system_diff = 0;
		long avg_data_diff = 0;
	}
	
	public static class DiskInfo {
		long write_rate = 0;
		long read_rate = 0;
		long written = 0;
		long read = 0;
		long system_time = 0;
	}
	
	public static class DiskTabInfo extends DiskInfo {
		long avg_write_rate = 0;
		long avg_read_rate = 0;
		long max_write_rate = 0;
		long max_read_rate = 0;
	}
	
	public static class LineData {
		String text = "";
		int id = 0;
	}
	
	public static class PingInfo {
		long system_time = 0;
		int ping = 0;
	}

	public static class PingTabInfo extends PingInfo {
		int avg_ping = 0;
		int max_ping = 0;
		int min_ping = 0;
	}
	
	public static class PhoneInfo {
		int gsm_signal = 0;
		long system_time = 0;
		String formatSignal(int signal) {
			if(signal == 99) return "N/A";
			int sig = (int) (((float)signal/31) * 100);
			return String.valueOf(sig) + "%";
		}
	}
	public static class PhoneTabInfo extends PhoneInfo {
		float avg_gsm_signal = 0;
		int min_gsm_signal = 0;
		int max_gsm_signal = 0;
	}
	
	public static class WlanInfo {
		int signal = 0;
		long system_time = 0;
		String formatSignal(int sig) {
			int s = 100+sig;
			//if(s > 150) return "N/A";
			return String.valueOf(s) + "%";
		}
	}
	
	public static class WlanTabInfo extends WlanInfo {
		float avg_signal = 0;
		int min_signal = 0;
		int max_signal = 0;
	}
}