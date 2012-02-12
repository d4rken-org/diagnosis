package eu.thedarken.diagnosis;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import eu.thedarken.extra.ObjectSerializer;

public class DGlinebuilder extends ListActivity {
    private SharedPreferences settings;
    private static SharedPreferences.Editor prefEditor;
	private IconicAdapter adapter=null;
	private ArrayList<Integer> array = new ArrayList<Integer>();
	private int line = 99;
	ArrayAdapter<CharSequence> itemadapter;
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.linebuilder);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = settings.edit();
        
        itemadapter = ArrayAdapter.createFromResource(this, R.array.infolist, android.R.layout.simple_spinner_item);
		itemadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
	    // retrieve the data from intent (or bundle)
	    Intent in = getIntent();
	    Bundle extras = in.getExtras();
	    line = extras.getInt("line");
	    @SuppressWarnings("unchecked")
		ArrayList<Integer> selection = (ArrayList<Integer>) ObjectSerializer.deserialize((String) extras.get("currentset"));
	    if(selection != null) {
	    	for(Integer sel : selection) {
	    		array.add(sel);
	    	}
	    }
	    
		TouchListView tlv=(TouchListView)getListView();
		adapter = new IconicAdapter(this,array);
		this.setListAdapter(adapter);
		
		tlv.setDropListener(onDrop);
		tlv.setRemoveListener(onRemove);
	}
	
	private TouchListView.DropListener onDrop=new TouchListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			Integer item=adapter.getItem(from);
				
			adapter.remove(item);
			adapter.insert(item, to);
		}
	};
	
	private TouchListView.RemoveListener onRemove=new TouchListView.RemoveListener() {
		@Override
		public void remove(int which) {
				adapter.remove(adapter.getItem(which));
		}
	};
	
	class IconicAdapter extends BaseAdapter {
		private ArrayList<Integer> mItems = new ArrayList<Integer>();
		public IconicAdapter(Context context, ArrayList<Integer> items) {
		     mItems = items;
		}
		
		private void notifiy() {
			this.notifyDataSetChanged();
		    prefEditor.putString(("layout.line" + line), ObjectSerializer.serialize(array));
		    prefEditor.commit();
		    if(DGoverlay.getLine(line) != null) {
		    	DGoverlay.getLine(line).layout = array;
		    }
		    for(Integer ar : array) {
		    	Log.d("eu.thedarken.diagnosis", "#" +ar);
		    }
		}
		
		public void insert(Integer item, int to) {
			mItems.add(to, item);
			notifiy();
		}
		
		public void remove(Integer item) {
			mItems.remove(item);
			notifiy();
		}
		
		public int getCount() {
		     return mItems.size();
		}

		public Integer getItem(int position) {
		     return mItems.get(position);
		}

		 /** Use the array index as a unique id. */
		public long getItemId(int position) {
		     return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row=convertView;
			
			if (row==null) {													
				LayoutInflater inflater=getLayoutInflater();
				
				row=inflater.inflate(R.layout.linebuilderrow2, parent, false);
			}
			
			TextView label=(TextView)row.findViewById(R.id.label);
			
			label.setText(itemadapter.getItem(array.get(position)));
			
			return(row);
		}
	}
	
	private Dialog viewDialog;
	private boolean fired_init = false;
	public void add(View view) {
    	viewDialog = new Dialog(this);
		//viewDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		viewDialog.setTitle("Select item");

		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = li.inflate(R.layout.addinfospinner, null);
		viewDialog.setContentView(dialogView);

		fired_init = false;
		Spinner itemspinner = (Spinner) dialogView.findViewById(R.id.infospinner);;

		itemspinner.setOnItemSelectedListener(new OnItemSelectedListener() {
    		public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
    			if(!fired_init) {
    				fired_init = true;
    			} else {
    				if(parent.getSelectedItemPosition() != 0) {
    					adapter.insert(parent.getSelectedItemPosition(),adapter.getCount());
		    			adapter.notifyDataSetChanged();
    				}
	    			fired_init = false;
	    			viewDialog.dismiss();
    			}
    		}
    		public void onNothingSelected(AdapterView<?> arg0) {
    			viewDialog.dismiss();
    		}
		});
		itemspinner.setPrompt("Select an info");
		itemspinner.setAdapter(itemadapter);
		viewDialog.setContentView(dialogView);
		viewDialog.show();
		itemspinner.performClick();
	}
	
	public void close(View view) {
	    Intent result = new Intent();
	    setResult(Activity.RESULT_OK, result);
	    finish();
	}
    
 
}
