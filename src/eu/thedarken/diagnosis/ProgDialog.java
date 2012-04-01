package eu.thedarken.diagnosis;

import android.app.Activity;
import android.app.ProgressDialog;


public class ProgDialog extends ProgressDialog {
	Activity mActivity;
	ProgressDialog d;
	public ProgDialog(Activity activity) {
		super(activity);
		this.mActivity = activity;
		this.setCancelable(false);
		this.setIndeterminate(false);
		this.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		this.setMax(1);
		d = this;
	}
	
	@Override
	public void show() {
		//Catch race condition when activity is finishing before dialog is shown
		if(!mActivity.isFinishing()) {
			super.show();
		}
	}
	
	@Override
	public void dismiss() {
		//Catch exception in window is no longer available
		try {
			//Only dismiss if it is showing
			if (d.isShowing()) {
				super.dismiss();
			}
		} catch (Exception e) {}
	}
	
    public void incrProgress() {
    	this.incrementProgressBy(1);
    }
    
    public void incrProgress2() {
    	this.incrementSecondaryProgressBy(1);
    }
    
    public void resetProgress(int val) {
    	if(val < 0) {
    		this.setProgress(0);
    	} else {
	    	this.setProgress(val);
    	}
    }
    
    public void resetProgress2(int val) {
    	if(val < 0) {
    		this.setSecondaryProgress(0);
    	} else {
	    	this.setSecondaryProgress(val);
    	}
    }
    
    public void incrMax() {
    	this.setMax(this.getMax()+1);
    }

    public void decrMax() {
    	if(this.getMax() > 0) {
    		this.setMax(this.getMax()-1);
    	} else {
    		this.setMax(0);
    	}
    }
    
    public void decrProgress() {
    	this.setProgress(this.getProgress()-1);
    }
    
    public void updateMessage(String msg) {
		final String message = msg;
		mActivity.runOnUiThread(new Runnable() {
		    public void run() {
		    	d.setMessage(message);
		    }
		});
    }
    
    public void updateTitle(String msg) {
		final String message = msg;
		mActivity.runOnUiThread(new Runnable() {
		    public void run() {
		    	d.setTitle(message);
		    }
		});
    }
}
