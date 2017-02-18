package eu.thedarken.diagnosis.pro;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DiagnosisProActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView version = (TextView) this.findViewById(R.id.vers);
        try {
            version.setText(this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            version.setText("");
        }
    }

    public void hide_icon(View v) {
        PackageManager p = getPackageManager();
        p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        Toast.makeText(getApplicationContext(), this.getString(R.string.hidden_toast), Toast.LENGTH_SHORT).show();
    }

    public void diagnosis(View v) {
        try {
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=eu.thedarken.diagnosis"));
            startActivity(marketIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}