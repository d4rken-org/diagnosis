package eu.thedarken.diagnosis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import android.app.AlertDialog.Builder;

public class LinePreference extends ListPreference {
	Context mContext;
    CharSequence[] entries;
    CharSequence[] entryValues;
	public LinePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		int index = findIndexOfValue(getSharedPreferences().getString(getKey(), "1"));

        entries = getEntries();
        entryValues = getEntryValues();

        if (entries == null || entryValues == null || entries.length != entryValues.length )
        {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array which are both the same length");
        }		
		
		ListAdapter listAdapter = (ListAdapter) new LineArrayAdapter(getContext(), R.layout.lineselector, this.getEntryValues() ,index, this);

		builder.setAdapter(listAdapter, this);
		super.onPrepareDialogBuilder(builder);
	}
	
	public void setResult(int clicked)
	{

		if(this.callChangeListener(""+clicked))
		{
			Editor edit = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
			edit.putString(this.getKey(), ""+clicked);
			edit.commit();
		}
		this.getDialog().dismiss();
	}
	
	public class LineArrayAdapter extends ArrayAdapter<CharSequence> implements OnClickListener {
		int index;
		LinePreference ts;

		public LineArrayAdapter(Context context, int textViewResourceId, CharSequence[] objects, int selected, LinePreference lp) {
			super(context, textViewResourceId, objects);
			index = selected;
			this.ts = lp;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			//get themeId
			CharSequence themeId = this.getItem(position);

			//inflate layout
			LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
			View row = inflater.inflate(R.layout.lineselector, parent, false);
			row.setId(Integer.parseInt(themeId.toString()));
			row.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.lineselectorhighlist));
			//set on click listener for row
			row.setOnClickListener(this);

			//set name
			TextView tv = (TextView) row.findViewById(R.id.lineid);
			tv.setText(entries[position]);
			tv.setTextColor(0xff000000);

			//set checkbox
			RadioButton tb = (RadioButton) row.findViewById(R.id.ckbox);
			if (position == index) {
				tb.setChecked(true);
			}
			tb.setClickable(false);

			return row;
		}

		@Override
		public void onClick(View v)
		{	
			ts.setResult(v.getId());
		}
	}
}