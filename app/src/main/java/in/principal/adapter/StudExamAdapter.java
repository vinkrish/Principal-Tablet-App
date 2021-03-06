package in.principal.adapter;

import in.principal.activity.R;
import in.principal.sqlite.AdapterOverloaded;

import java.util.ArrayList;
import java.util.List;

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
public class StudExamAdapter extends ArrayAdapter<AdapterOverloaded> {
    private int resource;
    private List<AdapterOverloaded> data = new ArrayList<>();
    private LayoutInflater inflater = null;

    public StudExamAdapter(Context context, int resource, List<AdapterOverloaded> listArray) {
        super(context, resource, listArray);
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
            holder.txt1 = (TextView) row.findViewById(R.id.txt1);
            row.setTag(holder);
        } else holder = (RecordHolder) row.getTag();

        if (position % 2 == 0)
            row.setBackgroundResource(R.drawable.list_selector1);
        else
            row.setBackgroundResource(R.drawable.list_selector2);

        AdapterOverloaded listItem = data.get(position);
        holder.txt1.setText(listItem.getText1());

        return row;
    }

    public static class RecordHolder {
        public TextView txt1;
    }

}
