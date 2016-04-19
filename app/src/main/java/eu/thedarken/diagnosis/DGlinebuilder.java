package eu.thedarken.diagnosis;

import java.util.ArrayList;

import android.app.Activity;
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
import android.widget.Toast;

public class DGlinebuilder extends ListActivity {
    private static SharedPreferences.Editor prefEditor;
    private final String TAG = "eu.thedarken.diagnosis.DGlinebuilder";
    private SharedPreferences settings;
    private IconicAdapter adapter = null;
    private ArrayList<Integer> array = new ArrayList<Integer>();
    private int line = 99;
    private ArrayAdapter<CharSequence> itemadapter;
    private Context mContext;
    private TouchListView.DropListener onDrop = new TouchListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            Integer item = adapter.getItem(from);

            adapter.remove(item);
            adapter.insert(item, to);
        }
    };
    private TouchListView.RemoveListener onRemove = new TouchListView.RemoveListener() {
        @Override
        public void remove(int which) {
            adapter.remove(adapter.getItem(which));
        }
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.linebuilder);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = settings.edit();
        mContext = this;
        itemadapter = ArrayAdapter.createFromResource(this, R.array.infolist, R.layout.spinneritem);
        itemadapter.setDropDownViewResource(R.layout.spinnerdropdownitem);

        // retrieve the data from intent (or bundle)
        Intent in = getIntent();
        Bundle extras = in.getExtras();
        line = extras.getInt("line");
        @SuppressWarnings("unchecked")
        ArrayList<Integer> selection = (ArrayList<Integer>) ObjectSerializer.deserialize((String) extras.get("currentset"));
        if (selection != null) {
            for (Integer sel : selection) {
                array.add(sel);
            }
        }

        TouchListView tlv = (TouchListView) getListView();
        adapter = new IconicAdapter(this, array);
        this.setListAdapter(adapter);

        tlv.setDropListener(onDrop);
        tlv.setRemoveListener(onRemove);

        Spinner itemspinner = (Spinner) findViewById(R.id.spinner);
        ;

        itemspinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
                int arraypos = parent.getSelectedItemPosition();
                if (arraypos != 0) {
                    Log.d(TAG, String.valueOf(itemadapter.getItem(arraypos)));
                    if (!DGmain.checkPro(mContext) && (arraypos >= 34 && arraypos <= 67)) {
                        Toast.makeText(mContext, mContext.getString(R.string.sorry_pro_only), Toast.LENGTH_LONG).show();
                    } else {
                        adapter.insert(arraypos, adapter.getCount());
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        itemspinner.setPrompt(mContext.getString(R.string.select_an_info));
        itemspinner.setAdapter(itemadapter);
    }

    public void close(View view) {
        Intent result = new Intent();
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    class IconicAdapter extends BaseAdapter {
        private ArrayList<Integer> mItems = new ArrayList<Integer>();

        public IconicAdapter(Context context, ArrayList<Integer> items) {
            mItems = items;
        }

        private void notifiy() {
            this.notifyDataSetChanged();
            prefEditor.putString(("layout.line" + line), ObjectSerializer.serialize(array));
            prefEditor.commit();
            if (DGoverlay.getLine(line) != null) {
                DGoverlay.getLine(line).layout = array;
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

        /**
         * Use the array index as a unique id.
         */
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();

                row = inflater.inflate(R.layout.linebuilderrow2, parent, false);
            }

            TextView label = (TextView) row.findViewById(R.id.label);

            label.setText(itemadapter.getItem(array.get(position)));

            return (row);
        }
    }

}
