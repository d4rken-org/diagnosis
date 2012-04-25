package eu.thedarken.diagnosis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DGautostart extends BroadcastReceiver {
	private final String TAG = "eu.thedarken.diagnosis.DGautostart";
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Diagnosis autostart called");
		Intent svc = new Intent(context, DGoverlay.class);
		context.startService(svc);
	}
}