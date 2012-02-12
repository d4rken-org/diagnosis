package eu.thedarken.diagnosis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import eu.thedarken.diagnosis.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Helper {
	private Helper() {
		
	}
	public static String getTemp(int temp,boolean use_fahrenheit) {
		StringBuilder ret = new StringBuilder();
		//Log.d("eu.thedarken.diagnosis", "temp" + temp);
		float ctemp = 0;
		if(!use_fahrenheit) {
			ctemp = ((float)temp/10);
			ret.append(String.valueOf((float)Math.round(ctemp*1000)/1000));
			ret.append("°C");
		} else {
			ctemp = (((((float)temp*90)/50)+320)/10);
			ret.append(String.valueOf((float)Math.round(ctemp*1000)/1000));
			ret.append("°F");
		}
		return ret.toString();
	}
	
	public static int[] getColors(int count) {
		int[] colors = new int[count];
		int[] pool = new int[] {Color.BLUE, Color.CYAN, Color.GRAY, Color.GREEN, Color.LTGRAY, Color.MAGENTA, Color.RED, Color.YELLOW, Color.DKGRAY};
		//int[] pool = new int[] {R.color.BlueViolet, R.color.Coral, R.color.CornflowerBlue, R.color.Gold, R.color.Lime, R.color.OrangeRed, R.color.OliveDrab, R.color.Yellow, R.color.HotPink};
        for(Integer i : colors) {
        	colors[i] = pool[i];
        }
		return colors;
	}
	
	public static String writeStringToFile(String content, String name) {
    	FileWriter out;
    	File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SD_Maid");
    	dir.mkdirs();
    	String fname = dir.getAbsolutePath()+ "/" + name + "_" + System.currentTimeMillis() + ".log";
		try {
			out = new FileWriter(fname);
			out.write(content);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return fname;
	}
	public class DetailsBox extends Dialog {
		Context mContext;
		TextView text;
		String savefile = null;
		Button save;
		public DetailsBox(Context context) {
			super(context);
			mContext = context;
	    	this.setContentView(R.layout.detailsbox);
	    	text = (TextView) this.findViewById(R.id.DetailsTextView);
	        save = (Button) this.findViewById(R.id.save);
	        save.setVisibility(View.VISIBLE);
	        save.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	String ret;
	            	if((ret = Helper.writeStringToFile(text.getText().toString(), savefile)) != null) {
	                	Toast.makeText(mContext, "Saved file" + " (" + ret + ")", Toast.LENGTH_LONG).show();
	            	} else {
	            		Toast.makeText(mContext, "Error saving file", Toast.LENGTH_LONG).show();
	            	}
	            }
	        });
	        
		}
		public void setSaveFile(String name) {
			this.savefile = name;
		}
		
		public void setContent(String content) {
			this.text.setText(content);
		}
		
		public void setNoSave(boolean b) {
			if(b) this.save.setVisibility(View.GONE);
			else this.save.setVisibility(View.VISIBLE);
		}
	}
	public static class ProgDialog extends ProgressDialog {
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
	public class CheckSum {
		private Context mContext;
		public CheckSum(Context c)	 {
			mContext = c;
		}
		
		private byte[] createMD5Checksum(String filename) throws Exception
		{
			InputStream fis =  new FileInputStream(filename);
			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int numRead;
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			fis.close();
			return complete.digest();
		}

		// a byte array to a HEX string
		public String getMD5Checksum(String filename) {
			byte[] b = null;
			try {
				b = createMD5Checksum(filename);
			} catch (Exception e) {
				Log.d(mContext.getPackageName(), "(getMD5Checksum) Error while calculating md5 for " + filename);
				e.printStackTrace();
			}
			String result = getHex(b);
			return result;
		}
		
		static final String HEXES = "0123456789ABCDEF";
		private String getHex( byte [] raw ) {
			if ( raw == null ) {
				return null;
			}
			final StringBuilder hex = new StringBuilder( 2 * raw.length );
			for ( final byte b : raw ) {
				hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
			}
			return hex.toString();
		}
	}
}
