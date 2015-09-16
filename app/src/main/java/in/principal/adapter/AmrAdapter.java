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
public class AmrAdapter extends ArrayAdapter<AdapterOverloaded> {
    private int resource;
    private Context context;
    private ArrayList<AdapterOverloaded> data = new ArrayList<>();
    private LayoutInflater inflater = null;

    public AmrAdapter(Context context, int resource, ArrayList<AdapterOverloaded> listArray) {
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
            holder.idx = (TextView) row.findViewById(R.id.idx);
            holder.txtAbsentee = (TextView) row.findViewById(R.id.txt);
            holder.pb = (ProgressBar) row.findViewById(R.id.avgProgress);
            holder.percentage = (TextView) row.findViewById(R.id.percent);
            row.setTag(holder);
        } else holder = (RecordHolder) row.getTag();

        if (position % 2 == 0)
            row.setBackgroundResource(R.drawable.list_selector1);
        else
            row.setBackgroundResource(R.drawable.list_selector2);

        AdapterOverloaded listItem = data.get(position);
        holder.idx.setText(listItem.getText1());
        holder.txtAbsentee.setText(listItem.getText3());

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
        public TextView idx;
        public TextView txtAbsentee;
        public ProgressBar pb;
        public TextView percentage;
    }

}
