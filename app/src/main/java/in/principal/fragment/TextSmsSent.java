package in.principal.fragment;

import java.util.ArrayList;

import in.principal.activity.R;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class TextSmsSent extends Fragment {
	private ArrayList<AdapterOverloaded> adapterList = new ArrayList<>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.text_sms_sent, container, false);

		Context context = AppGlobal.getContext();
		SQLiteDatabase sqliteDatabase = AppGlobal.getSqliteDatabase();
		ListView lv = (ListView) view.findViewById(R.id.list);
		
		Button voiceSmsBtn = (Button)view.findViewById(R.id.voiceSms);
		voiceSmsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ReplaceFragment.replace(new TextSms(), getFragmentManager());
			}
		});
		
		Cursor c = sqliteDatabase.rawQuery("select * from textsms", null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			String mes = c.getString(c.getColumnIndex("Message"));
			String dat = c.getString(c.getColumnIndex("Date"));
			String to = c.getString(c.getColumnIndex("MessageTo"));
			adapterList.add(new AdapterOverloaded(mes, dat, to));
			c.moveToNext();
		}
		c.close();

		TextSmsAdapter textSmsAdapter = new TextSmsAdapter(context, R.layout.sms_sent_list, adapterList);
		lv.setAdapter(textSmsAdapter);
		
		
		return view;
	}
	
	public class TextSmsAdapter extends ArrayAdapter<AdapterOverloaded>{
		int resource;
		Context context;
		ArrayList<AdapterOverloaded> data = new ArrayList<>();

		public TextSmsAdapter(Context context, int resource, ArrayList<AdapterOverloaded> listArray) {
			super(context, resource, listArray);
			this.context = context;
			this.resource = resource;
			this.data = listArray;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			RecordHolder holder;

			if (row == null) {
				LayoutInflater inflater = (LayoutInflater)context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(resource, parent, false);

				holder = new RecordHolder();
				holder.tv1 = (TextView) row.findViewById(R.id.message);
				holder.tv2 = (TextView) row.findViewById(R.id.date);
				holder.tv3 = (TextView) row.findViewById(R.id.to);

				row.setTag(holder);
			} else {
				holder = (RecordHolder) row.getTag();
			}

			if(position % 2 == 0){
				//	row.setBackgroundResource(R.drawable.list_selector1);
				row.setBackgroundColor(Color.rgb(255, 255, 255));
			}
			else {
				//	row.setBackgroundResource(R.drawable.list_selector2);
				row.setBackgroundColor(Color.rgb(237, 239, 242));
			}

			AdapterOverloaded listItem = data.get(position);
			holder.tv1.setText(listItem.getText1());
			holder.tv2.setText(listItem.getText2());
			holder.tv3.setText(listItem.getText3());

			return row;
		}

		class RecordHolder{
			TextView tv1;
			TextView tv2;
			TextView tv3;
		}

	}

}
