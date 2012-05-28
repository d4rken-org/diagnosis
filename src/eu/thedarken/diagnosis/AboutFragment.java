package eu.thedarken.diagnosis;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class AboutFragment extends SherlockDialogFragment {
	static AboutFragment newInstance() {
		AboutFragment f = new AboutFragment();
		return f;
	}

	private int versCode;
	private String versName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			versCode = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
			versName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			versCode = 0;
			versName = "";
		}
	}
	
	void showDialog(FragmentManager fragman) {
        // Create the fragment and show it as a dialog.
    	SherlockDialogFragment show = AboutFragment.newInstance();
        show.show(fragman, "show");
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setCanceledOnTouchOutside(true);
		//FIXME
		getDialog().setTitle("Diagnosis " + versName+"("+versCode+")");
		
		View v = inflater.inflate(R.layout.about_fragment, container, false);

		Button xda = (Button) v.findViewById(R.id.xda);
		xda.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://forum.xda-developers.com/showthread.php?t=1411074"));
				startActivity(browserIntent);
			}
		});
		Button email = (Button) v.findViewById(R.id.email);
		email.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createSupportEmail();
			}
		});

		TextView text = (TextView) v.findViewById(R.id.about_text);
		text.setText(getString(R.string.about_help_text));
		
		return v;
	}
	
	private void createSupportEmail() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@thedarken.eu" });
		StringBuilder version = new StringBuilder();
		try {
			version.append(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
			version.append("(");
			version.append(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode);
			version.append(")");
		} catch (NameNotFoundException e1) {
			Log.d(getActivity().getPackageName(), "Error while getting version");
			e1.printStackTrace();
		}

		String subject = "[Diagnosis] Question/Request";
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(android.content.Intent.EXTRA_TEXT, "\n\n" +

		"Send from inside the app.\n\n" + "Debug Information:\n" + "(This is anonymous and only tells me your device + firmware version.)\n"
				+ "Diagnosis Version: " + version.toString() + "\n" + "FINGERPRINT: " + android.os.Build.FINGERPRINT + "\n");
		startActivity(Intent.createChooser(intent, ""));
	}
	

}