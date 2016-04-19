package eu.thedarken.diagnosis;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class AboutDialogFragment extends SherlockDialogFragment {
    private int versCode;
    private String versName;

    static AboutDialogFragment newInstance() {
        AboutDialogFragment f = new AboutDialogFragment();
        return f;
    }

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
        SherlockDialogFragment show = AboutDialogFragment.newInstance();
        show.show(fragman, "about_dialog");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);
        //FIXME
        getDialog().setTitle("Diagnosis " + versName + "(" + versCode + ")");

        View v = inflater.inflate(R.layout.about_fragment, container, false);

        Button homepage = (Button) v.findViewById(R.id.homepage);
        homepage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.darken.eu"));
                startActivity(browserIntent);
            }
        });

        TextView text = (TextView) v.findViewById(R.id.about_text);
        text.setText(getString(R.string.about_help_text));

        return v;
    }

}