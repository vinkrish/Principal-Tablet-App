package in.principal.adapter;

import in.principal.activity.R;
import in.principal.model.NavDrawerItem;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavDrawerListAdapter extends BaseAdapter {
	private ArrayList<NavDrawerItem> navDrawerItems;
	private LayoutInflater mInflater = null;

	public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
		this.navDrawerItems = navDrawerItems;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {		
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		if(view == null){
			view = mInflater.inflate(R.layout.drawer_list_item, parent, false);
		}
		ImageView imgIcon = (ImageView) view.findViewById(R.id.icon);
		TextView txtTitle = (TextView) view.findViewById(R.id.title);

		imgIcon.setImageResource(navDrawerItems.get(position).getIcon());        
		txtTitle.setText(navDrawerItems.get(position).getTitle());  

		return view;
	}

}
