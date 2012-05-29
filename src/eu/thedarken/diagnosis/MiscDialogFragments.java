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
	private String dialog_tag = "";
	public static MiscDialogFragments newInstance(int type) {
		MiscDialogFragments frag = new MiscDialogFragments();
		Bundle args = new Bundle();
		args.putInt("type", type);
		frag.setArguments(args);
		return frag;
	}
	
	public void showDialog(FragmentManager fm) {
		this.show(fm, dialog_tag);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int id = getArguments().getInt("type");
		switch (id) {
		case BUSYBOX_ERROR:
			dialog_tag = "busybox_error_dialog";
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
			dialog_tag = "database_removal_dialog";
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
			dialog_tag = "reinstall_dialog";
			return new AlertDialog.Builder(getActivity()).setTitle(R.string.error).setCancelable(true)
					.setMessage(R.string.sorry_reinstall)
					.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							getActivity().finish();
						}
					}).create();
		case NEWS:
			dialog_tag = "news_dialog";
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
		dialog_tag = "unknown_dialog";
		return dialog;
	}
}