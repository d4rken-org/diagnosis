package eu.thedarken.diagnosis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;


public class ProgDialog extends ProgressDialog {
	Context mContext;
	ProgressDialog d;
	public ProgDialog(Context context) {
		super(context);
		this.mContext = context;
		this.setCancelable(false);
		this.setIndeterminate(false);
		this.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		this.setMax(1);
		d = this;
	}
	
	@Override
	public void show() {
		if(!((Activity)mContext).isFinishing()) {
			super.show();
		}
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
		((Activity) mContext).runOnUiThread(new Runnable() {
		    public void run() {
		    	d.setMessage(message);
		    }
		});
    }
    
    public void updateTitle(String msg) {
		final String message = msg;
		((Activity) mContext).runOnUiThread(new Runnable() {
		    public void run() {
		    	d.setTitle(message);
		    }
		});
    }
}
