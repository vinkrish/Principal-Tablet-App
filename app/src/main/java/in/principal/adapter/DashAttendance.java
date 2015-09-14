package in.principal.adapter;

import in.principal.activity.R;
import in.principal.sqlite.AdapterOverloaded;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by vinkrish.
 */

public class DashAttendance extends ArrayAdapter<AdapterOverloaded> {
    private int resource;
    private Context context;
    private ArrayList<AdapterOverloaded> data = new ArrayList<>();
    private LayoutInflater inflater = null;

    public DashAttendance(Context context, int resource, ArrayList<AdapterOverloaded> listArray) {
        super(context, resource, listArray);
        this.context = context;
        this.resource = resource;
        this.data = listArray;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder;

        if (row == null) {
            row = inflater.inflate(resource, parent, false);
            holder = new RecordHolder();
            holder.clas1 = (TextView) row.findViewById(R.id.clas1);
            holder.clas2 = (TextView) row.findViewById(R.id.clas2);
            holder.absentee = (TextView) row.findViewById(R.id.studname);
            holder.days = (TextView) row.findViewById(R.id.days);
            holder.pb = (ProgressBar) row.findViewById(R.id.avgProgress);
            holder.percentage = (TextView) row.findViewById(R.id.percent);
            row.setTag(holder);
        } else holder = (RecordHolder) row.getTag();

        AdapterOverloaded listItem = data.get(position);
        holder.clas1.setText(listItem.getText2());
        holder.clas2.setText(listItem.getText2());
        holder.absentee.setText(listItem.getText3());
        holder.days.setText(listItem.getInt3() + "");

        if (listItem.getInt2() >= 75) {
            holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
        } else if (listItem.getInt2() >= 50) {
            holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
        } else {
            holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
        }
        holder.pb.setProgress(listItem.getInt2());
        holder.percentage.setText(String.valueOf(listItem.getInt2() + "%"));

        return row;
    }

    public static class RecordHolder {
        public TextView clas1;
        public ProgressBar pb;
        public TextView percentage;
        public TextView clas2;
        public TextView absentee;
        public TextView days;
    }

}
