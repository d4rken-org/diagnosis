package eu.thedarken.diagnosis;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class MiscDialogFragments extends SherlockDialogFragment {
    final static int BUSYBOX_ERROR = 0;
    final static int DATABASE_REMOVAL = 1;
    final static int REINSTALL = 2;
    final static int NEWS = 3;

    public static MiscDialogFragments newInstance(int type) {
        MiscDialogFragments frag = new MiscDialogFragments();
        Bundle args = new Bundle();
        args.putInt("type", type);
        frag.setArguments(args);
        return frag;
    }

    public static void showDialog(FragmentManager fragman, int type) {
        // Create the fragment and show it as a dialog.
        SherlockDialogFragment show = MiscDialogFragments.newInstance(type);
        show.show(fragman, "MiscDialogFragments");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int id = getArguments().getInt("type");
        switch (id) {
            case BUSYBOX_ERROR:
                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.busybox_error)
                        .setCancelable(true)
                        .setMessage(
                                R.string.busybox_error_explanation)
                        .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        }).create();
            case DATABASE_REMOVAL:
                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.database_removal)
                        .setCancelable(true)
                        .setMessage(
                                R.string.database_removal_explanation)
                        .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
            case REINSTALL:
                return new AlertDialog.Builder(getActivity()).setTitle(R.string.error).setCancelable(true)
                        .setMessage(R.string.sorry_reinstall)
                        .setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        }).create();
            case NEWS:
                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.news)
                        .setCancelable(true)
                        .setMessage(getString(R.string.news_content))
                        .setPositiveButton(R.string.diagnosis_pro, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=eu.thedarken.diagnosis.pro"));
                                    startActivity(marketIntent);
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), R.string.no_market_application_found, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.hide, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create();
        }
        Dialog dialog = null;
        return dialog;
    }
}