package in.principal.adapter;

import java.util.ArrayList;

import in.principal.activity.R;
import in.principal.sqlite.AdapterOverloaded;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PerfStAdapter extends ArrayAdapter<AdapterOverloaded> {
	private int resource;
	private Context context;
	private ArrayList<AdapterOverloaded> data = new ArrayList<>();
	private LayoutInflater inflater = null;

	public PerfStAdapter(Context context, int resource, ArrayList<AdapterOverloaded> listArray) {
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
			holder.txt1 = (TextView) row.findViewById(R.id.idx);
			holder.txt2 = (TextView) row.findViewById(R.id.txt);
			holder.txt3 = (TextView) row.findViewById(R.id.score);
			holder.pb = (ProgressBar) row.findViewById(R.id.avgProgress);
			holder.percentage = (TextView) row.findViewById(R.id.percent);
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
		holder.txt2.setText(listItem.getText2());
		holder.txt3.setText(listItem.getText3());

		if(listItem.getInt1()>=75){
			holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
		}else if(listItem.getInt1()>=50){
			holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
		}else{
			holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
		}
		holder.pb.setProgress(listItem.getInt1());
		holder.percentage.setText(String.valueOf(listItem.getInt1()+"%"));

		return row;
	}

	public static class RecordHolder{
		public TextView txt1;
		public TextView txt2;
		public TextView txt3;
		public ProgressBar pb;
		public TextView percentage;
	}

}
