package eu.thedarken.diagnosis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

public class ChangelogDialogFragment extends SherlockDialogFragment {
	static ChangelogDialogFragment newInstance() {
		ChangelogDialogFragment f = new ChangelogDialogFragment();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	void showDialog(FragmentManager fragman) {
        // Create the fragment and show it as a dialog.
		ChangelogDialogFragment show = ChangelogDialogFragment.newInstance();
        show.show(fragman, "changelog_dialog");
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setCanceledOnTouchOutside(true);
		View v = inflater.inflate(R.layout.changelog, container, false);
		getDialog().setTitle(getActivity().getString(R.string.diagnosis_changelog));
		TextView text = (TextView) v.findViewById(R.id.ChangelogTextView);
		text.setTextSize(13);
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(getActivity().getAssets().open("changelog.txt"));

			BufferedReader br = new BufferedReader(reader);
			StringBuilder buffer = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				buffer.append(line).append('\n');
			}
			text.setText(buffer.toString());
			reader.close();
		} catch (IOException e) {
			Log.d(this.getClass().getSimpleName(),"Error while reading changelog.txt");
			e.printStackTrace();
		}
		return v;
	}
}
