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
        int status = 0;
        int power = 0;

        String formatTemp(int temp, boolean use_fahrenheit) {
            StringBuilder ret = new StringBuilder();
            //Log.d("eu.thedarken.diagnosis", "temp" + temp);
            float ctemp = 0;
            if (!use_fahrenheit) {
                ctemp = ((float) temp / 10);
                ret.append(String.valueOf((float) Math.round(ctemp * 1000) / 1000));
                ret.append("°C");
            } else {
                ctemp = (((((float) temp * 90) / 50) + 320) / 10);
                ret.append(String.valueOf((float) Math.round(ctemp * 1000) / 1000));
                ret.append("°F");
            }
            return ret.toString();
        }

        String getHealth() {
            switch (health) {
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

        String getStatus() {
            switch (status) {
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

        String getPower() {
            switch (power) {
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
        float[] usage = new float[DGdata.CORES];
        float[] user = new float[DGdata.CORES];
        float[] nice = new float[DGdata.CORES];
        float[] system = new float[DGdata.CORES];
        float[] idle = new float[DGdata.CORES];
        float[] io = new float[DGdata.CORES];
        int act_apps_cur = 0;
        long system_time = 0;
        String governor = "";

        public static float calcAvgCpu(float[] use) {
            float ret = 0;
            int avg = 0;
            for (int i = 0; i < use.length; i++) {
                if (use[i] != 0)
                    avg++;
                ret += use[i];
            }
            if (avg != 0)
                ret /= avg;
            ret = ((float) (Math.round(ret * 10)) / 10);
            return ret;
        }
    }

    public static class CpuTabInfo extends CpuInfo {
        float[] cpu_avg_user = new float[DGdata.CORES];
        float[] cpu_avg_system = new float[DGdata.CORES];
        float[] cpu_avg_io = new float[DGdata.CORES];
        float[] cpu_avg_total = new float[DGdata.CORES];
        float act_apps_avg = 0;
        int act_apps_max = 0;
    }

    public static class FreqInfo {
        long[] cpu_frequency = new long[DGdata.CORES];
        long system_time = 0;

        public static long calcAvgCoreFrequency(long[] freqs) {
            long ret = 0;
            int avg = 0;
            for (int i = 0; i < freqs.length; i++) {
                if (freqs[i] != 0)
                    avg++;
                ret += freqs[i];
            }
            if (avg != 0)
                ret /= avg;
            return ret;
        }
    }

    public static class FreqTabInfo extends FreqInfo {
        long[] max_obs_cpu_freq = new long[DGdata.CORES];
        long[] min_obs_cpu_freq = new long[DGdata.CORES];
        double[] avg_cpu_freq = new double[DGdata.CORES];

        public static long calcAvgCoreFrequency(double[] freqs) {
            long ret = 0;
            int avg = 0;
            for (int i = 0; i < freqs.length; i++) {
                if (freqs[i] != 0)
                    avg++;
                ret += freqs[i];
            }
            if (avg != 0)
                ret /= avg;
            return ret;
        }
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
        long mobile_traffic_up = 0;
        long mobile_traffic_down = 0;
        long mobile_rate_up = 0;
        long mobile_rate_down = 0;
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
        long mobile_peak_rate_down_last_3_hours = 0;
        long mobile_peak_rate_up_last_3_hours = 0;
        long mobile_peak_rate_down_last_24_hours = 0;
        long mobile_peak_rate_up_last_24_hours = 0;
        long mobile_traffic_last_threehour_down = 0;
        long mobile_traffic_last_threehour_up = 0;
        long mobile_traffic_last_day_down = 0;
        long mobile_traffic_last_day_up = 0;
        long mobile_traffic_last_week_down = 0;
        long mobile_traffic_last_week_up = 0;
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
            if (signal == 99) return "N/A";
            int sig = (int) (((float) signal / 31) * 100);
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
        int ip = 0;
        String name = "";
        int linkspeed = 0;

        String formatSignal(int sig) {
            int s = 100 + sig;
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