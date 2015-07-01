package in.principal.adapter;

import in.principal.activity.R;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAlertAdapter extends BaseAdapter{
	private List<String> listarray = null;
	private LayoutInflater mInflater = null;
	
	public CustomAlertAdapter(Activity activty, List<String> list){
		mInflater = activty.getLayoutInflater();
		this.listarray=list;
	}
	@Override
	public int getCount() {
	
		return listarray.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
      final ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.alertlistrow, parent, false);
			holder.name = (TextView) convertView.findViewById(R.id.text);
	        convertView.setTag(holder);
		}else
			holder = (ViewHolder) convertView.getTag();
		
		String datavalue=listarray.get(position);
		holder.name.setText(datavalue);
		
		return convertView;
	}
	
	private static class ViewHolder {
        TextView name;
    }

}
