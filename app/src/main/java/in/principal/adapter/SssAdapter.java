package in.principal.adapter;

import java.util.ArrayList;
import java.util.List;

import in.principal.activity.R;
import in.principal.sqlite.AdapterOverloaded;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SssAdapter extends ArrayAdapter<AdapterOverloaded> {
	private int resource;
	private Context context;
	private List<AdapterOverloaded> data = new ArrayList<>();
	private LayoutInflater inflater = null;

	public SssAdapter(Context context, int resource, List<AdapterOverloaded> listArray) {
		super(context, resource, listArray);
		this.context = context;
		this.resource = resource;
		this.data = listArray;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RecordHolder holder;

		if(row == null){
			row = inflater.inflate(resource, parent, false);
			holder = new RecordHolder();
			holder.txt1 = (TextView) row.findViewById(R.id.txt1);
			holder.pb1 = (ProgressBar) row.findViewById(R.id.avgProgress);
			holder.percentage1 = (TextView) row.findViewById(R.id.percent);
			row.setTag(holder);
		}else{
			holder = (RecordHolder) row.getTag();
		}

		if(position % 2 == 0)
			row.setBackgroundResource(R.drawable.list_selector1);
		else
			row.setBackgroundResource(R.drawable.list_selector2);

		AdapterOverloaded listItem = data.get(position);
		holder.txt1.setText(listItem.getText1());

		if(listItem.getInt1()>=75){
			holder.pb1.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
		}else if(listItem.getInt1()>=50){
			holder.pb1.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
		}else{
			holder.pb1.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
		}
		holder.pb1.setProgress(listItem.getInt1());
		holder.percentage1.setText(String.valueOf(listItem.getInt1()+"%"));

		return row;
	}

	public static class RecordHolder{
		public TextView txt1;
		public ProgressBar pb1;
		public TextView percentage1;
	}

}
