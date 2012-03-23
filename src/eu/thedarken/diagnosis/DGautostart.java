package eu.thedarken.diagnosis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DGautostart extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("eu.thedarken.diagnosis", "Diagnosis autostart called");
		Intent svc = new Intent(context, DGoverlay.class);
		context.startService(svc);
	}
}